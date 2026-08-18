package com.finance.billtick.product.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductResponse {

    private Long id;
    private String productName;
    private String categoryType;
    private String productCode;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private Long businessId;
}
