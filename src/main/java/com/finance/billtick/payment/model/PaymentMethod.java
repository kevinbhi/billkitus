package com.finance.billtick.payment.model;
public enum PaymentMethod {
    CASH(false),
    CHECK(true),
    ACH(true),
    WIRE(true),
    CREDIT_CARD(true),
    OTHER(false);

    private final boolean referenceRequired;

    PaymentMethod(boolean referenceRequired) {
        this.referenceRequired = referenceRequired;
    }

    public boolean isReferenceRequired() {
        return referenceRequired;
    }
}
