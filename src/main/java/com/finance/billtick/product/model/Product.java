package com.finance.billtick.product.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finance.billtick.business.model.Business;
import com.finance.billtick.customer.model.Customer;
import com.finance.billtick.invoice.model.Invoice;
import com.finance.billtick.invoice.model.InvoiceItem;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer customer;

    @OneToMany(mappedBy = "product")
    private List<InvoiceItem> invoiceItems = new ArrayList<>();
}
