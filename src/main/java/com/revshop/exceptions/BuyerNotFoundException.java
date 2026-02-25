package com.revshop.exceptions;

public class BuyerNotFoundException extends RuntimeException{
    public BuyerNotFoundException(String message) {
        super(message);
    }
}
