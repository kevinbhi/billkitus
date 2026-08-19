package com.finance.billtick.product.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finance.billtick.business.model.Business;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product", uniqueConstraints = @UniqueConstraint(name = "uk_product_business_code", columnNames = {"business_id", "product_code"}))
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    private String categoryType;
    private String productCode;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    @JsonIgnore
    private Business business;
}
