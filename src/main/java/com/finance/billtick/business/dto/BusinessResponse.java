package com.finance.billtick.business.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessResponse {

    private Long id;
    private String businessName;
    private String city;
    private String state;
    private String zipCode;
    private String invoicePrefix;
    private String defaultTerms;
    private long salesTaxRate;
    private String logo;
    private Long userId;
}
