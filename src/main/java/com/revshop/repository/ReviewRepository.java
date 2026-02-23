package com.revshop.repository;

import com.revshop.entity.Product;
import com.revshop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Get reviews for a product
    List<Review> findByProduct(Product product);
}