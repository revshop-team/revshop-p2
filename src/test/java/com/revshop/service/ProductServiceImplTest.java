package com.revshop.service;

import com.revshop.entity.Product;
import com.revshop.entity.User;
import com.revshop.exceptions.ProductNotFoundException;
import com.revshop.repo.ProductRepository;
import com.revshop.serviceImpl.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void testSaveProduct() {
        Product product = new Product();
        User seller = new User();

        // The save method doesn't return anything, so we just call it
        productService.saveProduct(product, seller);

        // Verify that the required fields were set before saving
        Assertions.assertEquals(seller, product.getSeller());
        Assertions.assertEquals(1, product.getIsActive());
        Assertions.assertNotNull(product.getCreatedAt());

        // Verify repository save was triggered
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
    }

    @Test
    void testGetProductBySeller() {
        User seller = new User();
        Product product = new Product();
        Mockito.when(productRepository.findBySeller(seller)).thenReturn(Arrays.asList(product));

        List<Product> result = productService.getProductBySeller(seller);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void testGetAllCategoriesProduct() {
        Product product = new Product();
        Mockito.when(productRepository.findAll()).thenReturn(Arrays.asList(product));

        List<Product> result = productService.getAllCategoriesProduct();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void testGetActiveProducts() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> page = new PageImpl<>(Arrays.asList(new Product()));

        Mockito.when(productRepository.findByIsActive(1, pageable)).thenReturn(page);

        Page<Product> result = productService.getActiveProducts(pageable);

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void testGetProductById_Success() {
        Product product = new Product();
        product.setProductId(1L);
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        Assertions.assertEquals(1L, result.getProductId());
    }

    @Test
    void testGetProductById_ThrowsException() {
        Mockito.when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(99L);
        });
    }

    @Test
    void testGet12Products() {
        // Create 15 dummy products to ensure the limit(12) logic works
        List<Product> manyProducts = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            manyProducts.add(new Product());
        }

        Mockito.when(productRepository.findByIsActiveTrue()).thenReturn(manyProducts);

        List<Product> result = productService.get12Products();

        // Verify only 12 were returned
        Assertions.assertEquals(12, result.size());
    }

    @Test
    void testSaveOrUpdateProduct() {
        Product product = new Product();
        Mockito.when(productRepository.save(product)).thenReturn(product);

        Product result = productService.saveOrUpdateProduct(product);

        Assertions.assertNotNull(result);
    }

    @Test
    void testDeleteProductById_Success() {
        Product product = new Product();
        product.setProductId(1L);
        product.setIsActive(1);

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProductById(1L);

        // Verify the logic changed isActive to 0 (soft delete)
        Assertions.assertEquals(0, product.getIsActive());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
    }

    @Test
    void testDeleteProductById_ThrowsException() {
        Mockito.when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> {
            productService.deleteProductById(99L);
        });
    }

    @Test
    void testSearchProducts_WithKeyword() {
        String keyword = "laptop";
        Product product = new Product();
        Mockito.when(productRepository.findByProductNameContainingIgnoreCase(keyword))
                .thenReturn(Arrays.asList(product));

        List<Product> result = productService.searchProducts(keyword);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void testSearchProducts_NullOrEmptyKeyword() {
        Product product = new Product();
        Mockito.when(productRepository.findAll()).thenReturn(Arrays.asList(product));

        // Test with null
        List<Product> resultNull = productService.searchProducts(null);
        Assertions.assertEquals(1, resultNull.size());

        // Test with empty string
        List<Product> resultEmpty = productService.searchProducts("");
        Assertions.assertEquals(1, resultEmpty.size());
    }

    @Test
    void testGetProductsByCategory() {
        Long categoryId = 1L;
        Product product = new Product();
        Mockito.when(productRepository.findByCategory_CategoryId(categoryId)).thenReturn(Arrays.asList(product));

        List<Product> result = productService.getProductsByCategory(categoryId);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void testGetAllActiveProducts() {
        Product product = new Product();
        Mockito.when(productRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(product));

        List<Product> result = productService.getAllActiveProducts();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void testSearchActiveProducts_WithKeyword() {
        String keyword = "mouse";
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(Arrays.asList(new Product()));

        Mockito.when(productRepository.searchActiveProducts(keyword, pageRequest)).thenReturn(page);

        Page<Product> result = productService.searchActiveProducts(keyword, pageRequest);

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void testSearchActiveProducts_NullOrEmptyKeyword() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(Arrays.asList(new Product()));

        Mockito.when(productRepository.findByIsActiveTrue(pageRequest)).thenReturn(page);

        // Test with null
        Page<Product> resultNull = productService.searchActiveProducts(null, pageRequest);
        Assertions.assertEquals(1, resultNull.getContent().size());

        // Test with spaces
        Page<Product> resultEmpty = productService.searchActiveProducts("   ", pageRequest);
        Assertions.assertEquals(1, resultEmpty.getContent().size());
    }

    @Test
    void testGetActiveProductsByCategory() {
        Long categoryId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(Arrays.asList(new Product()));

        Mockito.when(productRepository.findByIsActiveTrueAndCategory_CategoryId(categoryId, pageRequest))
                .thenReturn(page);

        Page<Product> result = productService.getActiveProductsByCategory(categoryId, pageRequest);

        Assertions.assertEquals(1, result.getContent().size());
    }
}