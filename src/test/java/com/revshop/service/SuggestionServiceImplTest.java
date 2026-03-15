package com.revshop.service;

import com.revshop.entity.*;
import com.revshop.repo.*;
import com.revshop.serviceImpl.SuggestionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SuggestionServiceImplTest {

    @Mock
    private ProductViewRepo viewRepo;

    @Mock
    private OrderItemRepository orderRepo;

    @Mock
    private ProductRepository productRepo;

    @InjectMocks
    private SuggestionServiceImpl suggestionService;

    // ==========================================
    // 1. suggestByView Tests (2 Branches)
    // ==========================================

    @Test
    void testSuggestByView_EmptyViews() {
        // Setup: User has no viewed products
        User user = new User();
        when(viewRepo.findByUserOrderByViewTimeDesc(user)).thenReturn(new ArrayList<>());

        // Action
        List<Product> result = suggestionService.suggestByView(user);

        // Check: Should return an empty list
        assertTrue(result.isEmpty());
    }

    @Test
    void testSuggestByView_WithViews() {
        // Setup: User has viewed a product
        User user = new User();
        Product product = new Product();
        Category category = new Category();
        category.setCategoryId(10L);
        product.setCategory(category);

        ProductView view = new ProductView();
        view.setProduct(product);

        // Mocks
        when(viewRepo.findByUserOrderByViewTimeDesc(user)).thenReturn(List.of(view));
        // Mock the product repository to return a list of suggested products for Category 10
        when(productRepo.findByCategoryCategoryIdAndIsActive(10L, 1)).thenReturn(List.of(product));

        // Action
        List<Product> result = suggestionService.suggestByView(user);

        // Check: Should return the suggested products
        assertEquals(1, result.size());
        verify(productRepo).findByCategoryCategoryIdAndIsActive(10L, 1);
    }

    // ==========================================
    // 2. suggestByOrder Tests (2 Branches)
    // ==========================================

    @Test
    void testSuggestByOrder_EmptyOrders() {
        // Setup: User has no orders
        User user = new User();
        when(orderRepo.findByOrderBuyerOrderByOrderItemIdDesc(user)).thenReturn(new ArrayList<>());

        // Action
        List<Product> result = suggestionService.suggestByOrder(user);

        // Check: Should return an empty list
        assertTrue(result.isEmpty());
    }

    @Test
    void testSuggestByOrder_WithOrders() {
        // Setup: User has an ordered product
        User user = new User();
        Product product = new Product();
        Category category = new Category();
        category.setCategoryId(20L);
        product.setCategory(category);

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);

        // Mocks
        when(orderRepo.findByOrderBuyerOrderByOrderItemIdDesc(user)).thenReturn(List.of(orderItem));
        // Mock the product repository to return a list of suggested products for Category 20
        when(productRepo.findByCategoryCategoryIdAndIsActive(20L, 1)).thenReturn(List.of(product));

        // Action
        List<Product> result = suggestionService.suggestByOrder(user);

        // Check: Should return the suggested products
        assertEquals(1, result.size());
        verify(productRepo).findByCategoryCategoryIdAndIsActive(20L, 1);
    }
}