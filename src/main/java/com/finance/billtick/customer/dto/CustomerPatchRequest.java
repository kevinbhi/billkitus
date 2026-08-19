package com.finance.billtick.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CustomerPatchRequest {

    @Size(min = 2, message = "CustomerCode must be at least 2 characters")
    private String customerCode;

    @Size(min = 2, message = "CustomerName must be at least 2 characters")
    private String customerName;

    @Email(message = "Must be in valid format.")
    private String email;

    @Size(min = 10, message = "Phone must be at least 10 characters")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone must be 10 to 15 digits")
    private String phone;

    @Size(min = 2, message = "City must be at least 2 characters")
    private String city;

    @Size(min = 2, message = "State must be at least 2 characters")
    private String state;

    @Size(min = 2, message = "PaymentTerms must be at least 2 characters")
    private String paymentTerms;

    private Boolean taxExempt;

    @Size(min = 2, message = "ExemptionType must be at least 2 characters")
    private String exemptionType;

    @Size(min = 2, message = "CertificateNumber must be at least 2 characters")
    private String certificateNumber;

    private LocalDate exemptionExpiryDate;
}
