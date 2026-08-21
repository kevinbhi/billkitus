package com.finance.billtick.product.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {

    @NotBlank(message = "ProductName is required")
    @Size(min = 2, message = "ProductName must be at least 2 characters")
    private String productName;

    @NotBlank(message = "CategoryType is required")
    @Size(min = 2, message = "CategoryType must be at least 2 characters")
    private String categoryType;

    @NotBlank(message = "ProductCode is required")
    @Size(min = 2, message = "ProductCode must be at least 2 characters")
    private String productCode;

    @NotNull(message = "PurchasePrice is required")
    @PositiveOrZero(message = "PurchasePrice must be zero or positive")
    @Digits(integer = 10, fraction = 2, message = "PurchasePrice must have at most 2 decimal places")
    private BigDecimal purchasePrice;

    @NotNull(message = "SellingPrice is required")
    @PositiveOrZero(message = "SellingPrice must be zero or positive")
    @Digits(integer = 10, fraction = 2, message = "SellingPrice must have at most 2 decimal places")
    private BigDecimal sellingPrice;

    @NotNull(message = "BusinessId is required")
    private Long businessId;

    private Long customerId;
}
