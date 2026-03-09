package com.revshop.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReviewBuilderTest {

    @Test
    void testReviewBuilder() {

        Product product = new Product();
        User buyer = new User();
        Order order = new Order();

        Review review = Review.builder()
                .reviewId(3L)
                .product(product)
                .buyer(buyer)
                .order(order)
                .rating(5)
                .reviewComment("Amazing!")
                .build();

        assertNotNull(review);
        assertEquals(3L, review.getReviewId());
        assertEquals(product, review.getProduct());
        assertEquals(buyer, review.getBuyer());
        assertEquals(order, review.getOrder());
        assertEquals(5, review.getRating());
        assertEquals("Amazing!", review.getReviewComment());
    }
}