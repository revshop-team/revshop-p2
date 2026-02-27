package com.revshop.repo;

import com.revshop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * @param buyerId
     * @param productId
     * @param orderId
     * @return boolean
     */
    boolean existsByBuyer_UserIdAndProduct_ProductIdAndOrder_OrderId(
            Long buyerId,
            Long productId,
            Long orderId
    );

    /**
     *
     * @param productId
     * @return List<Review>
     */
    List<Review> findByProduct_ProductIdOrderByReviewDateDesc(Long productId);
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.productId = :productId")
    Double getAverageRatingByProductId(Long productId);

    Long countByProduct_ProductId(Long productId);
}
