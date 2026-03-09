package com.revshop.exception;

import com.revshop.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductNotFoundExceptionTest {

    @Test
    void testExceptionMessage() {

        ProductNotFoundException exception =
                new ProductNotFoundException("Product not found");

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    void testExceptionThrown() {

        assertThrows(ProductNotFoundException.class, () -> {
            throw new ProductNotFoundException("Product not found");
        });
    }
}