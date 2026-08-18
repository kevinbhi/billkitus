package com.finance.billtick.exception;

public class InvalidPricingException extends RuntimeException{
    public InvalidPricingException(String message){
        super(message);
    }
}
