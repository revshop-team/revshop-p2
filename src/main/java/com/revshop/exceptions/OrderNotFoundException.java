package com.revshop.exceptions;

public class OrderNotFoundException extends RuntimeException{

    /**
     *
     * @param message
     */
    public OrderNotFoundException(String message) {
        super(message);
    }
}
