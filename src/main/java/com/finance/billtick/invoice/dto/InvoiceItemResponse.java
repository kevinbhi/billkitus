package com.finance.billtick.invoice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InvoiceItemResponse {

    private Long id;
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private Boolean taxable;
    private BigDecimal lineTotal;
    private BigDecimal lineTax;
    private Long productId;
}
