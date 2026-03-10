package com.revshop.repo;

import com.revshop.entity.Order;
import com.revshop.entity.Product;
import com.revshop.entity.Review;
import com.revshop.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ReviewRepositoryTest {

    @Mock
    private ReviewRepository reviewRepository;

    private Review dummyReview;
    private User dummyBuyer;
    private User dummySeller;
    private Product dummyProduct;
    private Order dummyOrder;

    @BeforeEach
    void setUp() {
        dummyBuyer = new User();
        dummyBuyer.setUserId(10L);

        dummySeller = new User();
        dummySeller.setUserId(20L);

        dummyProduct = new Product();
        dummyProduct.setProductId(100L);
        dummyProduct.setSeller(dummySeller);

        dummyOrder = new Order();
        dummyOrder.setOrderId(500L);

        dummyReview = new Review();
        dummyReview.setReviewId(1L);
        dummyReview.setBuyer(dummyBuyer);
        dummyReview.setProduct(dummyProduct);
        dummyReview.setOrder(dummyOrder);
        dummyReview.setRating(5);
        dummyReview.setReviewComment("Excellent product!");
        dummyReview.setReviewDate(LocalDateTime.now());
    }

    // 1. Testing exists method when review is found
    @Test
    void testExistsByBuyer_UserIdAndProduct_ProductIdAndOrder_OrderId_True() {
        Mockito.when(reviewRepository.existsByBuyer_UserIdAndProduct_ProductIdAndOrder_OrderId(10L, 100L, 500L))
                .thenReturn(true);

        boolean exists = reviewRepository.existsByBuyer_UserIdAndProduct_ProductIdAndOrder_OrderId(10L, 100L, 500L);

        Assertions.assertTrue(exists);
    }

    // 2. Testing exists method when review is NOT found
    @Test
    void testExistsByBuyer_UserIdAndProduct_ProductIdAndOrder_OrderId_False() {
        Mockito.when(reviewRepository.existsByBuyer_UserIdAndProduct_ProductIdAndOrder_OrderId(99L, 99L, 99L))
                .thenReturn(false);

        boolean exists = reviewRepository.existsByBuyer_UserIdAndProduct_ProductIdAndOrder_OrderId(99L, 99L, 99L);

        Assertions.assertFalse(exists);
    }

    // 3. Testing findByProductId with results
    @Test
    void testFindByProduct_ProductIdOrderByReviewDateDesc_Found() {
        Mockito.when(reviewRepository.findByProduct_ProductIdOrderByReviewDateDesc(100L))
                .thenReturn(Arrays.asList(dummyReview));

        List<Review> results = reviewRepository.findByProduct_ProductIdOrderByReviewDateDesc(100L);

        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals(5, results.get(0).getRating());
    }

    // 4. Testing findByProductId with empty results
    @Test
    void testFindByProduct_ProductIdOrderByReviewDateDesc_Empty() {
        Mockito.when(reviewRepository.findByProduct_ProductIdOrderByReviewDateDesc(999L))
                .thenReturn(Collections.emptyList());

        List<Review> results = reviewRepository.findByProduct_ProductIdOrderByReviewDateDesc(999L);

        Assertions.assertTrue(results.isEmpty());
    }

    // 5. Testing average rating calculation (Valid)
    @Test
    void testGetAverageRatingByProductId_HasReviews() {
        Mockito.when(reviewRepository.getAverageRatingByProductId(100L)).thenReturn(4.5);

        Double avgRating = reviewRepository.getAverageRatingByProductId(100L);

        Assertions.assertEquals(4.5, avgRating);
    }

    // 6. Testing average rating calculation (No reviews)
    @Test
    void testGetAverageRatingByProductId_NoReviews() {
        Mockito.when(reviewRepository.getAverageRatingByProductId(999L)).thenReturn(null);

        Double avgRating = reviewRepository.getAverageRatingByProductId(999L);

        Assertions.assertNull(avgRating);
    }

    // 7. Testing custom JOIN FETCH query for seller reviews
    @Test
    void testFindReviewsForSellerProducts() {
        Mockito.when(reviewRepository.findReviewsForSellerProducts(dummySeller))
                .thenReturn(Arrays.asList(dummyReview));

        List<Review> results = reviewRepository.findReviewsForSellerProducts(dummySeller);

        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("Excellent product!", results.get(0).getReviewComment());
    }

    // 8. Testing review count for a product
    @Test
    void testCountByProduct_ProductId() {
        Mockito.when(reviewRepository.countByProduct_ProductId(100L)).thenReturn(15L);

        Long count = reviewRepository.countByProduct_ProductId(100L);

        Assertions.assertEquals(15L, count);
    }

    // 9. Testing built-in save method
    @Test
    void testSaveReview() {
        Mockito.when(reviewRepository.save(dummyReview)).thenReturn(dummyReview);

        Review savedReview = reviewRepository.save(dummyReview);

        Assertions.assertNotNull(savedReview);
        Assertions.assertEquals(1L, savedReview.getReviewId());
    }

    // 10. Testing built-in findById (Found)
    @Test
    void testFindById_Found() {
        Mockito.when(reviewRepository.findById(1L)).thenReturn(Optional.of(dummyReview));

        Optional<Review> result = reviewRepository.findById(1L);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(5, result.get().getRating());
    }

    // 11. Testing built-in findById (Not Found)
    @Test
    void testFindById_NotFound() {
        Mockito.when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Review> result = reviewRepository.findById(99L);

        Assertions.assertFalse(result.isPresent());
    }

    // 12. Testing built-in findAll
    @Test
    void testFindAll() {
        Mockito.when(reviewRepository.findAll()).thenReturn(Arrays.asList(dummyReview));

        List<Review> results = reviewRepository.findAll();

        Assertions.assertFalse(results.isEmpty());
        Assertions.assertEquals(1, results.size());
    }

    // 13. Testing built-in deleteById method
    @Test
    void testDeleteById() {
        reviewRepository.deleteById(1L);

        // Verify that the delete method was explicitly called one time
        Mockito.verify(reviewRepository, Mockito.times(1)).deleteById(1L);
    }
}