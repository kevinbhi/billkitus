package com.finance.billtick.invoice.dto;

import com.finance.billtick.invoice.model.InvoiceStatus;
import com.finance.billtick.invoice.model.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class InvoiceResponse {

    private Long id;
    private String invoiceNumber;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private InvoiceStatus status;
    private String currency;
    private BigDecimal taxRate;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal total;
    private PaymentStatus paymentStatus;
    private BigDecimal amountPaid;
    private BigDecimal balanceDue;
    private boolean overdue;
    private Long daysOverdue;
    private Long businessId;
    private Long customerId;
    private List<InvoiceItemResponse> items;
}
