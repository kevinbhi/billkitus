package com.finance.billtick.dashboard.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AgingBucketResponse {

    private String bucket;
    private BigDecimal amount;
    private long count;
}
