package com.finance.billtick.payment.model;
public enum PaymentMethod {
    CASH(false),
    CHECK(true),          // check number
    ACH(true),            // NACHA trace number
    WIRE(true),           // Fedwire reference / IMAD-OMAD
    CREDIT_CARD(true),    // processor transaction id or auth code -- never a card number
    OTHER(false);

    private final boolean referenceRequired;

    PaymentMethod(boolean referenceRequired) {
        this.referenceRequired = referenceRequired;
    }

    public boolean isReferenceRequired() {
        return referenceRequired;
    }
}
