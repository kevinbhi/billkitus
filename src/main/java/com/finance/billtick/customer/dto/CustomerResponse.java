package com.finance.billtick.customer.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CustomerResponse {

    private Long id;
    private String customerCode;
    private String customerName;
    private String email;
    private String phone;
    private String city;
    private String state;
    private String paymentTerms;
    private Boolean taxExempt;
    private String exemptionType;
    private String certificateNumber;
    private LocalDate exemptionExpiryDate;
    private Long businessId;
}
