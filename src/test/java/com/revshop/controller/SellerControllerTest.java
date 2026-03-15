package com.revshop.controller;

import com.revshop.entity.*;
import com.revshop.repo.*;
import com.revshop.serviceInterfaces.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SellerControllerTest {

    @Mock private ProductService productService;
    @Mock private UserService userService;
    @Mock private CategoryRepository categoryRepository;
    @Mock private SellerService sellerService;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private OrderAddressRepository orderAddressRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private LowStockService lowStockService;
    @Mock private NotificationService notificationService;
    @Mock private OrderService orderService;

    @Mock private Authentication auth;
    @Mock private Model model;
    @Mock private RedirectAttributes redirectAttributes;
    @Mock private MultipartFile imageFile;

    @InjectMocks
    private SellerController sellerController;

    // =================================================================
    // 1. DASHBOARD & PROFILE
    // =================================================================
    @Test
    void testSellerDashboard() {
        User user = setupUserWithBusinessName("Tech Store");
        when(auth.getName()).thenReturn("s@test.com");
        when(userService.findByEmail("s@test.com")).thenReturn(user);

        assertEquals("seller/dashboard", sellerController.sellerDashboard(auth, model));
        verify(model).addAttribute("businessName", "Tech Store");
    }

    @Test
    void testSellerProductsPage() {
        assertEquals("seller/products", sellerController.sellerProducts());
    }

    @Test
    void testShowProfile() {
        User user = setupUserWithBusinessName("Tech Store");
        user.setUserId(1L);
        when(auth.getName()).thenReturn("s@test.com");
        when(userService.findByEmail("s@test.com")).thenReturn(user);
        when(sellerService.getSellerDetails(1L)).thenReturn(new SellerDetails());

        assertEquals("seller/seller-profile", sellerController.showProfile(model, auth));
        verify(model).addAttribute(eq("sellerDetails"), any());
    }

    @Test
    void testShowProfileDetailsNull() {
        User user = setupUserWithBusinessName("Tech Store");
        user.setUserId(1L);
        when(auth.getName()).thenReturn("s@test.com");
        when(userService.findByEmail("s@test.com")).thenReturn(user);
        when(sellerService.getSellerDetails(1L)).thenReturn(null); // Force null path

        assertEquals("seller/seller-profile", sellerController.showProfile(model, auth));
    }

    @Test
    void testUpdateProfileSuccess() {
        when(auth.getName()).thenReturn("s@test.com");
        when(userService.findByEmail("s@test.com")).thenReturn(new User());

        assertEquals("redirect:/seller/profile?success", sellerController.updateProfile(new SellerDetails(), auth, model));
    }

    @Test
    void testUpdateProfileException() {
        when(auth.getName()).thenReturn("s@test.com");
        when(userService.findByEmail("s@test.com")).thenReturn(new User());
        doThrow(new RuntimeException("DB Error")).when(sellerService).saveOrUpdateSeller(any(), any());

        assertEquals("seller/seller-profile", sellerController.updateProfile(new SellerDetails(), auth, model));
        verify(model).addAttribute(eq("error"), anyString());
    }

    // =================================================================
    // 2. PRODUCT MANAGEMENT (ADD/EDIT/DELETE)
    // =================================================================
    @Test
    void testShowAddProductForm() {
        when(auth.getName()).thenReturn("s@test.com");
        when(userService.findByEmail("s@test.com")).thenReturn(setupUserWithBusinessName("Shop"));
        assertEquals("seller/add-product", sellerController.showAddProductForm(model, auth));
    }

    @Test
    void testSaveProductMissingCategory() {
        when(auth.getName()).thenReturn("s@test.com");
        when(userService.findByEmail("s@test.com")).thenReturn(new User());

        Product p = new Product(); // No category
        assertEquals("seller/add-product", sellerController.saveProduct(p, imageFile, "", auth, model, redirectAttributes));
        verify(model).addAttribute("error", "Please select a category.");
    }

    @Test
    void testSaveProductPriceError() {
        Product p = new Product();
        p.setMrp(100.0); p.setSellingPrice(200.0); // Error: Selling > MRP
        Category cat = new Category(); cat.setCategoryId(1L); p.setCategory(cat);

        when(auth.getName()).thenReturn("s@test.com");
        when(userService.findByEmail("s@test.com")).thenReturn(new User());

        assertEquals("seller/add-product", sellerController.saveProduct(p, imageFile, null, auth, model, redirectAttributes));
        verify(model).addAttribute(eq("error"), contains("greater than MRP"));
    }

    @Test
    void testSaveProductDiscountError() {
        Product p = new Product();
        p.setMrp(200.0); p.setSellingPrice(100.0); p.setDiscount(150.0); // Error: Discount > 100
        Category cat = new Category(); cat.setCategoryId(1L); p.setCategory(cat);

        when(auth.getName()).thenReturn("s@test.com");
        when(userService.findByEmail("s@test.com")).thenReturn(new User());

        assertEquals("seller/add-product", sellerController.saveProduct(p, imageFile, null, auth, model, redirectAttributes));
        verify(model).addAttribute(eq("error"), contains("Discount must be between"));
    }

    @Test
    void testSaveProductStockError() {
        Product p = new Product();
        p.setMrp(200.0); p.setSellingPrice(100.0); p.setStock(-5); // Error: Negative stock
        Category cat = new Category(); cat.setCategoryId(1L); p.setCategory(cat);

        when(auth.getName()).thenReturn("s@test.com");
        when(userService.findByEmail("s@test.com")).thenReturn(new User());

        assertEquals("seller/add-product", sellerController.saveProduct(p, imageFile, null, auth, model, redirectAttributes));
        verify(model).addAttribute(eq("error"), contains("Stock cannot be negative"));
    }

    @Test
    void testSaveProductSuccess() {
        Product p = new Product();
        p.setMrp(200.0); p.setSellingPrice(100.0); p.setStock(10);
        Category cat = new Category(); cat.setCategoryId(1L); p.setCategory(cat);

        when(auth.getName()).thenReturn("s@test.com");
        when(userService.findByEmail("s@test.com")).thenReturn(new User());
        when(imageFile.isEmpty()).thenReturn(true);

        assertEquals("redirect:/seller/my-products", sellerController.saveProduct(p, imageFile, null, auth, model, redirectAttributes));
    }

    @Test
    void testViewMyProducts() {
        when(auth.getName()).thenReturn("s@test.com");
        when(userService.findByEmail("s@test.com")).thenReturn(setupUserWithBusinessName("Shop"));
        when(productService.getProductBySeller(any())).thenReturn(new ArrayList<>());

        assertEquals("seller/my-products", sellerController.viewProducts(model, auth));
    }

    @Test
    void testShowEditProduct() {
        when(productService.getProductById(1L)).thenReturn(new Product());
        assertEquals("seller/edit-product", sellerController.showEditProduct(1L, redirectAttributes, model));
    }

    @Test
    void testUpdateProductSuccess() {
        when(productService.getProductById(1L)).thenReturn(new Product());
        assertEquals("redirect:/seller/my-products", sellerController.updateProduct(1L, "N", "D", "M", 10.0, 0.0, 10.0, 10, 2, redirectAttributes));
        verify(productService).saveOrUpdateProduct(any());
    }

    @Test
    void testDeleteProduct() {
        assertEquals("redirect:/seller/my-products", sellerController.deleteProduct(1L, redirectAttributes));
        verify(productService).deleteProductById(1L);
    }

    // =================================================================
    // 3. ORDERS & ANALYTICS
    // =================================================================
    @Test
    void testSellerOrders() {
        User seller = setupUserWithBusinessName("Shop");
        when(auth.getName()).thenReturn("s@test.com");
        when(userService.findByEmail("s@test.com")).thenReturn(seller);

        // Setup a placed order to cover the stream logic
        Order order = new Order(); order.setStatus("PLACED"); order.setOrderId(1L); order.setOrderDate(LocalDateTime.now());
        OrderItem item = new OrderItem(); item.setOrder(order);

        when(orderItemRepository.findBySeller(seller)).thenReturn(List.of(item));
        when(paymentRepository.findByOrder_OrderIdIn(anyList())).thenReturn(new ArrayList<>());
        when(orderAddressRepository.findByOrder(any())).thenReturn(new OrderAddress());

        assertEquals("seller/orders", sellerController.sellerOrders("PLACED", auth, model));
    }

    @Test
    void testSellerAnalyticsWithDeliveredOrder() {
        User seller = setupUserWithBusinessName("Shop"); seller.setUserId(1L);
        when(auth.getName()).thenReturn("s@test.com");
        when(userService.findByEmail("s@test.com")).thenReturn(seller);

        // Setup a delivered order to cover revenue math logic
        Order order = new Order(); order.setStatus("DELIVERED"); order.setOrderId(1L); order.setOrderDate(LocalDateTime.now());
        Product p = new Product(); p.setProductName("Laptop");
        OrderItem item = new OrderItem(); item.setSeller(seller); item.setOrder(order); item.setProduct(p); item.setPrice(100.0); item.setQuantity(2);

        when(orderItemRepository.findAll()).thenReturn(List.of(item));

        assertEquals("seller/sales", sellerController.sellerAnalytics(auth, model));
        verify(model).addAttribute("totalRevenue", 200.0);
    }

    @Test
    void testMarkOrderDelivered() {
        assertEquals("redirect:/seller/orders", sellerController.markOrderDelivered(1L, redirectAttributes));
        verify(orderService).markAsDelivered(1L);
    }

    // =================================================================
    // 4. REVIEWS, LOW STOCK & NOTIFICATIONS
    // =================================================================
    @Test
    void testSellerReviews() {
        when(auth.getName()).thenReturn("s@test.com");
        when(userService.findByEmail("s@test.com")).thenReturn(setupUserWithBusinessName("Shop"));
        when(reviewRepository.findReviewsForSellerProducts(any())).thenReturn(new ArrayList<>());

        assertEquals("seller/reviews", sellerController.sellerReviews(auth, model));
    }

    @Test
    void testViewAllReviewsForProductEmpty() {
        when(reviewRepository.findByProduct_ProductIdOrderByReviewDateDesc(1L)).thenReturn(new ArrayList<>());
        assertEquals("seller/product-reviews", sellerController.viewAllReviewsForProduct(1L, model));
        verify(model).addAttribute("avgRating", 0.0);
    }

    @Test
    void testViewLowStock() {
        when(auth.getName()).thenReturn("s@test.com");
        when(userService.findByEmail("s@test.com")).thenReturn(setupUserWithBusinessName("Shop"));
        when(lowStockService.getLowStockProducts("s@test.com")).thenReturn(new ArrayList<>());

        assertEquals("seller/low-stock", sellerController.viewLowStock(auth, model));
    }

    @Test
    void testNotificationsLogic() {
        when(auth.getName()).thenReturn("s@test.com");
        when(userService.findByEmail("s@test.com")).thenReturn(setupUserWithBusinessName("Shop"));

        assertEquals("seller/notifications", sellerController.viewNotifications(auth, model));
        assertEquals(0L, sellerController.getUnreadCount(auth));
        assertEquals("redirect:/seller/notifications", sellerController.markAsRead(1L));
        assertEquals("redirect:/seller/notifications", sellerController.deleteNotification(1L, auth));
    }

    // Helper method
    private User setupUserWithBusinessName(String name) {
        User user = new User();
        SellerDetails details = new SellerDetails();
        details.setBusinessName(name);
        user.setSellerDetails(details);
        return user;
    }
}