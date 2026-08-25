package com.finance.billtick.invoice.repository;

import java.math.BigDecimal;

public record InvoiceTotals(
        BigDecimal totalInvoiced,
        BigDecimal totalCollected,
        BigDecimal totalOutstanding,
        BigDecimal overdueAmount,
        long overdueCount,
        long draftCount,
        long sentCount,
        long voidCount,
        long unpaidCount,
        long partiallyPaidCount,
        long paidCount) {
}
