package com.revshop.exceptions;

/**
 * Exception: CartItemNotFoundException
 */
public class CartItemNotFoundException extends RuntimeException{

    /**
     * @param message
     */
    public CartItemNotFoundException(String message) {
        super(message);
    }
}
