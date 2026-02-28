package com.revshop.serviceImpl;

import com.revshop.entity.Order;
import com.revshop.entity.Product;
import com.revshop.entity.Review;
import com.revshop.entity.User;
import com.revshop.exceptions.OrderNotFoundException;
import com.revshop.exceptions.ProductNotFoundException;
import com.revshop.exceptions.ReviewRestrictedException;
import com.revshop.exceptions.UserNotFoundException;
import com.revshop.repo.*;
import com.revshop.serviceInterfaces.ReviewService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             OrderItemRepository orderItemRepository,
                             UserRepository userRepository,
                             ProductRepository productRepository,
                             OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void addReview(Long orderId,
                          Long productId,
                          Integer rating,
                          String comment,
                          String userEmail) {

        User buyer = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found"));

        boolean validPurchase =
                orderItemRepository
                        .existsByOrder_OrderIdAndOrder_Buyer_UserIdAndProduct_ProductId(
                                orderId,
                                buyer.getUserId(),
                                productId
                        );
        if (!validPurchase) {
            throw new ReviewRestrictedException("You cannot review this product.");
        }

        boolean alreadyReviewed =
                reviewRepository
                        .existsByBuyer_UserIdAndProduct_ProductIdAndOrder_OrderId(
                                buyer.getUserId(),
                                productId,
                                orderId
                        );

        if (alreadyReviewed) {
            throw new RuntimeException("Already reviewed.");
        }

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        Review review = new Review();
        review.setBuyer(buyer);
        review.setProduct(product);
        review.setOrder(order);
        review.setRating(rating);
        review.setReviewComment(comment);
        review.setReviewDate(LocalDateTime.now());

        reviewRepository.save(review);
    }

    @Override
    public boolean hasReviewed(Long orderId,
                               Long productId,
                               String userEmail) {

        User buyer = userRepository.findByEmail(userEmail)
                .orElseThrow();

        return reviewRepository
                .existsByBuyer_UserIdAndProduct_ProductIdAndOrder_OrderId(
                        buyer.getUserId(),
                        productId,
                        orderId
                );
    }
}
