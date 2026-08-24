package com.finance.billtick.invoice.model;

// Document lifecycle only: DRAFT -> SENT -> VOID.
// PAID is superseded by Invoice.paymentStatus and is no longer written; the constant
// stays so existing rows and the generated status check constraint remain valid.
public enum InvoiceStatus {
    DRAFT,
    SENT,
    PAID,
    VOID
}
