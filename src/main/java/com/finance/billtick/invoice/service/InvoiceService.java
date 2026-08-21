package com.finance.billtick.invoice.service;

import com.finance.billtick.business.model.Business;
import com.finance.billtick.business.repository.BusinessRepository;
import com.finance.billtick.customer.model.Customer;
import com.finance.billtick.customer.repository.CustomerRepository;
import com.finance.billtick.exception.DuplicateResourceException;
import com.finance.billtick.exception.InvalidInvoiceStateException;
import com.finance.billtick.exception.InvalidPricingException;
import com.finance.billtick.exception.ResourceNotFoundException;
import com.finance.billtick.invoice.dto.InvoiceItemRequest;
import com.finance.billtick.invoice.dto.InvoiceRequest;
import com.finance.billtick.invoice.dto.InvoiceResponse;
import com.finance.billtick.invoice.mapper.InvoiceMapper;
import com.finance.billtick.invoice.model.Invoice;
import com.finance.billtick.invoice.model.InvoiceItem;
import com.finance.billtick.invoice.model.InvoiceStatus;
import com.finance.billtick.invoice.repository.InvoiceRepository;
import com.finance.billtick.product.model.Product;
import com.finance.billtick.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final int AMOUNT_SCALE = 2;
    private static final int SEQUENCE_PADDING = 4;
    private static final int MAX_NUMBER_RETRIES = 5;
    private static final Pattern DIGITS = Pattern.compile("\\d+");

    private final InvoiceRepository invoiceRepository;
    private final BusinessRepository businessRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final InvoiceMapper invoiceMapper;

    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest invoiceRequest) {
        // 1. Load the customer (anchor for business, terms, exemption).
        Customer customer = assertCustomer(invoiceRequest.getCustomerId());

        // 2. Derive the business from the customer (tenancy is implied, never sent by the client).
        Business business = customer.getBusiness();
        if (business == null) {
            throw new InvalidInvoiceStateException(
                    "Customer with id: " + customer.getId() + " is not linked to a business");
        }

        // 3. Resolve the issue date (server owns the default).
        LocalDate issueDate = invoiceRequest.getIssueDate() != null
                ? invoiceRequest.getIssueDate()
                : LocalDate.now();

        // 4. Derive the due date from payment terms.
        LocalDate dueDate = deriveDueDate(customer, business, issueDate);

        // 5. + 7. Assemble the invoice with server-owned fields; snapshot the tax rate.
        Invoice invoice = new Invoice();
        invoice.setBusiness(business);
        invoice.setCustomer(customer);
        invoice.setIssueDate(issueDate);
        invoice.setDueDate(dueDate);
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setTaxRate(business.getSalesTaxRate() != null ? business.getSalesTaxRate() : BigDecimal.ZERO);

        // 8. Build the line items (resolve price, verify product ownership).
        applyItems(invoice, business, invoiceRequest.getItems());

        // 9. Compute totals; a line is taxed only if taxable AND the customer is not exempt.
        applyTotals(invoice, isCustomerExempt(customer, issueDate));

        // 6. + 10. Assign a unique invoice number and persist (retry on collision).
        return saveWithUniqueNumber(invoice, business, issueDate.getYear());
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceMapper.toInvoiceResponseList(invoiceRepository.findAll());
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(Long id) {
        return invoiceMapper.toInvoiceResponse(assertInvoice(id));
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAllInvoicesForBusiness(Long businessId) {
        return invoiceMapper.toInvoiceResponseList(invoiceRepository.findByBusiness(assertBusiness(businessId)));
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAllInvoicesForCustomer(Long customerId) {
        return invoiceMapper.toInvoiceResponseList(invoiceRepository.findByCustomer(assertCustomer(customerId)));
    }

    // ---- create helpers -------------------------------------------------

    private void applyItems(Invoice invoice, Business business, List<InvoiceItemRequest> itemRequests) {
        List<InvoiceItem> items = new ArrayList<>();
        for (InvoiceItemRequest itemRequest : itemRequests) {
            InvoiceItem item = invoiceMapper.toInvoiceItem(itemRequest);
            Product product = assertProduct(itemRequest.getProductId());
            assertProductBelongsToBusiness(business, product);
            item.setProduct(product);
            item.setUnitPrice(resolveUnitPrice(product, itemRequest));
            item.setInvoice(invoice);
            items.add(item);
        }
        invoice.getItems().clear();
        invoice.getItems().addAll(items);
    }

    private void applyTotals(Invoice invoice, boolean customerExempt) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;
        for (InvoiceItem item : invoice.getItems()) {
            BigDecimal lineTotal = item.getQuantity().multiply(item.getUnitPrice())
                    .setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
            boolean effectiveTaxable = Boolean.TRUE.equals(item.getTaxable()) && !customerExempt;
            BigDecimal lineTax = effectiveTaxable
                    ? lineTotal.multiply(invoice.getTaxRate()).divide(HUNDRED, AMOUNT_SCALE, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO.setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
            item.setLineTotal(lineTotal);
            item.setLineTax(lineTax);
            subtotal = subtotal.add(lineTotal);
            taxAmount = taxAmount.add(lineTax);
        }
        invoice.setSubtotal(subtotal.setScale(AMOUNT_SCALE, RoundingMode.HALF_UP));
        invoice.setTaxAmount(taxAmount.setScale(AMOUNT_SCALE, RoundingMode.HALF_UP));
        invoice.setTotal(subtotal.add(taxAmount).setScale(AMOUNT_SCALE, RoundingMode.HALF_UP));
    }

    private BigDecimal resolveUnitPrice(Product product, InvoiceItemRequest itemRequest) {
        BigDecimal price = product.getSellingPrice() != null
                ? product.getSellingPrice()
                : itemRequest.getUnitPrice();
        if (price == null) {
            throw new InvalidPricingException("No price available for product with id: " + product.getId());
        }
        return price;
    }

    private LocalDate deriveDueDate(Customer customer, Business business, LocalDate issueDate) {
        String terms = customer.getPaymentTerms() != null ? customer.getPaymentTerms() : business.getDefaultTerms();
        return issueDate.plusDays(parseTermDays(terms));
    }

    private long parseTermDays(String terms) {
        if (terms == null) {
            return 0;
        }
        Matcher matcher = DIGITS.matcher(terms);
        return matcher.find() ? Long.parseLong(matcher.group()) : 0;
    }

    private boolean isCustomerExempt(Customer customer, LocalDate onDate) {
        if (!Boolean.TRUE.equals(customer.getTaxExempt())) {
            return false;
        }
        LocalDate expiry = customer.getExemptionExpiryDate();
        return expiry == null || !expiry.isBefore(onDate);
    }

    private InvoiceResponse saveWithUniqueNumber(Invoice invoice, Business business, int year) {
        for (int attempt = 1; ; attempt++) {
            invoice.setInvoiceNumber(generateInvoiceNumber(business, year));
            try {
                return invoiceMapper.toInvoiceResponse(invoiceRepository.saveAndFlush(invoice));
            } catch (DataIntegrityViolationException ex) {
                if (attempt >= MAX_NUMBER_RETRIES) {
                    throw new DuplicateResourceException(
                            "Could not allocate a unique invoice number, please retry");
                }
                // Another transaction took the number; recompute and try again.
            }
        }
    }

    private String generateInvoiceNumber(Business business, int year) {
        String prefix = business.getInvoicePrefix() + "-" + year + "-";
        int nextSequence = invoiceRepository
                .findFirstByBusinessAndInvoiceNumberStartingWithOrderByInvoiceNumberDesc(business, prefix)
                .map(latest -> parseSequence(latest.getInvoiceNumber(), prefix) + 1)
                .orElse(1);
        return prefix + String.format("%0" + SEQUENCE_PADDING + "d", nextSequence);
    }

    private int parseSequence(String invoiceNumber, String prefix) {
        try {
            return Integer.parseInt(invoiceNumber.substring(prefix.length()));
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    // ---- shared lookups -------------------------------------------------

    private Invoice assertInvoice(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    }

    private Business assertBusiness(Long businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + businessId));
    }

    private Customer assertCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
    }

    private Product assertProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }

    private void assertProductBelongsToBusiness(Business business, Product product) {
        if (product.getBusiness() == null || !product.getBusiness().getId().equals(business.getId())) {
            throw new InvalidInvoiceStateException("Product with id: " + product.getId()
                    + " does not belong to business with id: " + business.getId());
        }
    }
}
