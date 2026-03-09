package com.revshop.controller;

import com.revshop.entity.*;
import com.revshop.repo.*;
import com.revshop.serviceInterfaces.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BuyerController.class)
@AutoConfigureMockMvc
class BuyerControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private ProductService productService;
    @MockBean private CartService cartService;
    @MockBean private OrderService orderService;
    @MockBean private CategoryService categoryService;
    @MockBean private BuyerService buyerService;
    @MockBean private ReviewService reviewService;
    @MockBean private ReviewRepository reviewRepository;
    @MockBean private FavouriteRepository favouriteRepository;
    @MockBean private UserRepository userRepository;
    @MockBean private PaymentRepository paymentRepository;
    @MockBean private NotificationService notificationService;
    @MockBean private OrderRepository orderRepository;

    private final String email = "buyer@test.com";

    @BeforeEach
    void globalSetup() {
        // Prevent SpEL errors in products.html by returning a valid Page object
        lenient().when(productService.getActiveProducts(any())).thenReturn(new PageImpl<>(new ArrayList<>()));
        lenient().when(productService.searchActiveProducts(any(), any())).thenReturn(new PageImpl<>(new ArrayList<>()));
        lenient().when(productService.getActiveProductsByCategory(any(), any())).thenReturn(new PageImpl<>(new ArrayList<>()));

        // Prevent NullPointer in Controller/Thymeleaf logic
        lenient().when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
        lenient().when(favouriteRepository.findByBuyer(any())).thenReturn(new ArrayList<>());
        lenient().when(categoryService.getAllCategories()).thenReturn(new ArrayList<>());
    }

    // --- HOME & PROFILE (6 CASES) ---
    @Test void buyerHome_ReturnsOk() throws Exception { mockMvc.perform(get("/buyer/home").with(user(email))).andExpect(status().isOk()); }
    @Test void buyerHome_View() throws Exception { mockMvc.perform(get("/buyer/home").with(user(email))).andExpect(view().name("buyer/home")); }
    @Test void viewProfile_Status() throws Exception {
        when(buyerService.getBuyerDetailsByEmail(any())).thenReturn(new BuyerDetails());
        mockMvc.perform(get("/buyer/profile").with(user(email))).andExpect(status().isOk());
    }
    @Test void profile_HasSpending() throws Exception {
        when(buyerService.getBuyerDetailsByEmail(any())).thenReturn(new BuyerDetails());
        mockMvc.perform(get("/buyer/profile").with(user(email))).andExpect(model().attributeExists("totalSpending"));
    }
    @Test void updateProfile_Redirects() throws Exception {
        mockMvc.perform(post("/buyer/profile/update").with(user(email)).with(csrf())).andExpect(status().is3xxRedirection());
    }
    @Test void updateProfile_ErrorBranch() throws Exception {
        doThrow(new RuntimeException("Fail")).when(buyerService).updateBuyerDetails(any(), any());
        mockMvc.perform(post("/buyer/profile/update").with(user(email)).with(csrf())).andExpect(view().name("buyer/profile")).andExpect(model().attributeExists("error"));
    }

    // --- PRODUCTS & BROWSE (6 CASES) ---
    @Test void products_SearchPath() throws Exception {
        mockMvc.perform(get("/buyer/products").param("keyword", "phone").with(user(email))).andExpect(status().isOk());
    }
    @Test void products_CategoryPath() throws Exception {
        mockMvc.perform(get("/buyer/products").param("categoryId", "1").with(user(email))).andExpect(status().isOk());
    }
    @Test void products_SortAsc() throws Exception { mockMvc.perform(get("/buyer/products").param("sort", "priceAsc").with(user(email))).andExpect(status().isOk()); }
    @Test void products_SortDesc() throws Exception { mockMvc.perform(get("/buyer/products").param("sort", "priceDesc").with(user(email))).andExpect(status().isOk()); }
    @Test void products_SortNewest() throws Exception { mockMvc.perform(get("/buyer/products").param("sort", "newest").with(user(email))).andExpect(status().isOk()); }
    @Test void productDetail_HasProduct() throws Exception {
        when(productService.getProductById(anyLong())).thenReturn(new Product());
        mockMvc.perform(get("/buyer/product/1").with(user(email))).andExpect(model().attributeExists("product"));
    }

    // --- CART ACTIONS (6 CASES) ---
    @Test void addToCart_Success() throws Exception {
        Product p = new Product(); p.setProductName("Laptop");
        when(productService.getProductById(any())).thenReturn(p);
        mockMvc.perform(get("/buyer/cart/add/1").with(user(email))).andExpect(status().is3xxRedirection());
    }
    @Test void addToCart_Catch() throws Exception {
        doThrow(new RuntimeException("Stock Error")).when(cartService).addToCart(anyLong(), anyString());
        mockMvc.perform(get("/buyer/cart/add/1").with(user(email))).andExpect(flash().attribute("errorMessage", "Stock Error"));
    }
    @Test void viewCart_Ok() throws Exception {
        Cart cart = new Cart(); cart.setCartItems(new ArrayList<>()); // Fix List.isEmpty() NPE
        when(cartService.getCartByBuyer(any())).thenReturn(cart);
        mockMvc.perform(get("/buyer/cart").with(user(email))).andExpect(status().isOk());
    }
    @Test void cart_Inc() throws Exception { mockMvc.perform(get("/buyer/cart/increase/1").with(user(email))).andExpect(status().is3xxRedirection()); }
    @Test void cart_Dec() throws Exception { mockMvc.perform(get("/buyer/cart/decrease/1").with(user(email))).andExpect(status().is3xxRedirection()); }
    @Test void cart_Rem() throws Exception { mockMvc.perform(post("/buyer/cart/remove/1").with(user(email)).with(csrf())).andExpect(status().is3xxRedirection()); }

    // --- CHECKOUT & SUCCESS (6 CASES) ---
    @Test void checkout_Page_Ok() throws Exception {
        Cart cart = new Cart(); cart.setCartItems(new ArrayList<>());
        when(cartService.getCartByBuyer(any())).thenReturn(cart);
        mockMvc.perform(get("/buyer/cart/checkout").with(user(email))).andExpect(status().isOk());
    }
    @Test void checkout_EmptyError() throws Exception {
        Cart cart = new Cart(); cart.setCartItems(new ArrayList<>());
        when(cartService.getCartByBuyer(any())).thenReturn(cart);
        mockMvc.perform(post("/buyer/cart/checkout").with(user(email)).with(csrf()).param("fullName", "A").param("phone", "1").param("addressLine1", "A").param("addressLine2", "A").param("city", "A").param("state", "A").param("pincode", "1").param("paymentMethod", "COD"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
    @Test void checkout_Success_COD() throws Exception {
        Cart cart = new Cart(); cart.setCartItems(List.of(new CartItem()));
        when(cartService.getCartByBuyer(any())).thenReturn(cart);
        Order o = new Order(); o.setOrderId(10L);
        when(orderService.checkout(any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(o);
        mockMvc.perform(post("/buyer/cart/checkout").with(user(email)).with(csrf()).param("fullName", "A").param("phone", "1").param("addressLine1", "A").param("addressLine2", "A").param("city", "A").param("state", "A").param("pincode", "1").param("paymentMethod", "COD"))
                .andExpect(redirectedUrl("/buyer/orders-success/10"));
    }
    @Test void orderSuccess_Ok() throws Exception {
        when(orderRepository.findById(any())).thenReturn(Optional.of(new Order()));
        Payment p = new Payment(); p.setPaymentMethod("COD"); // Fix payment.paymentMethod NPE
        when(paymentRepository.findByOrder_OrderId(any())).thenReturn(p);
        mockMvc.perform(get("/buyer/orders-success/1").with(user(email))).andExpect(status().isOk());
    }
    @Test void buyNow_Ok() throws Exception { mockMvc.perform(get("/buyer/buy-now/1").with(user(email))).andExpect(redirectedUrl("/buyer/cart/checkout")); }
    @Test void viewOrders_Ok() throws Exception { mockMvc.perform(get("/buyer/orders").with(user(email))).andExpect(status().isOk()); }

    // --- REVIEWS & FAVS (6 CASES) ---
    @Test void review_Success() throws Exception {
        mockMvc.perform(post("/buyer/review").with(user(email)).with(csrf()).param("orderId", "1").param("productId", "1").param("rating", "5").param("comment", "A")).andExpect(status().is3xxRedirection());
    }
    @Test void review_Fail() throws Exception {
        doThrow(new RuntimeException("Msg")).when(reviewService).addReview(any(), any(), any(), any(), any());
        mockMvc.perform(post("/buyer/review").with(user(email)).with(csrf()).param("orderId", "1").param("productId", "1").param("rating", "5").param("comment", "A")).andExpect(flash().attribute("errorMessage", "Msg"));
    }
    @Test void fav_Toggle() throws Exception {
        when(productService.getProductById(any())).thenReturn(new Product());
        mockMvc.perform(post("/buyer/favourite/toggle/1").with(user(email)).with(csrf())).andExpect(status().is3xxRedirection());
    }
    @Test void fav_View() throws Exception { mockMvc.perform(get("/buyer/favourites").with(user(email))).andExpect(view().name("buyer/favourites")); }
    @Test void notes_Count() throws Exception {
        when(notificationService.getUnreadCount(anyString())).thenReturn(5L);
        mockMvc.perform(get("/buyer/notifications/unread-count").with(user(email))).andExpect(content().string("5"));
    }
    @Test void notes_Clear() throws Exception { mockMvc.perform(get("/buyer/notifications/clear-all").with(user(email))).andExpect(status().is3xxRedirection()); }
}