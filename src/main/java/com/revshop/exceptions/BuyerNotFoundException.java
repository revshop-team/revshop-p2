package com.revshop.exceptions;


/**
 * Exception: BuyerNotFoundException
 */
public class BuyerNotFoundException extends RuntimeException{
    public BuyerNotFoundException(String message) {
        super(message);
    }
}
