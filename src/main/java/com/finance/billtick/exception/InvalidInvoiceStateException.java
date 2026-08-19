package com.finance.billtick.exception;

public class InvalidInvoiceStateException extends RuntimeException{
    public InvalidInvoiceStateException(String message){
        super(message);
    }
}
