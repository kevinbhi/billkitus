package com.finance.billtick.customer.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CustomerRequest {

    @NotBlank(message = "CustomerCode is required")
    @Size(min = 2, message = "CustomerCode must be at least 2 characters")
    private String customerCode;

    @NotBlank(message = "CustomerName is required")
    @Size(min = 2, message = "CustomerName must be at least 2 characters")
    private String customerName;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be in valid format.")
    private String email;

    @NotBlank(message = "Phone is required")
    @Size(min = 10, message = "Phone must be at least 10 characters")
    private String phone;

    @NotBlank(message = "City is required")
    @Size(min = 2, message = "City must be at least 2 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(min = 2, message = "State must be at least 2 characters")
    private String state;

    @NotBlank(message = "PaymentTerms is required")
    @Size(min = 2, message = "PaymentTerms must be at least 2 characters")
    private String paymentTerms;

    @NotNull(message = "TaxExempt is required")
    private Boolean taxExempt;

    @NotBlank(message = "ExemptionType is required")
    @Size(min = 2, message = "ExemptionType must be at least 2 characters")
    private String exemptionType;

    @NotBlank(message = "CertificateNumber is required")
    @Size(min = 2, message = "CertificateNumber must be at least 2 characters")
    private String certificateNumber;

    @NotNull(message = "ExemptionExpiryDate is required")
    private LocalDate exemptionExpiryDate;

    @NotNull(message = "BusinessId is required")
    private Long businessId;
}
