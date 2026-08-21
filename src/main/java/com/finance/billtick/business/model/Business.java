package com.finance.billtick.business.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finance.billtick.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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
}
