package com.finance.billtick.invoice.service;

import com.finance.billtick.business.model.Business;
import com.finance.billtick.business.repository.BusinessRepository;
import com.finance.billtick.customer.model.Customer;
import com.finance.billtick.customer.repository.CustomerRepository;
import com.finance.billtick.exception.DuplicateResourceException;
import com.finance.billtick.exception.InvalidInvoiceStateException;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final int AMOUNT_SCALE = 2;

    private final InvoiceRepository invoiceRepository;
    private final BusinessRepository businessRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final InvoiceMapper invoiceMapper;

    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest invoiceRequest) {
        Invoice invoice = invoiceMapper.toInvoice(invoiceRequest);
        Business business = assertBusiness(invoiceRequest.getBusinessId());
        Customer customer = assertCustomer(invoiceRequest.getCustomerId());
        assertCustomerBelongsToBusiness(business, customer);
        assertUniqueInvoiceNumber(invoice.getInvoiceNumber(), null);
        invoice.setBusiness(business);
        invoice.setCustomer(customer);
        applyItems(invoice, invoiceRequest.getItems());
        applyTotals(invoice);
        return invoiceMapper.toInvoiceResponse(invoiceRepository.save(invoice));
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceMapper.toInvoiceResponseList(invoiceRepository.findAll());
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(Long id) {
        return invoiceMapper.toInvoiceResponse(assertInvoice(id));
    }

    @Transactional
    public InvoiceResponse updateInvoice(Long id, InvoiceRequest invoiceRequest) {
        Invoice invoice = assertInvoice(id);
        assertMutable(invoice);
        Business business = assertBusiness(invoiceRequest.getBusinessId());
        Customer customer = assertCustomer(invoiceRequest.getCustomerId());
        assertCustomerBelongsToBusiness(business, customer);
        assertUniqueInvoiceNumber(invoiceRequest.getInvoiceNumber(), id);
        invoiceMapper.updateInvoice(invoiceRequest, invoice);
        invoice.setBusiness(business);
        invoice.setCustomer(customer);
        applyItems(invoice, invoiceRequest.getItems());
        applyTotals(invoice);
        return invoiceMapper.toInvoiceResponse(invoiceRepository.save(invoice));
    }

    @Transactional
    public InvoiceResponse patchInvoice(Long id, InvoicePatchRequest invoicePatchRequest) {
        Invoice invoice = assertInvoice(id);
        if (!isStatusOnlyPatch(invoicePatchRequest)) {
            assertMutable(invoice);
        }
        assertUniqueInvoiceNumber(invoicePatchRequest.getInvoiceNumber(), id);
        invoiceMapper.patchInvoice(invoicePatchRequest, invoice);
        if (invoicePatchRequest.getItems() != null) {
            applyItems(invoice, invoicePatchRequest.getItems());
        }
        assertDueDate(invoice);
        applyTotals(invoice);
        return invoiceMapper.toInvoiceResponse(invoiceRepository.save(invoice));
    }

    @Transactional
    public void deleteInvoice(Long id) {
        Invoice invoice = assertInvoice(id);
        assertMutable(invoice);
        invoiceRepository.delete(invoice);
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

    private boolean isStatusOnlyPatch(InvoicePatchRequest invoicePatchRequest) {
        return invoicePatchRequest.getInvoiceNumber() == null
                && invoicePatchRequest.getIssueDate() == null
                && invoicePatchRequest.getDueDate() == null
                && invoicePatchRequest.getTaxRate() == null
                && invoicePatchRequest.getItems() == null;
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

    private void applyTotals(Invoice invoice) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;
        for (InvoiceItem item : invoice.getItems()) {
            BigDecimal lineTotal = item.getQuantity().multiply(item.getUnitPrice())
                    .setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
            BigDecimal lineTax = Boolean.TRUE.equals(item.getTaxable())
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
