package com.revshop.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    @Test
    void testReviewGettersAndSetters() {

        Review review = new Review();

        Product product = new Product();
        User buyer = new User();
        Order order = new Order();

        LocalDateTime now = LocalDateTime.now();

        review.setReviewId(1L);
        review.setProduct(product);
        review.setBuyer(buyer);
        review.setOrder(order);
        review.setRating(5);
        review.setReviewComment("Excellent product");
        review.setReviewDate(now);

        assertEquals(1L, review.getReviewId());
        assertEquals(product, review.getProduct());
        assertEquals(buyer, review.getBuyer());
        assertEquals(order, review.getOrder());
        assertEquals(5, review.getRating());
        assertEquals("Excellent product", review.getReviewComment());
        assertEquals(now, review.getReviewDate());
    }

    @Test
    void testAllArgsConstructor() {

        Product product = new Product();
        User buyer = new User();
        Order order = new Order();
        LocalDateTime now = LocalDateTime.now();

        Review review = new Review(
                2L,
                product,
                buyer,
                order,
                4,
                "Good product",
                now
        );

        assertNotNull(review);
        assertEquals(2L, review.getReviewId());
        assertEquals(4, review.getRating());
        assertEquals("Good product", review.getReviewComment());
    }
}