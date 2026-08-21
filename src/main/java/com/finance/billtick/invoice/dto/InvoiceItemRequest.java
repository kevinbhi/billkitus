package com.finance.billtick.invoice.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InvoiceItemRequest {

    @NotBlank(message = "Description is required")
    @Size(min = 2, max = 500, message = "Description must be between 2 and 500 characters")
    private String description;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Quantity must have at most 2 decimal places")
    private BigDecimal quantity;

    // Optional fallback - used only if the product has no sellingPrice of its own.
    @PositiveOrZero(message = "UnitPrice must be zero or positive")
    @Digits(integer = 15, fraction = 2, message = "UnitPrice must have at most 2 decimal places")
    private BigDecimal unitPrice;

    @NotNull(message = "Taxable is required")
    private Boolean taxable;

    @NotNull(message = "ProductId is required")
    private Long productId;
}
