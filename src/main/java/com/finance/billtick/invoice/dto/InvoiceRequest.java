package com.finance.billtick.invoice.dto;

import com.finance.billtick.invoice.model.InvoiceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class InvoiceRequest {

    @NotBlank(message = "InvoiceNumber is required")
    @Size(min = 2, message = "InvoiceNumber must be at least 2 characters")
    private String invoiceNumber;

    @NotNull(message = "IssueDate is required")
    private LocalDate issueDate;

    @NotNull(message = "DueDate is required")
    private LocalDate dueDate;

    @NotNull(message = "Status is required")
    private InvoiceStatus status;

    @NotNull(message = "TaxRate is required")
    @PositiveOrZero(message = "TaxRate must be zero or positive")
    @Digits(integer = 10, fraction = 2, message = "TaxRate must have at most 2 decimal places")
    private BigDecimal taxRate;

    @NotNull(message = "BusinessId is required")
    private Long businessId;

    @NotNull(message = "CustomerId is required")
    private Long customerId;

    private Long productId;

    private Long parentInvoiceId;

    @NotEmpty(message = "Items must contain at least one line item")
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
