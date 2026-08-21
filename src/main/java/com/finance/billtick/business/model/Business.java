package com.finance.billtick.business.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finance.billtick.customer.model.Customer;
import com.finance.billtick.product.model.Product;
import com.finance.billtick.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "business")
@Getter
@Setter
@NoArgsConstructor
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String businessName;
    private String city;
    private String state;
    private String zipCode;
    private String invoicePrefix;
    private String defaultTerms;
    @Column(precision = 10, scale = 2)
    private BigDecimal salesTaxRate;
    private String logo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Customer> customers;

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;
}
