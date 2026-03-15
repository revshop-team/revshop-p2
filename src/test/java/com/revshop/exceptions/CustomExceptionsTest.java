package com.revshop.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomExceptionsTest {

    @Test
    void testCartItemNotFoundException() {
        CartItemNotFoundException exception = new CartItemNotFoundException("Cart item missing");
        assertEquals("Cart item missing", exception.getMessage());
    }

    @Test
    void testEmailAlreadyExistsException() {
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException("Email is already in use");
        assertEquals("Email is already in use", exception.getMessage());
    }
}