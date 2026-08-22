package com.finance.billtick.invoice.service;

import com.finance.billtick.business.model.Business;
import com.finance.billtick.business.repository.BusinessRepository;
import com.finance.billtick.customer.model.Customer;
import com.finance.billtick.customer.repository.CustomerRepository;
import com.finance.billtick.exception.DuplicateResourceException;
import com.finance.billtick.exception.InvalidInvoiceStateException;
import com.finance.billtick.exception.InvalidPricingException;
import com.finance.billtick.exception.ResourceInUseException;
import com.finance.billtick.exception.ResourceNotFoundException;
import com.finance.billtick.invoice.dto.InvoiceItemRequest;
import com.finance.billtick.invoice.dto.InvoicePatchRequest;
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
    private static final int SEQUENCE_WIDTH = 4;
    private static final Pattern TERM_DAYS = Pattern.compile("([0-9]+)");

    private final InvoiceRepository invoiceRepository;
    private final BusinessRepository businessRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final InvoiceMapper invoiceMapper;

    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest invoiceRequest) {
        Customer customer = assertCustomer(invoiceRequest.getCustomerId());
        Business business = assertCustomerBusiness(customer);
        LocalDate issueDate = invoiceRequest.getIssueDate() != null
                ? invoiceRequest.getIssueDate()
                : LocalDate.now();

        Invoice invoice = new Invoice();
        invoice.setBusiness(business);
        invoice.setCustomer(customer);
        invoice.setIssueDate(issueDate);
        invoice.setDueDate(deriveDueDate(customer, business, issueDate));
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setTaxRate(business.getSalesTaxRate() != null ? business.getSalesTaxRate() : BigDecimal.ZERO);
        invoice.setInvoiceNumber(generateInvoiceNumber(business, issueDate.getYear()));

        applyCreateItems(invoice, invoiceRequest.getItems());
        applyTotals(invoice, isCustomerExempt(customer, issueDate));
        return invoiceMapper.toInvoiceResponse(invoiceRepository.saveAndFlush(invoice));
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceMapper.toInvoiceResponseList(invoiceRepository.findAll());
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(Long id) {
        return invoiceMapper.toInvoiceResponse(assertInvoice(id));
    }

//    @Transactional
//    public InvoiceResponse updateInvoice(Long id, InvoiceRequest invoiceRequest) {
//        Invoice invoice = assertInvoice(id);
//        assertMutable(invoice);
//        Business business = assertBusiness(invoiceRequest.getBusinessId());
//        Customer customer = assertCustomer(invoiceRequest.getCustomerId());
//        assertCustomerBelongsToBusiness(business, customer);
//        assertUniqueInvoiceNumber(invoiceRequest.getInvoiceNumber(), id);
//        invoiceMapper.updateInvoice(invoiceRequest, invoice);
//        invoice.setBusiness(business);
//        invoice.setCustomer(customer);
//        applyItems(invoice, invoiceRequest.getItems());
//        applyTotals(invoice, isCustomerExempt(customer, invoice.getIssueDate()));
//        return invoiceMapper.toInvoiceResponse(invoiceRepository.save(invoice));
//    }

//    @Transactional
//    public InvoiceResponse patchInvoice(Long id, InvoicePatchRequest invoicePatchRequest) {
//        Invoice invoice = assertInvoice(id);
//        if (!isStatusOnlyPatch(invoicePatchRequest)) {
//            assertMutable(invoice);
//        }
//        assertUniqueInvoiceNumber(invoicePatchRequest.getInvoiceNumber(), id);
//        invoiceMapper.patchInvoice(invoicePatchRequest, invoice);
//        if (invoicePatchRequest.getItems() != null) {
//            applyItems(invoice, invoicePatchRequest.getItems());
//        }
//
//        assertDueDate(invoice);
//        applyTotals(invoice, isCustomerExempt(invoice.getCustomer(), invoice.getIssueDate()));
//        return invoiceMapper.toInvoiceResponse(invoiceRepository.save(invoice));
//    }

    @Transactional
    public void deleteInvoice(Long id) {
        Invoice invoice = assertInvoice(id);
        assertMutable(invoice);
        invoice.setActive(false);
        invoiceRepository.save(invoice);
    }

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

    private Business assertCustomerBusiness(Customer customer) {
        Business business = customer.getBusiness();
        if (business == null) {
            throw new InvalidInvoiceStateException("Customer with id: " + customer.getId()
                    + " does not belong to any business");
        }
        if (business.getSalesTaxRate() == null) {
            throw new InvalidInvoiceStateException("Business with id: " + business.getId()
                    + " has no sales tax rate configured");
        }
        return business;
    }

    private void assertUniqueInvoiceNumber(String invoiceNumber, Long excludeId) {
        if (invoiceNumber == null) {
            return;
        }
        boolean exists = excludeId == null
                ? invoiceRepository.existsByInvoiceNumber(invoiceNumber)
                : invoiceRepository.existsByInvoiceNumberAndIdNot(invoiceNumber, excludeId);
        if (exists) {
            throw new DuplicateResourceException("Invoice number already exists: " + invoiceNumber);
        }
    }

    private void assertCustomerBelongsToBusiness(Business business, Customer customer) {
        if (customer.getBusiness() == null || !customer.getBusiness().getId().equals(business.getId())) {
            throw new InvalidInvoiceStateException("Customer with id: " + customer.getId()
                    + " does not belong to business with id: " + business.getId());
        }
    }

    private void assertProductBelongsToBusiness(Business business, Product product) {
        if (product.getBusiness() == null || !product.getBusiness().getId().equals(business.getId())) {
            throw new InvalidInvoiceStateException("Product with id: " + product.getId()
                    + " does not belong to business with id: " + business.getId());
        }
    }

    private void assertMutable(Invoice invoice) {
        if (invoice.getStatus() == InvoiceStatus.PAID || invoice.getStatus() == InvoiceStatus.VOID) {
            throw new InvalidInvoiceStateException("Invoice with status " + invoice.getStatus()
                    + " cannot be modified or deleted");
        }
    }

    private void assertDueDate(Invoice invoice) {
        if (invoice.getDueDate().isBefore(invoice.getIssueDate())) {
            throw new InvalidInvoiceStateException("DueDate must be on or after IssueDate");
        }
    }

//    private boolean isStatusOnlyPatch(InvoicePatchRequest invoicePatchRequest) {
//        return invoicePatchRequest.getInvoiceNumber() == null
//                && invoicePatchRequest.getIssueDate() == null
//                && invoicePatchRequest.getDueDate() == null
//                && invoicePatchRequest.getTaxRate() == null
//                && invoicePatchRequest.getItems() == null;
//    }





    private void assertSameBusiness(Invoice invoice, Invoice parentInvoice) {
        if (parentInvoice.getBusiness() == null || invoice.getBusiness() == null
                || !parentInvoice.getBusiness().getId().equals(invoice.getBusiness().getId())) {
            throw new InvalidInvoiceStateException("Parent invoice with id: " + parentInvoice.getId()
                    + " does not belong to business with id: "
                    + (invoice.getBusiness() == null ? null : invoice.getBusiness().getId()));
        }
    }

    private void applyItems(Invoice invoice, List<InvoiceItemRequest> itemRequests) {
        if (itemRequests.isEmpty()) {
            throw new InvalidInvoiceStateException("Invoice must contain at least one line item");
        }
        List<InvoiceItem> items = new ArrayList<>();
        for (InvoiceItemRequest itemRequest : itemRequests) {
            InvoiceItem item = invoiceMapper.toInvoiceItem(itemRequest);
            item.setInvoice(invoice);
            if (itemRequest.getProductId() != null) {
                Product product = assertProduct(itemRequest.getProductId());
                assertProductBelongsToBusiness(invoice.getBusiness(), product);
                item.setProduct(product);
            }
            items.add(item);
        }
        invoice.getItems().clear();
        invoice.getItems().addAll(items);
    }

    private LocalDate deriveDueDate(Customer customer, Business business, LocalDate issueDate) {
        String terms = customer.getPaymentTerms() != null
                ? customer.getPaymentTerms()
                : business.getDefaultTerms();
        return issueDate.plusDays(parseTermDays(terms));
    }

    // "Net 30" -> 30. Terms carrying no number (e.g. "Due on receipt") fall back to same-day.
    private int parseTermDays(String terms) {
        if (terms == null) {
            return 0;
        }
        Matcher matcher = TERM_DAYS.matcher(terms);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    // Exempt only when the flag is set and the exemption has not expired on the issue date.
    private boolean isCustomerExempt(Customer customer, LocalDate issueDate) {
        return customer != null
                && Boolean.TRUE.equals(customer.getTaxExempt())
                && (customer.getExemptionExpiryDate() == null
                || !customer.getExemptionExpiryDate().isBefore(issueDate));
    }

    // PREFIX-YEAR-SEQ, where SEQ is (highest existing sequence for this business this year) + 1.
    private String generateInvoiceNumber(Business business, int year) {
        String prefix = business.getInvoicePrefix() + "-" + year + "-";
        long sequence = invoiceRepository
                .findFirstByBusinessAndInvoiceNumberStartingWithOrderByInvoiceNumberDesc(business, prefix)
                .map(latest -> parseSequence(latest.getInvoiceNumber(), prefix))
                .orElse(0L) + 1;
        return prefix + String.format("%0" + SEQUENCE_WIDTH + "d", sequence);
    }

    private long parseSequence(String invoiceNumber, String prefix) {
        try {
            return Long.parseLong(invoiceNumber.substring(prefix.length()));
        } catch (NumberFormatException | IndexOutOfBoundsException ex) {
            return 0L;
        }
    }

    // Product price wins; the request's unitPrice is the fallback.
    private BigDecimal resolveUnitPrice(Product product, InvoiceItemRequest itemRequest) {
        BigDecimal price = product.getSellingPrice() != null
                ? product.getSellingPrice()
                : itemRequest.getUnitPrice();
        if (price == null) {
            throw new InvalidPricingException("No price available for product with id: " + product.getId());
        }
        return price;
    }

    private void applyCreateItems(Invoice invoice, List<InvoiceItemRequest> itemRequests) {
        List<InvoiceItem> items = new ArrayList<>();
        for (InvoiceItemRequest itemRequest : itemRequests) {
            InvoiceItem item = invoiceMapper.toInvoiceItem(itemRequest);
            Product product = assertProduct(itemRequest.getProductId());
            assertProductBelongsToBusiness(invoice.getBusiness(), product);
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

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAllInvoicesForBusiness(Long businessId) {
        return invoiceMapper.toInvoiceResponseList(invoiceRepository.findByBusiness(assertBusiness(businessId)));
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAllInvoicesForCustomer(Long customerId) {
        return invoiceMapper.toInvoiceResponseList(invoiceRepository.findByCustomer(assertCustomer(customerId)));
    }

}
