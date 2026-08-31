package com.finance.billtick.invoice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InvoiceDashboardResponse {

    private Long customerId;
    private String customerName;
    private int year;
    private int month;

    private long totalInvoices;
    private BigDecimal totalInvoicedAmount;
    private BigDecimal totalCollectedAmount;
    private BigDecimal totalOutstandingAmount;
}
