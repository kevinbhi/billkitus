package com.finance.billtick.business.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "business")
@Getter
@Setter
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String businessName;
    private String city;
    private String state;
    private int zipCode;
    private String invoicePrefix;
    private String defaultTerms;
    private long salesTaxRate;
    private String logo;
}
