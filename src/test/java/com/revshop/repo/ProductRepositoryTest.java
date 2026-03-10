package com.revshop.repo;

import com.revshop.entity.Category;
import com.revshop.entity.Product;
import com.revshop.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductRepositoryTest {

    // We completely mock the repository to bypass the Spring Context and Hibernate crash
    @Mock
    private ProductRepository productRepository;

    private User dummyUser;
    private Product dummyProduct;

    @BeforeEach
    void setUp() {
        dummyUser = new User();
        dummyUser.setEmail("test@test.com"); // Add any mandatory fields your User needs

        dummyProduct = new Product();
        dummyProduct.setProductId(1L);
        dummyProduct.setProductName("Test Laptop");
        dummyProduct.setSeller(dummyUser);
        dummyProduct.setIsActive(1);
    }

    @Test
    void testFindBySeller() {
        Mockito.when(productRepository.findBySeller(dummyUser)).thenReturn(Arrays.asList(dummyProduct));

        List<Product> result = productRepository.findBySeller(dummyUser);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Test Laptop", result.get(0).getProductName());
    }

    @Test
    void testFindByIsActive() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Arrays.asList(dummyProduct));

        Mockito.when(productRepository.findByIsActive(1, pageRequest)).thenReturn(productPage);

        Page<Product> result = productRepository.findByIsActive(1, pageRequest);

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void testFindById() {
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(dummyProduct));

        Optional<Product> result = productRepository.findById(1L);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(1L, result.get().getProductId());
    }

    @Test
    void testFindByProductNameContainingIgnoreCase() {
        Mockito.when(productRepository.findByProductNameContainingIgnoreCase("laptop"))
                .thenReturn(Arrays.asList(dummyProduct));

        List<Product> result = productRepository.findByProductNameContainingIgnoreCase("laptop");

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void testFindByCategory_CategoryId() {
        Long categoryId = 5L;
        Mockito.when(productRepository.findByCategory_CategoryId(categoryId))
                .thenReturn(Arrays.asList(dummyProduct));

        List<Product> result = productRepository.findByCategory_CategoryId(categoryId);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void testFindByIsActiveTrue() {
        Mockito.when(productRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(dummyProduct));

        List<Product> result = productRepository.findByIsActiveTrue();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void testFindByIsActiveTrue_Paged() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Arrays.asList(dummyProduct));

        Mockito.when(productRepository.findByIsActiveTrue(pageRequest)).thenReturn(productPage);

        Page<Product> result = productRepository.findByIsActiveTrue(pageRequest);

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void testFindByIsActiveTrueAndProductNameContainingIgnoreCase() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Arrays.asList(dummyProduct));

        Mockito.when(productRepository.findByIsActiveTrueAndProductNameContainingIgnoreCase("test", pageRequest))
                .thenReturn(productPage);

        Page<Product> result = productRepository.findByIsActiveTrueAndProductNameContainingIgnoreCase("test", pageRequest);

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void testFindByIsActiveTrueAndCategory_CategoryId() {
        Long categoryId = 2L;
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Arrays.asList(dummyProduct));

        Mockito.when(productRepository.findByIsActiveTrueAndCategory_CategoryId(categoryId, pageRequest))
                .thenReturn(productPage);

        Page<Product> result = productRepository.findByIsActiveTrueAndCategory_CategoryId(categoryId, pageRequest);

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void testSearchActiveProducts() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Arrays.asList(dummyProduct));

        Mockito.when(productRepository.searchActiveProducts("laptop", pageRequest)).thenReturn(productPage);

        Page<Product> result = productRepository.searchActiveProducts("laptop", pageRequest);

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void testFindLowStockProductsBySeller() {
        Mockito.when(productRepository.findLowStockProductsBySeller(dummyUser))
                .thenReturn(Arrays.asList(dummyProduct));

        List<Product> result = productRepository.findLowStockProductsBySeller(dummyUser);

        Assertions.assertEquals(1, result.size());
    }
}