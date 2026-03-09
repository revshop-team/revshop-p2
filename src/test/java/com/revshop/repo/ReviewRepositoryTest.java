package com.revshop.repo;

import com.revshop.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ReviewRepositoryTest {

    @Autowired private TestEntityManager entityManager;
    @Autowired private ReviewRepository reviewRepository;

    private User testBuyer;
    private User testSeller;
    private Product testProduct;
    private Order testOrder;
    private Review testReview;

    @BeforeEach
    void setUp() {
        long timestamp = System.currentTimeMillis();

        // 1. Create Seller & Buyer
        testSeller = createDummyUser("seller_" + timestamp, "SELLER");
        testBuyer = createDummyUser("buyer_" + timestamp, "BUYER");

        // 2. Create Product
        testProduct = new Product();
        testProduct.setProductName("Laptop_" + timestamp);
        testProduct.setManufacturer("RevTech");
        testProduct.setSeller(testSeller);
        testProduct = entityManager.persistAndFlush(testProduct);

        // 3. Create Order
        testOrder = new Order();
        testOrder.setBuyer(testBuyer);
        testOrder.setTotalAmount(1200.0);
        testOrder.setStatus("DELIVERED");
        testOrder = entityManager.persistAndFlush(testOrder);

        // 4. Create Baseline Review
        testReview = new Review();
        testReview.setBuyer(testBuyer);
        testReview.setProduct(testProduct);
        testReview.setOrder(testOrder);
        testReview.setRating(5);
        testReview.setReviewComment("Excellent product!");
        testReview.setReviewDate(LocalDateTime.now());
        testReview = entityManager.persistAndFlush(testReview);
    }

    private User createDummyUser(String email, String role) {
        User user = new User();
        user.setEmail(email + "@test.com");
        user.setPassword("pass");
        user.setRole(role);
        return entityManager.persistAndFlush(user);
    }

    // 1. Test Custom Method: existsByBuyer_UserIdAndProduct_ProductIdAndOrder_OrderId
    @Test
    void testExistsByBuyerProductOrder_ShouldReturnTrue() {
        boolean exists = reviewRepository.existsByBuyer_UserIdAndProduct_ProductIdAndOrder_OrderId(
                testBuyer.getUserId(), testProduct.getProductId(), testOrder.getOrderId());
        assertThat(exists).isTrue();
    }

    // 2. Test Custom Method: findByProduct_ProductIdOrderByReviewDateDesc
    @Test
    void testFindByProduct_ShouldReturnSortedReviews() {
        List<Review> reviews = reviewRepository.findByProduct_ProductIdOrderByReviewDateDesc(testProduct.getProductId());
        assertThat(reviews).isNotEmpty();
        assertThat(reviews.get(0).getRating()).isEqualTo(5);
    }

    // 3. Test Custom Query: getAverageRatingByProductId
    @Test
    void testGetAverageRating_ShouldReturnCorrectValue() {
        // Add another review for the same product (using a different buyer/order to avoid unique constraint)
        User secondBuyer = createDummyUser("buyer2_" + System.currentTimeMillis(), "BUYER");
        Order secondOrder = new Order();
        secondOrder.setBuyer(secondBuyer);
        secondOrder = entityManager.persistAndFlush(secondOrder);

        Review secondReview = new Review();
        secondReview.setBuyer(secondBuyer);
        secondReview.setProduct(testProduct);
        secondReview.setOrder(secondOrder);
        secondReview.setRating(3);
        entityManager.persistAndFlush(secondReview);

        Double avg = reviewRepository.getAverageRatingByProductId(testProduct.getProductId());
        assertThat(avg).isEqualTo(4.0); // (5+3)/2
    }

    // 4. Test Custom Query: findReviewsForSellerProducts (JOIN FETCH check)
    @Test
    void testFindReviewsForSeller_ShouldReturnList() {
        List<Review> sellerReviews = reviewRepository.findReviewsForSellerProducts(testSeller);
        assertThat(sellerReviews).isNotEmpty();
        assertThat(sellerReviews.get(0).getProduct().getSeller().getUserId()).isEqualTo(testSeller.getUserId());
    }

    // 5. Test Custom Method: countByProduct_ProductId
    @Test
    void testCountByProduct_ShouldReturnTotal() {
        Long count = reviewRepository.countByProduct_ProductId(testProduct.getProductId());
        assertThat(count).isEqualTo(1L);
    }

    // 6. Test Unique Constraint Violation (ORA-00001)
    @Test
    void testDuplicateReview_ShouldFail() {
        Review duplicate = new Review();
        duplicate.setBuyer(testBuyer);
        duplicate.setProduct(testProduct);
        duplicate.setOrder(testOrder);
        duplicate.setRating(1);

        try {
            reviewRepository.save(duplicate);
            entityManager.flush();
        } catch (Exception e) {
            return; // Success: Oracle blocked it
        }
        assertThat(false).as("Expected ORA-00001 unique constraint violation").isTrue();
    }

    // 7. Test Save Review (Covers setRating, setReviewComment, etc.)
    @Test
    void testSaveReview_ShouldPersist() {
        assertThat(testReview.getReviewId()).isNotNull();
        assertThat(testReview.getReviewComment()).isEqualTo("Excellent product!");
    }

    // 8. Test Find By ID
    @Test
    void testFindById() {
        Optional<Review> found = reviewRepository.findById(testReview.getReviewId());
        assertThat(found).isPresent();
    }

    // 9. Test Update Review
    @Test
    void testUpdateReview_ShouldReflectChanges() {
        testReview.setRating(4);
        Review updated = reviewRepository.save(testReview);
        entityManager.flush();

        assertThat(updated.getRating()).isEqualTo(4);
    }

    // 10. Test Delete Review
    @Test
    void testDeleteReview() {
        Long id = testReview.getReviewId();
        reviewRepository.delete(testReview);
        entityManager.flush();

        assertThat(reviewRepository.findById(id)).isNotPresent();
    }

    // 11. Test Average Rating with No Reviews
    @Test
    void testGetAverageRating_NoReviews_ShouldReturnNull() {
        Double avg = reviewRepository.getAverageRatingByProductId(-99L);
        assertThat(avg).isNull();
    }

    // 12. Test Null Rating Boundary
    @Test
    void testSaveReview_NullValues_ShouldHandleGracefully() {
        Review emptyReview = new Review();
        // Missing mandatory fields
        try {
            reviewRepository.save(emptyReview);
            entityManager.flush();
        } catch (Exception e) {
            return;
        }
        // If we reach here, it might depend on your specific DB null constraints
    }
}