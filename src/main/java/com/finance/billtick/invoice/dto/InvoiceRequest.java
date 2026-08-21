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


    @NotNull(message = "CustomerId is required")
    private Long customerId;

    private LocalDate issueDate;

    @NotEmpty(message = "Items must contain at least one line item")
    @Valid
    private List<InvoiceItemRequest> items;


}
