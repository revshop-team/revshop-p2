package com.revshop.exceptions;



public class BuyerNotFoundException extends RuntimeException{

    /**
     * @param message
     */
    public BuyerNotFoundException(String message) {
        super(message);
    }
}
