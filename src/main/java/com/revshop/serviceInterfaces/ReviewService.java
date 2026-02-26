package com.revshop.serviceInterfaces;

public interface ReviewService {
    void addReview(Long orderId,
                   Long productId,
                   Integer rating,
                   String comment,
                   String userEmail);

    boolean hasReviewed(Long orderId,
                        Long productId,
                        String userEmail);
}
