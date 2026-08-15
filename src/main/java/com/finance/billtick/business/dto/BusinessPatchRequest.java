package com.finance.billtick.business.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessPatchRequest {

    @Size(min = 2, message = "BusinessName must be at least 2 characters")
    private String businessName;

    @Size(min = 2, message = "City must be at least 2 characters")
    private String city;

    @Size(min = 2, message = "State must be at least 2 characters")
    private String state;

    @Positive(message = "ZipCode must be positive")
    private Integer zipCode;

    @Size(min = 2, message = "InvoicePrefix must be at least 2 characters")
    private String invoicePrefix;

    @Size(min = 2, message = "DefaultTerms must be at least 2 characters")
    private String defaultTerms;

    @PositiveOrZero(message = "SalesTaxRate must be zero or positive")
    private Long salesTaxRate;

    private String logo;
}
