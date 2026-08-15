package com.finance.billtick.business.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessRequest {

    @NotBlank(message = "BusinessName is required")
    @Size(min = 2, message = "BusinessName must be at least 2 characters")
    private String businessName;

    @NotBlank(message = "City is required")
    @Size(min = 2, message = "City must be at least 2 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(min = 2, message = "State must be at least 2 characters")
    private String state;

    @NotNull(message = "ZipCode is required")
    @Positive(message = "ZipCode must be positive")
    private String zipCode;

    @NotBlank(message = "InvoicePrefix is required")
    @Size(min = 2, message = "InvoicePrefix must be at least 2 characters")
    private String invoicePrefix;

    @NotBlank(message = "DefaultTerms is required")
    @Size(min = 2, message = "DefaultTerms must be at least 2 characters")
    private String defaultTerms;

    @NotNull(message = "SalesTaxRate is required")
    @PositiveOrZero(message = "SalesTaxRate must be zero or positive")
    private Long salesTaxRate;

    @NotBlank(message = "Logo is required")
    private String logo;

    @NotNull(message = "UserId is required")
    private Long userId;
}
