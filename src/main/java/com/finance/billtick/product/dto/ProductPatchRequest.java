package com.finance.billtick.product.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductPatchRequest {

    @Size(min = 2, message = "ProductName must be at least 2 characters")
    private String productName;

    @Size(min = 2, message = "CategoryType must be at least 2 characters")
    private String categoryType;

    @Size(min = 2, message = "ProductCode must be at least 2 characters")
    private String productCode;

    @PositiveOrZero(message = "PurchasePrice must be zero or positive")
    @Digits(integer = 10, fraction = 2, message = "PurchasePrice must have at most 2 decimal places")
    private BigDecimal purchasePrice;

    @PositiveOrZero(message = "SellingPrice must be zero or positive")
    @Digits(integer = 10, fraction = 2, message = "SellingPrice must have at most 2 decimal places")
    private BigDecimal sellingPrice;

    private Long customerId;
}
