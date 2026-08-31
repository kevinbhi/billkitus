package com.finance.billtick.invoice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class OverdueInvoiceResponse {

    private String customerName;
    private String invoiceNumber;
    private LocalDate dueDate;
    private BigDecimal balanceDue;
    private Long daysOverdue;
}
