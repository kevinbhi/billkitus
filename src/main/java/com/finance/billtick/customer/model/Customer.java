package com.finance.billtick.customer.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finance.billtick.business.model.Business;
import com.finance.billtick.common.model.BaseEntity;
import com.finance.billtick.product.model.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer", uniqueConstraints = @UniqueConstraint(name = "customer_business_code", columnNames = {"business_id", "customer_code"}))
@SQLRestriction("is_active = 1")
@Getter
@Setter
@NoArgsConstructor
public class Customer extends BaseEntity {

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

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    @JsonIgnore
    private Business business;

    @OneToMany(mappedBy = "customer")
    private List<Product> products = new ArrayList<>();
}
