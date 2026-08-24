package com.finance.billtick.dashboard.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class DashboardResponse {

    private Long businessId;
    private LocalDate asOfDate;

    private BigDecimal totalInvoiced;
    private BigDecimal totalCollected;
    private BigDecimal totalOutstanding;
    private BigDecimal overdueAmount;
    private long overdueCount;

    private long draftCount;
    private long sentCount;
    private long voidCount;

    private long unpaidCount;
    private long partiallyPaidCount;
    private long paidCount;

    private List<AgingBucketResponse> aging;
}
