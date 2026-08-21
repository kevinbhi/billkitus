package com.finance.billtick.invoice.dto;

import com.finance.billtick.invoice.model.InvoiceStatus;
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
    private BigDecimal taxRate;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal total;
    private Long businessId;
    private Long customerId;
    private Long productId;
    private Long parentInvoiceId;
    private List<InvoiceItemResponse> items;
}
