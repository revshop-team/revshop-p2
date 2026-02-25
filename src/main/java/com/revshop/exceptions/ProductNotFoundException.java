package com.revshop.exceptions;

/**
 * Exception: ProductNotFoundException
 */
public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(String message) {
        super(message);
    }
}
