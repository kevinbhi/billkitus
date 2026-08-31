package com.finance.billtick.invoice.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finance.billtick.business.model.Business;
import com.finance.billtick.common.model.BaseEntity;
import com.finance.billtick.customer.model.Customer;
import com.finance.billtick.product.model.Product;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
// To CHANGE an index, change its NAME and drop the old one by hand: ddl-auto: update matches
// existing indexes by name only, so editing a columnList in place is silently ignored forever.
@Table(name = "invoice",
        uniqueConstraints = @UniqueConstraint(name = "uk_invoice_business", columnNames = {"invoice_number","business_id"}),
        indexes = {
                // findByBusiness, the dashboard aggregate and the overdue finder all lead with
                // (business_id, is_active). uk_invoice_business cannot serve any of them -- it
                // leads with invoice_number, so business_id is not a seekable prefix.
                @Index(name = "idx_invoice_business_active_due", columnList = "business_id, is_active, due_date"),
                @Index(name = "idx_invoice_customer_active", columnList = "customer_id, is_active")
        })
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


    @ColumnDefault("'USD'")
    @Column(nullable = false, length = 3)
    private String currency = "USD";

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal total;


    @ColumnDefault("0")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceDue = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @ColumnDefault("'UNPAID'")
    @Column(nullable = false, length = 50)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Version
    @ColumnDefault("0")
    private long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    @JsonIgnore
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer customer;

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "invoice", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<InvoiceItem> items = new ArrayList<>();

    // Derived, not persisted: the entity uses field access, so Hibernate ignores these getters.
    // MapStruct still maps them onto InvoiceResponse by name.
    public BigDecimal getAmountPaid() {
        return balanceDue == null ? BigDecimal.ZERO : total.subtract(balanceDue);
    }

    // Overdue is a function of the clock, so it is computed on read rather than stored.
    public boolean isOverdue() {
        return status == InvoiceStatus.SENT
                && paymentStatus != PaymentStatus.PAID
                && dueDate.isBefore(LocalDate.now())
                && balanceDue.signum() > 0;
    }

    public Long getDaysOverdue() {
        return isOverdue() ? ChronoUnit.DAYS.between(dueDate, LocalDate.now()) : 0L;
    }

}
