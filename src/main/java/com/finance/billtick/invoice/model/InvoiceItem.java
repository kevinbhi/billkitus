package com.finance.billtick.invoice.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finance.billtick.common.model.BaseEntity;
import com.finance.billtick.product.model.Product;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_item",
        indexes = @Index(name = "idx_invoice_item_invoice_active", columnList = "invoice_id, is_active"))
@SQLRestriction("is_active = 1")
@Getter
@Setter
@NoArgsConstructor
public class InvoiceItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Boolean taxable;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal lineTotal;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal lineTax;

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;
}
