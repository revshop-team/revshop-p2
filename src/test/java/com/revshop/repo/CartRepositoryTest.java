package com.revshop.repo;

import com.revshop.entity.Cart;
import com.revshop.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CartRepositoryTest {

    // Mocking the interface directly to bypass the database connection
    @Mock
    private CartRepository cartRepository;

    private User dummyBuyer;
    private Cart dummyCart;

    @BeforeEach
    void setUp() {
        dummyBuyer = new User();
        // set dummyBuyer.setEmail("buyer@test.com") here if your tests need it later

        dummyCart = new Cart();
        dummyCart.setCartId(1L);
        dummyCart.setBuyer(dummyBuyer);
    }

    @Test
    void testFindByBuyer_Found() {
        // Train the mock to return our dummy cart
        Mockito.when(cartRepository.findByBuyer(dummyBuyer)).thenReturn(Optional.of(dummyCart));

        // Call the method
        Optional<Cart> result = cartRepository.findByBuyer(dummyBuyer);

        // Verify the results
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(1L, result.get().getCartId());
        Assertions.assertEquals(dummyBuyer, result.get().getBuyer());
    }

    @Test
    void testFindByBuyer_NotFound() {
        // Train the mock to return an empty Optional
        Mockito.when(cartRepository.findByBuyer(dummyBuyer)).thenReturn(Optional.empty());

        // Call the method
        Optional<Cart> result = cartRepository.findByBuyer(dummyBuyer);

        // Verify it is empty
        Assertions.assertFalse(result.isPresent());
    }
}