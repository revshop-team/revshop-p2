package com.revshop.service;

import com.revshop.entity.Product;
import com.revshop.entity.User;
import com.revshop.exceptions.ProductNotFoundException;
import com.revshop.repo.ProductRepository;
import com.revshop.serviceImpl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock private ProductRepository productRepository;
    @InjectMocks private ProductServiceImpl productService;

    private Product testProduct;
    private User testSeller;

    @BeforeEach
    void setUp() {
        testSeller = new User();
        testSeller.setUserId(1L);

        testProduct = new Product();
        testProduct.setProductId(100L);
        testProduct.setProductName("Gaming Laptop");
        testProduct.setIsActive(1);
    }

    // 1. Success: Save Product
    @Test
    void testSaveProduct_Success() {
        productService.saveProduct(testProduct, testSeller);
        assertThat(testProduct.getSeller()).isEqualTo(testSeller);
        verify(productRepository).save(testProduct);
    }

    // 2. Success: Get Product By ID
    @Test
    void testGetProductById_Success() {
        when(productRepository.findById(100L)).thenReturn(Optional.of(testProduct));
        Product result = productService.getProductById(100L);
        assertThat(result.getProductName()).isEqualTo("Gaming Laptop");
    }

    // 3. Failure: Get Product By ID (Exception)
    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // 4. Logic: Soft Delete (IsActive 1 -> 0)
    @Test
    void testDeleteProduct_SoftDeleteLogic() {
        when(productRepository.findById(100L)).thenReturn(Optional.of(testProduct));
        productService.deleteProductById(100L);
        assertThat(testProduct.getIsActive()).isEqualTo(0);
        verify(productRepository).save(testProduct);
    }

    // 5. Success: Search with Keyword
    @Test
    void testSearchProducts_WithKeyword() {
        when(productRepository.findByProductNameContainingIgnoreCase("Laptop"))
                .thenReturn(List.of(testProduct));
        List<Product> results = productService.searchProducts("Laptop");
        assertThat(results).hasSize(1);
    }

    // 6. Branch: Search with Null/Empty Keyword
    @Test
    void testSearchProducts_EmptyKeyword() {
        when(productRepository.findAll()).thenReturn(List.of(testProduct));
        List<Product> results = productService.searchProducts("");
        verify(productRepository).findAll();
    }

    // 7. Success: Get 12 Products (Stream Limit)
    @Test
    void testGet12Products_LimitCheck() {
        List<Product> products = new ArrayList<>();
        for(int i=0; i<15; i++) products.add(new Product());
        when(productRepository.findByIsActive(1)).thenReturn(products);

        List<Product> result = productService.get12Products();
        assertThat(result).hasSize(12);
    }

    // 8. Success: Pagination Active Products
    @Test
    void testGetActiveProducts_Paginated() {
        PageRequest pageable = PageRequest.of(0, 5);
        when(productRepository.findByIsActive(1, pageable)).thenReturn(Page.empty());
        productService.getActiveProducts(pageable);
        verify(productRepository).findByIsActive(1, pageable);
    }

    // 9. Logic: Search Active Products (Blank Keyword)
    @Test
    void testSearchActiveProducts_Blank() {
        PageRequest pageable = PageRequest.of(0, 5);
        productService.searchActiveProducts("   ", pageable);
        verify(productRepository).findByIsActiveTrue(pageable);
    }

    // 10. Success: Search Active Products (Keyword)
    @Test
    void testSearchActiveProducts_WithKeyword() {
        PageRequest pageable = PageRequest.of(0, 5);
        productService.searchActiveProducts("Phone", pageable);
        verify(productRepository).searchActiveProducts("Phone", pageable);
    }

    // 11. Success: Get Products By Category
    @Test
    void testGetProductsByCategory() {
        productService.getProductsByCategory(1L);
        verify(productRepository).findByCategory_CategoryId(1L);
    }

    // 12. Success: Save or Update
    @Test
    void testSaveOrUpdateProduct() {
        productService.saveOrUpdateProduct(testProduct);
        verify(productRepository).save(testProduct);
    }

    // --- NEW: 4 ADDITIONAL CASES FOR HIGHER COVERAGE ---

    // 13. Success: Get All Active Products (List version)
    @Test
    void testGetAllActiveProducts() {
        when(productRepository.findByIsActive(1)).thenReturn(List.of(testProduct));
        List<Product> results = productService.getAllActiveProducts();
        assertThat(results).contains(testProduct);
    }

    // 14. Success: Get Active Products By Category (Paginated)
    @Test
    void testGetActiveProductsByCategory_Paginated() {
        PageRequest pageable = PageRequest.of(0, 10);
        productService.getActiveProductsByCategory(1L, pageable);
        verify(productRepository).findByIsActiveTrueAndCategory_CategoryId(1L, pageable);
    }

    // 15. Success: Get Product By Seller
    @Test
    void testGetProductBySeller() {
        productService.getProductBySeller(testSeller);
        verify(productRepository).findBySeller(testSeller);
    }

    // 16. Success: Get All Categories Product (FindAll)
    @Test
    void testGetAllCategoriesProduct() {
        productService.getAllCategoriesProduct();
        verify(productRepository).findAll();
    }
}