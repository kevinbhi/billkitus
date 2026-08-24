package com.finance.billtick.payment.dto;

import com.finance.billtick.payment.model.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InvoicePaymentRequest {

    @NotNull(message = "InvoiceId is required")
    private Long invoiceId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Digits(integer = 15, fraction = 2, message = "Amount must have at most 2 decimal places")
    private BigDecimal amount;

    // Optional: defaults to today, mirroring how issueDate is defaulted on an invoice.
    @PastOrPresent(message = "PaymentDate must not be in the future")
    private LocalDate paymentDate;

    @NotNull(message = "Method is required")
    private PaymentMethod method;

    @Size(max = 255, message = "ReferenceNumber must be at most 255 characters")
    private String referenceNumber;

    @Size(max = 500, message = "Notes must be at most 500 characters")
    private String notes;

    @AssertTrue(message = "ReferenceNumber is required when Method is CHECK or BANK_TRANSFER")
    public boolean isReferenceNumberValid() {
        if (method != PaymentMethod.CHECK && method != PaymentMethod.BANK_TRANSFER) {
            return true;
        }
        return referenceNumber != null && !referenceNumber.isBlank();
    }
}
