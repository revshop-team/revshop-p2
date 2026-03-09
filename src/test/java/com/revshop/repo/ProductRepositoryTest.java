package com.revshop.repo;

import com.revshop.entity.Category;
import com.revshop.entity.Product;
import com.revshop.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired private TestEntityManager entityManager;
    @Autowired private ProductRepository productRepository;

    private User testSeller;
    private Category testCategory;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // 1. Create a Seller
        testSeller = new User();
        testSeller.setEmail("seller_" + System.currentTimeMillis() + "@test.com");
        testSeller.setPassword("pass");
        testSeller.setRole("SELLER");
        testSeller = entityManager.persistAndFlush(testSeller);

        // 2. Create a Category
        testCategory = new Category();
        testCategory.setCategoryName("Tech_" + System.currentTimeMillis());
        testCategory = entityManager.persistAndFlush(testCategory);

        // 3. Create a Baseline Product
        testProduct = new Product();
        testProduct.setSeller(testSeller);
        testProduct.setCategory(testCategory);
        testProduct.setProductName("Gaming Laptop");
        testProduct.setManufacturer("RevBrand");
        testProduct.setMrp(1000.0);
        testProduct.setSellingPrice(900.0);
        testProduct.setStock(50);
        testProduct.setStockThreshold(10);
        testProduct.setIsActive(1);
        testProduct = entityManager.persistAndFlush(testProduct);
    }

    // 1. Test Custom Search Query (JPQL)
    @Test
    void testSearchActiveProducts_ShouldFindByName() {
        Page<Product> result = productRepository.searchActiveProducts("gaming", PageRequest.of(0, 10));
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getProductName()).containsIgnoringCase("Gaming");
    }

    // 2. Test Low Stock Filter (JPQL)
    @Test
    void testFindLowStockProductsBySeller_ShouldReturnProduct() {
        // Update stock to be below threshold
        testProduct.setStock(5);
        entityManager.persistAndFlush(testProduct);

        List<Product> lowStock = productRepository.findLowStockProductsBySeller(testSeller);
        assertThat(lowStock).hasSize(1);
    }

    // 3. Test findByIsActive (Standard Method)
    @Test
    void testFindByIsActive_ShouldReturnActiveOnly() {
        List<Product> activeProducts = productRepository.findByIsActive(1);
        assertThat(activeProducts).isNotEmpty();
    }

    // 4. Test findByCategory_CategoryId
    @Test
    void testFindByCategory_ShouldReturnProducts() {
        List<Product> products = productRepository.findByCategory_CategoryId(testCategory.getCategoryId());
        assertThat(products).isNotEmpty();
        assertThat(products.get(0).getCategory().getCategoryId()).isEqualTo(testCategory.getCategoryId());
    }

    // 5. Test findBySeller
    @Test
    void testFindBySeller_ShouldReturnSellerProducts() {
        List<Product> products = productRepository.findBySeller(testSeller);
        assertThat(products).isNotEmpty();
    }

    // 6. Test Keyword Search Ignore Case
    @Test
    void testFindByProductNameContainingIgnoreCase() {
        List<Product> products = productRepository.findByProductNameContainingIgnoreCase("LAPTOP");
        assertThat(products).isNotEmpty();
    }

    // 7. Test Inactive Filter
    @Test
    void testSearchActiveProducts_ShouldNotFindInactive() {
        testProduct.setIsActive(0); // Set to inactive
        entityManager.persistAndFlush(testProduct);

        Page<Product> result = productRepository.searchActiveProducts("Gaming", PageRequest.of(0, 10));
        assertThat(result.getContent()).isEmpty();
    }

    // 8. Test Update Stock
    @Test
    void testUpdateStock_ShouldPersist() {
        Product toUpdate = productRepository.findById(testProduct.getProductId()).get();
        toUpdate.setStock(100);
        productRepository.save(toUpdate);
        entityManager.flush();

        Product updated = productRepository.findById(testProduct.getProductId()).get();
        assertThat(updated.getStock()).isEqualTo(100);
    }

    // 9. Test Delete Product
    @Test
    void testDeleteProduct() {
        productRepository.delete(testProduct);
        entityManager.flush();
        assertThat(productRepository.findById(testProduct.getProductId())).isNotPresent();
    }

    // 10. Test Search by Manufacturer (JPQL Coverage)
    @Test
    void testSearchActiveProducts_ByManufacturer() {
        Page<Product> result = productRepository.searchActiveProducts("RevBrand", PageRequest.of(0, 10));
        assertThat(result.getContent()).isNotEmpty();
    }

    // 11. Test Null Constraint (Product Name)
    @Test
    void testSaveProduct_NullName_ShouldFail() {
        Product badProduct = new Product();
        badProduct.setProductName(null);
        badProduct.setSeller(testSeller);
        badProduct.setManufacturer("Fail");

        try {
            productRepository.save(badProduct);
            entityManager.flush();
        } catch (Exception e) {
            return; // Success
        }
        assertThat(false).as("Expected null constraint violation").isTrue();
    }

    // 12. Test Pagination
    @Test
    void testFindByIsActive_WithPagination() {
        Page<Product> page = productRepository.findByIsActive(1, PageRequest.of(0, 1));
        assertThat(page.getSize()).isEqualTo(1);
    }
}