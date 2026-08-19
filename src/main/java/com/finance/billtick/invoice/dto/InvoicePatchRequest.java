package com.finance.billtick.invoice.dto;

import com.finance.billtick.invoice.model.InvoiceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class InvoicePatchRequest {

    @Size(min = 2, message = "InvoiceNumber must be at least 2 characters")
    private String invoiceNumber;

    private LocalDate issueDate;

    private LocalDate dueDate;

    private InvoiceStatus status;

    @PositiveOrZero(message = "TaxRate must be zero or positive")
    @Digits(integer = 10, fraction = 2, message = "TaxRate must have at most 2 decimal places")
    private BigDecimal taxRate;

    @Valid
    private List<InvoiceItemRequest> items;

    @AssertTrue(message = "DueDate must be on or after IssueDate")
    public boolean isDueDateValid() {
        if (issueDate == null || dueDate == null) {
            return true;
        }
        return !dueDate.isBefore(issueDate);
    }
}
