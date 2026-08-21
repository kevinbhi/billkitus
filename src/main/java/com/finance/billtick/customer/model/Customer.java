package com.finance.billtick.customer.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finance.billtick.business.model.Business;
import com.finance.billtick.product.model.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer", uniqueConstraints = @UniqueConstraint(name = "customer_business_code", columnNames = {"business_id", "customer_code"}))
@Getter
@Setter
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    @JsonIgnore // check point
    private Business business;

    @OneToMany(mappedBy = "customer")
    private List<Product> products = new ArrayList<>();
}
