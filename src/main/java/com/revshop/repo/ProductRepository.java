package com.revshop.repo;

import com.revshop.entity.Product;
import com.revshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * @param seller
     * @return List<Product>
     */
    List<Product> findBySeller(User seller);

    /**
     * @param isActive
     * @param pageable
     * @return Page<Product>
     */
    Page<Product> findByIsActive(Integer isActive, Pageable pageable);

    /**
     * @param id
     * @return Optional<Product>
     */
    Optional<Product> findById(Long id);

    // 🔎 Search by product name (keyword)
    List<Product> findByProductNameContainingIgnoreCase(String keyword);

    // 📂 Filter by Category ID
    List<Product> findByCategory_CategoryId(Long categoryId);

    // (Optional) Only active products
    List<Product> findByIsActiveTrue();
    Page<Product> findByIsActiveTrue(PageRequest pageable);

    Page<Product> findByIsActiveTrueAndProductNameContainingIgnoreCase(
            String keyword,
            PageRequest pageable
    );

    Page<Product> findByIsActiveTrueAndCategory_CategoryId(
            Long categoryId,
            PageRequest pageable
    );
    @Query("""
    SELECT p FROM Product p
    WHERE p.isActive = 1 AND (
        LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(p.manufacturer) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(p.category.categoryName) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
""")
    Page<Product> searchActiveProducts(@Param("keyword") String keyword,
                                       Pageable pageable);
    @Query("""
           SELECT p FROM Product p
           WHERE p.seller = :seller
           AND p.stock <= p.stockThreshold
           AND p.isActive = 1
           """)

    List<Product> findLowStockProductsBySeller(User seller);


    List<Product>
    findByCategoryCategoryIdAndIsActive(
            Long categoryId,
            Integer isActive
    );
}
