package com.finance.billtick.payment.service;

import com.finance.billtick.business.model.Business;
import com.finance.billtick.business.repository.BusinessRepository;
import com.finance.billtick.customer.model.Customer;
import com.finance.billtick.customer.repository.CustomerRepository;
import com.finance.billtick.exception.InvalidInvoiceStateException;
import com.finance.billtick.exception.ResourceNotFoundException;
import com.finance.billtick.invoice.model.Invoice;
import com.finance.billtick.invoice.model.InvoiceStatus;
import com.finance.billtick.invoice.model.PaymentStatus;
import com.finance.billtick.invoice.repository.InvoiceRepository;
import com.finance.billtick.payment.dto.InvoicePaymentRequest;
import com.finance.billtick.payment.dto.InvoicePaymentResponse;
import com.finance.billtick.payment.mapper.InvoicePaymentMapper;
import com.finance.billtick.payment.model.InvoicePayment;
import com.finance.billtick.payment.repository.InvoicePaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoicePaymentService {

    private static final int AMOUNT_SCALE = 2;

    private final InvoicePaymentRepository invoicePaymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final BusinessRepository businessRepository;
    private final CustomerRepository customerRepository;
    private final InvoicePaymentMapper invoicePaymentMapper;

    @Transactional
    public InvoicePaymentResponse createPayment(InvoicePaymentRequest invoicePaymentRequest) {
        Invoice invoice = assertInvoice(invoicePaymentRequest.getInvoiceId());
        assertPayable(invoice);
        LocalDate paymentDate = invoicePaymentRequest.getPaymentDate() != null
                ? invoicePaymentRequest.getPaymentDate()
                : LocalDate.now();
        assertPaymentDate(invoice, paymentDate);
        assertNotOverpayment(invoice, invoicePaymentRequest.getAmount());

        InvoicePayment payment = invoicePaymentMapper.toInvoicePayment(invoicePaymentRequest);
        payment.setInvoice(invoice);
        payment.setBusiness(invoice.getBusiness());
        payment.setCustomer(invoice.getCustomer());
        payment.setPaymentDate(paymentDate);
        payment.setAmount(invoicePaymentRequest.getAmount().setScale(AMOUNT_SCALE, RoundingMode.HALF_UP));
        // Flushed so the recompute below sees the new row.
        InvoicePayment saved = invoicePaymentRepository.saveAndFlush(payment);

        applyInvoiceBalance(invoice);
        invoiceRepository.save(invoice);
        return invoicePaymentMapper.toInvoicePaymentResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<InvoicePaymentResponse> getAllPayments() {
        return invoicePaymentMapper.toInvoicePaymentResponseList(invoicePaymentRepository.findAll());
    }

    @Transactional(readOnly = true)
    public InvoicePaymentResponse getPaymentById(Long id) {
        return invoicePaymentMapper.toInvoicePaymentResponse(assertPayment(id));
    }

    @Transactional(readOnly = true)
    public List<InvoicePaymentResponse> getPaymentsForInvoice(Long invoiceId) {
        return invoicePaymentMapper.toInvoicePaymentResponseList(
                invoicePaymentRepository.findByInvoiceOrderByPaymentDateDescIdDesc(assertInvoice(invoiceId)));
    }

    @Transactional(readOnly = true)
    public List<InvoicePaymentResponse> getPaymentsForBusiness(Long businessId) {
        return invoicePaymentMapper.toInvoicePaymentResponseList(
                invoicePaymentRepository.findByBusiness(assertBusiness(businessId)));
    }

    @Transactional(readOnly = true)
    public List<InvoicePaymentResponse> getPaymentsForCustomer(Long customerId) {
        return invoicePaymentMapper.toInvoicePaymentResponseList(
                invoicePaymentRepository.findByCustomer(assertCustomer(customerId)));
    }

    // Reversal, not edit: the payment row stays in the table with its original timestamp and
    // is_active = 0, and the balance is recomputed through the same path used on create.
    // A second call finds nothing through @SQLRestriction and 404s, so this is idempotent.
    @Transactional
    public void deletePayment(Long id) {
        InvoicePayment payment = assertPayment(id);
        Invoice invoice = payment.getInvoice();
        assertReversible(invoice);
        payment.setActive(false);
        // Flushed so the recompute below no longer sees the reversed row.
        invoicePaymentRepository.saveAndFlush(payment);

        applyInvoiceBalance(invoice);
        invoiceRepository.save(invoice);
    }

    private InvoicePayment assertPayment(Long id) {
        return invoicePaymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
    }

    private Invoice assertInvoice(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));
    }

    private Business assertBusiness(Long businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + businessId));
    }

    private Customer assertCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
    }

    private void assertPayable(Invoice invoice) {
        if (invoice.getStatus() == InvoiceStatus.DRAFT) {
            throw new InvalidInvoiceStateException("Invoice with id: " + invoice.getId()
                    + " must be sent before payments can be recorded");
        }
        if (invoice.getStatus() == InvoiceStatus.VOID) {
            throw new InvalidInvoiceStateException("Invoice with id: " + invoice.getId()
                    + " is void and cannot accept payments");
        }
        if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
            throw new InvalidInvoiceStateException("Invoice with id: " + invoice.getId()
                    + " is already paid in full");
        }
    }

    // Deliberately does not consult InvoiceService.assertMutable: reversing a payment on a
    // fully-paid invoice is a legal transition, not an edit of a locked document.
    private void assertReversible(Invoice invoice) {
        if (invoice.getStatus() == InvoiceStatus.VOID) {
            throw new InvalidInvoiceStateException("Invoice with id: " + invoice.getId()
                    + " is void and its payments cannot be reversed");
        }
    }

    private void assertPaymentDate(Invoice invoice, LocalDate paymentDate) {
        if (paymentDate.isBefore(invoice.getIssueDate())) {
            throw new InvalidInvoiceStateException("PaymentDate must be on or after the invoice issue date: "
                    + invoice.getIssueDate());
        }
    }

    private void assertNotOverpayment(Invoice invoice, BigDecimal amount) {
        if (amount.compareTo(invoice.getBalanceDue()) > 0) {
            throw new InvalidInvoiceStateException("Amount " + amount
                    + " exceeds the remaining balance of " + invoice.getBalanceDue()
                    + " on invoice with id: " + invoice.getId());
        }
    }

    // The single place balance and payment status are decided, so recording and reversing
    // a payment cannot drift apart. Recomputed from the payment rows rather than adjusted,
    // so any past drift self-repairs on the next write.
    private void applyInvoiceBalance(Invoice invoice) {
        BigDecimal paid = invoicePaymentRepository.findByInvoiceOrderByPaymentDateDescIdDesc(invoice).stream()
                .map(InvoicePayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
        invoice.setBalanceDue(invoice.getTotal().subtract(paid).setScale(AMOUNT_SCALE, RoundingMode.HALF_UP));
        invoice.setPaymentStatus(derivePaymentStatus(invoice.getTotal(), paid));
    }

    private PaymentStatus derivePaymentStatus(BigDecimal total, BigDecimal paid) {
        if (paid.signum() == 0) {
            return PaymentStatus.UNPAID;
        }
        return paid.compareTo(total) >= 0 ? PaymentStatus.PAID : PaymentStatus.PARTIALLY_PAID;
    }

}
