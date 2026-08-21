package com.finance.billtick.invoice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class InvoiceRequest {

    @NotNull(message = "CustomerId is required")
    private Long customerId;

    // Optional. Server defaults to today when not provided.
    private LocalDate issueDate;

    @NotEmpty(message = "Items must contain at least one line item")
    @Valid
    private List<InvoiceItemRequest> items;
}
