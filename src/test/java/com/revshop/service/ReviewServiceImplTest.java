package com.revshop.service;

import com.revshop.entity.*;
import com.revshop.exceptions.*;
import com.revshop.repo.*;
import com.revshop.serviceImpl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;
    @Mock private OrderRepository orderRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User testBuyer;
    private Order testOrder;
    private Product testProduct;
    private final String email = "buyer@test.com";

    @BeforeEach
    void setUp() {
        testBuyer = new User();
        testBuyer.setUserId(1L);
        testBuyer.setEmail(email);

        testOrder = new Order();
        testOrder.setOrderId(100L);
        testOrder.setBuyer(testBuyer);

        testProduct = new Product();
        testProduct.setProductId(50L);
    }

    // --- ADD REVIEW TESTS ---

    @Test
    void addReview_Success() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testBuyer));
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(productRepository.findById(50L)).thenReturn(Optional.of(testProduct));
        when(orderItemRepository.existsByOrder_OrderIdAndOrder_Buyer_UserIdAndProduct_ProductId(100L, 1L, 50L)).thenReturn(true);
        when(reviewRepository.existsByBuyer_UserIdAndProduct_ProductIdAndOrder_OrderId(1L, 50L, 100L)).thenReturn(false);

        reviewService.addReview(100L, 50L, 5, "Great product!", email);

        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void addReview_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reviewService.addReview(100L, 50L, 5, "Msg", email))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void addReview_OrderNotFound_ThrowsException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testBuyer));
        when(orderRepository.findById(100L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reviewService.addReview(100L, 50L, 5, "Msg", email))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void addReview_ProductNotFound_ThrowsException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testBuyer));
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(productRepository.findById(50L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reviewService.addReview(100L, 50L, 5, "Msg", email))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void addReview_InvalidPurchase_ThrowsReviewRestrictedException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testBuyer));
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(productRepository.findById(50L)).thenReturn(Optional.of(testProduct));
        when(orderItemRepository.existsByOrder_OrderIdAndOrder_Buyer_UserIdAndProduct_ProductId(100L, 1L, 50L)).thenReturn(false);

        assertThatThrownBy(() -> reviewService.addReview(100L, 50L, 5, "Msg", email))
                .isInstanceOf(ReviewRestrictedException.class);
    }

    @Test
    void addReview_AlreadyReviewed_ThrowsException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testBuyer));
        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(productRepository.findById(50L)).thenReturn(Optional.of(testProduct));
        when(orderItemRepository.existsByOrder_OrderIdAndOrder_Buyer_UserIdAndProduct_ProductId(100L, 1L, 50L)).thenReturn(true);
        when(reviewRepository.existsByBuyer_UserIdAndProduct_ProductIdAndOrder_OrderId(1L, 50L, 100L)).thenReturn(true);

        assertThatThrownBy(() -> reviewService.addReview(100L, 50L, 5, "Msg", email))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Already reviewed.");
    }

    @Test
    void addReview_RatingTooLow_ThrowsException() {
        setupCommonMocks();
        assertThatThrownBy(() -> reviewService.addReview(100L, 50L, 0, "Msg", email))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addReview_RatingTooHigh_ThrowsException() {
        setupCommonMocks();
        assertThatThrownBy(() -> reviewService.addReview(100L, 50L, 6, "Msg", email))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // --- HAS REVIEWED TESTS ---

    @Test
    void hasReviewed_ReturnsTrue() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testBuyer));
        when(reviewRepository.existsByBuyer_UserIdAndProduct_ProductIdAndOrder_OrderId(1L, 50L, 100L)).thenReturn(true);

        boolean result = reviewService.hasReviewed(100L, 50L, email);
        assertThat(result).isTrue();
    }

    @Test
    void hasReviewed_ReturnsFalse() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testBuyer));
        when(reviewRepository.existsByBuyer_UserIdAndProduct_ProductIdAndOrder_OrderId(1L, 50L, 100L)).thenReturn(false);

        boolean result = reviewService.hasReviewed(100L, 50L, email);
        assertThat(result).isFalse();
    }

    // --- EDGE CASE & BRANCH TESTS ---

    @Test
    void addReview_BoundaryRating_1_Success() {
        setupCommonMocks();
        reviewService.addReview(100L, 50L, 1, "Msg", email);
        verify(reviewRepository).save(any());
    }

    @Test
    void addReview_BoundaryRating_5_Success() {
        setupCommonMocks();
        reviewService.addReview(100L, 50L, 5, "Msg", email);
        verify(reviewRepository).save(any());
    }

    @Test
    void hasReviewed_Throws_IfUserNotFound() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reviewService.hasReviewed(100L, 50L, email))
                .isInstanceOf(java.util.NoSuchElementException.class);
    }

    @Test
    void addReview_SavesCorrectComment() {
        setupCommonMocks();
        reviewService.addReview(100L, 50L, 4, "Quality product", email);
        verify(reviewRepository).save(argThat(r -> r.getReviewComment().equals("Quality product")));
    }

    @Test
    void addReview_SetsReviewDate() {
        setupCommonMocks();
        reviewService.addReview(100L, 50L, 4, "Msg", email);
        verify(reviewRepository).save(argThat(r -> r.getReviewDate() != null));
    }

    @Test
    void addReview_VerifyNoMoreInteractions() {
        setupCommonMocks();
        reviewService.addReview(100L, 50L, 4, "Msg", email);
        verify(reviewRepository, times(1)).save(any());
    }

    private void setupCommonMocks() {
        lenient().when(userRepository.findByEmail(email)).thenReturn(Optional.of(testBuyer));
        lenient().when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        lenient().when(productRepository.findById(50L)).thenReturn(Optional.of(testProduct));
        lenient().when(orderItemRepository.existsByOrder_OrderIdAndOrder_Buyer_UserIdAndProduct_ProductId(100L, 1L, 50L)).thenReturn(true);
        lenient().when(reviewRepository.existsByBuyer_UserIdAndProduct_ProductIdAndOrder_OrderId(1L, 50L, 100L)).thenReturn(false);
    }
}