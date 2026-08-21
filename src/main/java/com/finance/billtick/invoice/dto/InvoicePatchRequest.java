package com.finance.billtick.invoice.dto;

import com.finance.billtick.invoice.model.InvoiceStatus;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class InvoicePatchRequest {

    // Reassign a draft to a different customer. Optional.
    private Long customerId;

    private LocalDate issueDate;

    // Status transition, e.g. DRAFT -> SENT ("Save and send"), SENT -> PAID, * -> VOID.
    private InvoiceStatus status;

    // When present, fully replaces the existing line items.
    @Valid
    private List<InvoiceItemRequest> items;
}
