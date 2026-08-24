package com.finance.billtick.payment.dto;

import com.finance.billtick.payment.model.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InvoicePaymentResponse {

    private Long id;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private PaymentMethod method;
    private String referenceNumber;
    private String notes;
    private Long invoiceId;
    private String invoiceNumber;
    private Long businessId;
    private Long customerId;
}
