package com.revshop.repository;

import com.revshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Browse products by category
    List<Product> findByCategory_CategoryId(Long categoryId);

    // Search products by name
    List<Product> findByProductNameContainingIgnoreCase(String keyword);

    // Get products by seller (Seller dashboard)
    List<Product> findBySeller_UserId(Long sellerId);
    // Get only active products (for buyer browsing)
    List<Product> findByIsActive(Integer isActive);
}