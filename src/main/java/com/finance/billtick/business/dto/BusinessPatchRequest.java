package com.finance.billtick.business.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

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
    private String zipCode;

    @Size(min = 2, message = "InvoicePrefix must be at least 2 characters")
    private String invoicePrefix;

    @Size(min = 2, message = "DefaultTerms must be at least 2 characters")
    @Pattern(regexp = "(?i)^(due on receipt|cod|cia|([0-9]{1,2}/[0-9]{1,3}\\s+)?net\\s*[0-9]{1,3})$",
            message = "DefaultTerms must be one of: Due on Receipt, COD, CIA, Net <days>, or a discount form such as 2/10 Net 30")
    private String defaultTerms;

    @PositiveOrZero(message = "SalesTaxRate must be zero or positive")
    @Digits(integer = 10, fraction = 2, message = "SalesTaxRate must have at most 2 decimal places")
    private BigDecimal salesTaxRate;

    private String logo;
}
