package com.finance.billtick.invoice.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finance.billtick.business.model.Business;
import com.finance.billtick.common.model.BaseEntity;
import com.finance.billtick.customer.model.Customer;
import com.finance.billtick.product.model.Product;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoice", uniqueConstraints = @UniqueConstraint(name = "uk_invoice_business", columnNames = {"invoice_number","business_id"}))
@SQLRestriction("is_active = 1")
@Getter
@Setter
@NoArgsConstructor
public class Invoice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String invoiceNumber;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 50)
    private InvoiceStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal taxRate;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    @JsonIgnore
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer customer;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "invoice", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<InvoiceItem> items = new ArrayList<>();


}
