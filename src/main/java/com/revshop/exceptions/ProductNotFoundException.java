package com.revshop.exceptions;

/**
 * Exception: ProductNotFoundException
 */
public class ProductNotFoundException extends RuntimeException{

    /**
     * @param message
     */
    public ProductNotFoundException(String message) {
        super(message);
    }
}
