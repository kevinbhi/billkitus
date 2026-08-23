package com.finance.billtick.business.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finance.billtick.customer.model.Customer;
import com.finance.billtick.product.model.Product;
import com.finance.billtick.common.model.BaseEntity;
import com.finance.billtick.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "business")
@SQLRestriction("is_active = 1")
@Getter
@Setter
@NoArgsConstructor
public class Business extends BaseEntity {

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

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "business")
    private List<Customer> customers;

    @OneToMany(mappedBy = "business")
    private List<Product> products;
}
