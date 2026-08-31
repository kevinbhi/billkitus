package com.finance.billtick.invoice.repository;

import java.math.BigDecimal;

public record CustomerMonthlyTotals(
        long invoiceCount,
        BigDecimal totalInvoiced,
        BigDecimal totalCollected,
        BigDecimal totalOutstanding) {
}
