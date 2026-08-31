package com.finance.billtick.invoice.repository;

import java.math.BigDecimal;


public record AgingTotals(
        BigDecimal currentAmount, long currentCount,
        BigDecimal days1to30Amount, long days1to30Count,
        BigDecimal days31to60Amount, long days31to60Count,
        BigDecimal days61to90Amount, long days61to90Count,
        BigDecimal over90Amount, long over90Count) {
}
