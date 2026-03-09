package com.revshop.controller;

import com.revshop.entity.Product;
import com.revshop.entity.Review;
import com.revshop.entity.User;
import com.revshop.entity.SellerDetails;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.revshop.serviceInterfaces.*;
import com.revshop.repo.*;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SellerController.class)
@AutoConfigureMockMvc
class SellerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private ProductService productService;
    @MockBean private UserService userService;
    @MockBean private CategoryRepository categoryRepository;
    @MockBean private SellerService sellerService;
    @MockBean private PaymentRepository paymentRepository;
    @MockBean private OrderAddressRepository orderAddressRepository;
    @MockBean private OrderItemRepository orderItemRepository;
    @MockBean private ReviewRepository reviewRepository;
    @MockBean private LowStockService lowStockService;
    @MockBean private NotificationService notificationService;
    @MockBean private OrderService orderService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        // Setup a mock user to prevent NullPointerExceptions
        mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setEmail("seller@test.com");

        // Tell Mockito how to respond when the controller asks for data
        Mockito.when(userService.findByEmail(anyString())).thenReturn(mockUser);
        Mockito.when(categoryRepository.findAll()).thenReturn(Collections.emptyList());
        Mockito.when(sellerService.getSellerDetails(anyLong())).thenReturn(new SellerDetails());
        Mockito.when(reviewRepository.findReviewsForSellerProducts(mockUser)).thenReturn(Collections.emptyList());
    }

    // 1 Dashboard
    @Test
    void testDashboard() throws Exception {
        mockMvc.perform(get("/seller/dashboard")
                        .with(user("seller@test.com")))
                .andExpect(status().isOk())
                .andExpect(view().name("seller/dashboard"));
    }

    // 2 Add Product Page
    @Test
    void testAddProductPage() throws Exception {
        mockMvc.perform(get("/seller/add-product")
                        .with(user("seller@test.com")))
                .andExpect(status().isOk())
                .andExpect(view().name("seller/add-product"));
    }

    // 3 View Products
    @Test
    void testViewProducts() throws Exception {
        mockMvc.perform(get("/seller/my-products")
                        .with(user("seller@test.com")))
                .andExpect(status().isOk())
                .andExpect(view().name("seller/my-products"));
    }

    // 4 Show Edit Product
    @Test
    void testShowEditProduct() throws Exception {
        mockMvc.perform(get("/seller/edit-product/1")
                        .with(user("seller@test.com")))
                .andExpect(status().is3xxRedirection()); // Redirects to ?error because product is null in mock
    }

    // 5 Delete Product
    @Test
    void testDeleteProduct() throws Exception {
        mockMvc.perform(get("/seller/delete-product/1")
                        .with(user("seller@test.com")))
                .andExpect(status().is3xxRedirection());
    }

    // 6 Save Product
    @Test
    void testSaveProduct() throws Exception {
        mockMvc.perform(post("/seller/save-product")
                        .with(user("seller@test.com"))
                        .with(csrf())
                        .param("productName","Test Product")
                        .param("price","100"))
                .andExpect(status().isOk()); // Returns 200 because validation fails (no category selected in the test params)
    }

    // 7 Update Product (Fixed URL to /seller/update/{id})
    @Test
    void testUpdateProduct() throws Exception {
        mockMvc.perform(post("/seller/update/1")
                        .with(user("seller@test.com"))
                        .with(csrf())
                        .param("productName","Updated Product")
                        .param("description","desc")
                        .param("manufacturer","brand")
                        .param("mrp","100")
                        .param("discount","5")
                        .param("sellingPrice","95")
                        .param("stock","10")
                        .param("stockThreshold","2"))
                .andExpect(status().is3xxRedirection());
    }

    // 8 Seller Profile
    @Test
    void testSellerProfile() throws Exception {
        mockMvc.perform(get("/seller/profile")
                        .with(user("seller@test.com")))
                .andExpect(status().isOk())
                .andExpect(view().name("seller/seller-profile"));
    }

    // 9 Update Profile (Fixed URL to /seller/profile)
    @Test
    void testUpdateProfile() throws Exception {
        mockMvc.perform(post("/seller/profile")
                        .with(user("seller@test.com"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()); // Redirects on success
    }

    // 10 Notifications Page
    @Test
    void testNotifications() throws Exception {
        mockMvc.perform(get("/seller/notifications")
                        .with(user("seller@test.com")))
                .andExpect(status().isOk())
                .andExpect(view().name("seller/notifications"));
    }

    // 11 Clear Notifications (Fixed URL to /seller/notifications/clear-all)
    @Test
    void testClearNotifications() throws Exception {
        mockMvc.perform(get("/seller/notifications/clear-all")
                        .with(user("seller@test.com")))
                .andExpect(status().is3xxRedirection());
    }

    // 12 Orders Page
    @Test
    void testSellerOrders() throws Exception {
        mockMvc.perform(get("/seller/orders")
                        .with(user("seller@test.com")))
                .andExpect(status().isOk())
                .andExpect(view().name("seller/orders"));
    }

    // 13 Mark Order Delivered (Fixed URL & Method to POST /seller/orders/deliver/{orderId})
    @Test
    void testMarkOrderDelivered() throws Exception {
        mockMvc.perform(post("/seller/orders/deliver/1")
                        .with(user("seller@test.com"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    // 14 Reviews Page
    @Test
    void testSellerReviews() throws Exception {
        mockMvc.perform(get("/seller/reviews")
                        .with(user("seller@test.com")))
                .andExpect(status().isOk())
                .andExpect(view().name("seller/reviews"));
    }

    // 15 Product Reviews (Fixed URL to /seller/reviews/product/{id})
    // 15 Product Reviews
    // 15 Product Reviews
    @Test
    void testViewAllReviewsForProduct() throws Exception {
        // 1. Create a fake product
        Product mockProduct = new Product();
        mockProduct.setProductId(1L);
        mockProduct.setProductName("Test Review Product");

        // 2. Create a fake buyer (THIS FIXES THE EL1007E ERROR)
        User mockBuyer = new User();
        mockBuyer.setEmail("buyer@test.com");

        // 3. Create a fake review attached to the product AND the buyer
        Review mockReview = new Review();
        mockReview.setReviewId(1L);
        mockReview.setRating(5);
        mockReview.setProduct(mockProduct);
        mockReview.setBuyer(mockBuyer);

        // 4. Tell the repository to return a list containing our fake review
        Mockito.when(reviewRepository.findByProduct_ProductIdOrderByReviewDateDesc(1L))
                .thenReturn(java.util.List.of(mockReview));

        // Also provide mock values for the aggregations so they don't return null
        Mockito.when(reviewRepository.getAverageRatingByProductId(1L)).thenReturn(5.0);
        Mockito.when(reviewRepository.countByProduct_ProductId(1L)).thenReturn(1L);

        // 5. Perform the test
        mockMvc.perform(get("/seller/reviews/product/1")
                        .with(user("seller@test.com")))
                .andExpect(status().isOk())
                .andExpect(view().name("seller/product-reviews"));
    }

    // 16 Seller Analytics
    @Test
    void testSellerAnalytics() throws Exception {
        mockMvc.perform(get("/seller/sales")
                        .with(user("seller@test.com")))
                .andExpect(status().isOk())
                .andExpect(view().name("seller/sales"));
    }

    // 17 Unread Notification Count
    @Test
    void testUnreadNotificationCount() throws Exception {
        mockMvc.perform(get("/seller/notifications/unread-count")
                        .with(user("seller@test.com")))
                .andExpect(status().isOk());
    }

    // 18 Mark Notification Read
    @Test
    void testMarkNotificationRead() throws Exception {
        mockMvc.perform(get("/seller/notifications/read/1")
                        .with(user("seller@test.com")))
                .andExpect(status().is3xxRedirection());
    }
}