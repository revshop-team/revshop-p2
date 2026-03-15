package com.revshop.controller;

import com.revshop.entity.*;
import com.revshop.exceptions.UserNotFoundException;
import com.revshop.repo.*;
import com.revshop.serviceInterfaces.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BuyerControllerTest {

    @Mock private ProductService productService;
    @Mock private CartService cartService;
    @Mock private OrderService orderService;
    @Mock private CategoryService categoryService;
    @Mock private BuyerService buyerService;
    @Mock private ReviewService reviewService;
    @Mock private ReviewRepository reviewRepository;
    @Mock private FavouriteRepository favouriteRepository;
    @Mock private UserRepository userRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private NotificationService notificationService;
    @Mock private OrderRepository orderRepository;
    @Mock private ProductViewRepo productViewRepo;
    @Mock private SuggestionService suggestionService;

    @Mock private Authentication auth;
    @Mock private Model model;
    @Mock private RedirectAttributes redirectAttributes;

    @InjectMocks
    private BuyerController buyerController;

    // =================================================================
    // 1. HOME & SUGGESTIONS
    // =================================================================
    @Test
    void testBuyerHomeSuccess() {
        User user = new User();
        BuyerDetails details = new BuyerDetails();
        details.setFullName("John Doe");
        user.setBuyerDetails(details);

        when(auth.getName()).thenReturn("buyer@test.com");
        when(userRepository.findByEmail("buyer@test.com")).thenReturn(Optional.of(user));
        when(productService.getActiveProducts(any())).thenReturn(Page.empty());
        when(suggestionService.suggestByOrder(user)).thenReturn(new ArrayList<>());
        when(suggestionService.suggestByView(user)).thenReturn(new ArrayList<>());

        assertEquals("buyer/home", buyerController.buyerHome(auth, model));
        verify(model).addAttribute("buyerName", "John Doe");
    }

    @Test
    void testBuyerHomeNullBuyerDetails() {
        User user = new User(); // No buyer details attached
        when(auth.getName()).thenReturn("buyer@test.com");
        when(userRepository.findByEmail("buyer@test.com")).thenReturn(Optional.of(user));
        when(productService.getActiveProducts(any())).thenReturn(Page.empty());
        when(suggestionService.suggestByOrder(user)).thenReturn(new ArrayList<>());
        when(suggestionService.suggestByView(user)).thenReturn(new ArrayList<>());

        assertEquals("buyer/home", buyerController.buyerHome(auth, model));
        verify(model).addAttribute("buyerName", "Buyer"); // Tests the fallback logic
    }

    // =================================================================
    // 2. PROFILE MANAGEMENT
    // =================================================================
    @Test
    void testViewProfile() {
        when(auth.getName()).thenReturn("buyer@test.com");
        when(buyerService.getBuyerDetailsByEmail("buyer@test.com")).thenReturn(new BuyerDetails());

        Order o = new Order(); o.setTotalAmount(500.0);
        when(orderService.getOrdersByBuyer("buyer@test.com")).thenReturn(List.of(o));

        assertEquals("buyer/profile", buyerController.viewProfile(false, auth, model));
        verify(model).addAttribute("totalOrders", 1);
        verify(model).addAttribute("totalSpending", 500.0);
    }

    @Test
    void testUpdateProfileSuccess() {
        when(auth.getName()).thenReturn("buyer@test.com");
        assertEquals("redirect:/buyer/profile", buyerController.updateProfile(auth, new BuyerDetails(), model));
        verify(buyerService).updateBuyerDetails(eq("buyer@test.com"), any());
    }

    @Test
    void testUpdateProfileException() {
        when(auth.getName()).thenReturn("buyer@test.com");
        doThrow(new RuntimeException("DB Error")).when(buyerService).updateBuyerDetails(anyString(), any());
        when(orderService.getOrdersByBuyer("buyer@test.com")).thenReturn(new ArrayList<>());

        assertEquals("buyer/profile", buyerController.updateProfile(auth, new BuyerDetails(), model));
        verify(model).addAttribute("error", "Phone already exists or invalid data");
    }

    // =================================================================
    // 3. PRODUCTS & VIEWING
    // =================================================================
    @Test
    void testViewProductsDefault() {
        setupProductMocks();
        when(productService.getActiveProducts(any())).thenReturn(Page.empty());
        assertEquals("buyer/products", buyerController.viewProducts(0, 8, "default", null, null, auth, model));
    }

    @Test
    void testViewProductsWithKeyword() {
        setupProductMocks();
        when(productService.searchActiveProducts(eq("Laptop"), any())).thenReturn(Page.empty());
        assertEquals("buyer/products", buyerController.viewProducts(0, 8, "priceAsc", "Laptop", null, auth, model));
    }

    @Test
    void testViewProductsByCategory() {
        setupProductMocks();
        when(productService.getActiveProductsByCategory(eq(1L), any())).thenReturn(Page.empty());
        assertEquals("buyer/products", buyerController.viewProducts(0, 8, "priceDesc", "", 1L, auth, model));
    }

    @Test
    void testViewProductsNewest() {
        setupProductMocks();
        when(productService.getActiveProducts(any())).thenReturn(Page.empty());
        assertEquals("buyer/products", buyerController.viewProducts(0, 8, "newest", null, null, auth, model));
    }

    @Test
    void testViewProductDetails() {
        Product p = new Product();
        when(productService.getProductById(1L)).thenReturn(p);
        when(auth.getName()).thenReturn("b@test.com");
        when(userRepository.findByEmail("b@test.com")).thenReturn(Optional.of(new User()));
        when(reviewRepository.findByProduct_ProductIdOrderByReviewDateDesc(1L)).thenReturn(new ArrayList<>());
        when(reviewRepository.getAverageRatingByProductId(1L)).thenReturn(4.5);
        when(reviewRepository.countByProduct_ProductId(1L)).thenReturn(10L);

        assertEquals("buyer/product-details", buyerController.viewProduct(1L, model, auth));
        verify(productViewRepo).save(any(ProductView.class)); // Verifies the view history works
    }

    // Helper for product views
    private void setupProductMocks() {
        User user = new User();
        when(auth.getName()).thenReturn("buyer@test.com");
        when(userRepository.findByEmail("buyer@test.com")).thenReturn(Optional.of(user));
        when(favouriteRepository.findByBuyer(user)).thenReturn(new ArrayList<>());
        when(categoryService.getAllCategories()).thenReturn(new ArrayList<>());
    }

    // =================================================================
    // 4. CART LOGIC
    // =================================================================
    @Test
    void testAddToCartSuccess() {
        Product p = new Product(); p.setProductName("Laptop");
        when(auth.getName()).thenReturn("b@test.com");
        when(productService.getProductById(1L)).thenReturn(p);

        assertEquals("redirect:/buyer/products", buyerController.addToCart(1L, auth, redirectAttributes));
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), contains("added to cart"));
    }

    @Test
    void testAddToCartException() {
        when(auth.getName()).thenReturn("b@test.com");
        when(productService.getProductById(1L)).thenReturn(new Product());
        doThrow(new RuntimeException("Out of stock")).when(cartService).addToCart(1L, "b@test.com");

        assertEquals("redirect:/buyer/products", buyerController.addToCart(1L, auth, redirectAttributes));
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Out of stock");
    }

    @Test
    void testViewCart() {
        when(auth.getName()).thenReturn("b@test.com");
        when(cartService.getCartByBuyer("b@test.com")).thenReturn(new Cart());
        assertEquals("buyer/cart", buyerController.viewCart(model, auth));
    }

    @Test
    void testCartItemAdjustments() {
        assertEquals("redirect:/buyer/cart", buyerController.increaseQty(1L));
        assertEquals("redirect:/buyer/cart", buyerController.decreaseQty(1L));
        assertEquals("redirect:/buyer/cart", buyerController.removeFromCart(1L));
    }

    // =================================================================
    // 5. CHECKOUT & ORDERS
    // =================================================================
    @Test
    void testCheckoutPage() {
        when(auth.getName()).thenReturn("b@test.com");
        when(cartService.getCartByBuyer("b@test.com")).thenReturn(new Cart());
        assertEquals("buyer/checkout", buyerController.checkoutPage(auth, model));
    }

    @Test
    void testCheckoutEmptyCart() {
        when(auth.getName()).thenReturn("b@test.com");
        Cart emptyCart = new Cart(); emptyCart.setCartItems(new ArrayList<>());
        when(cartService.getCartByBuyer("b@test.com")).thenReturn(emptyCart);

        String view = buyerController.checkout(auth, "N", "P", "A1", "A2", "C", "S", "123", "COD", redirectAttributes);
        assertEquals("redirect:/buyer/products", view);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void testCheckoutCODOk() {
        when(auth.getName()).thenReturn("b@test.com");
        Cart cart = new Cart(); cart.setCartItems(List.of(new CartItem()));
        when(cartService.getCartByBuyer("b@test.com")).thenReturn(cart);

        Order o = new Order(); o.setOrderId(50L);
        when(orderService.checkout("b@test.com", "N", "P", "A1", "A2", "C", "S", "123", "COD")).thenReturn(o);

        String view = buyerController.checkout(auth, "N", "P", "A1", "A2", "C", "S", "123", "COD", redirectAttributes);
        assertEquals("redirect:/buyer/orders-success/50", view);
    }

    @Test
    void testCheckoutOnlinePayment() {
        when(auth.getName()).thenReturn("b@test.com");
        Cart cart = new Cart(); cart.setCartItems(List.of(new CartItem()));
        when(cartService.getCartByBuyer("b@test.com")).thenReturn(cart);

        Order o = new Order(); o.setOrderId(60L);
        when(orderService.checkout("b@test.com", "N", "P", "A1", "A2", "C", "S", "123", "ONLINE")).thenReturn(o);

        String view = buyerController.checkout(auth, "N", "P", "A1", "A2", "C", "S", "123", "ONLINE", redirectAttributes);
        assertEquals("redirect:/buyer/payment/60", view);
    }

    @Test
    void testViewOrdersWithPaymentStatus() {
        when(auth.getName()).thenReturn("b@test.com");
        Order o = new Order(); o.setOrderId(1L);
        when(orderService.getOrdersByBuyer("b@test.com")).thenReturn(List.of(o));

        Payment p = new Payment(); p.setOrder(o); p.setPaymentStatus("SUCCESS");
        when(paymentRepository.findByOrder_OrderIdIn(any())).thenReturn(List.of(p));

        assertEquals("buyer/orders", buyerController.viewOrders(auth, model));
        verify(model).addAttribute(eq("paymentStatusMap"), anyMap());
    }

    @Test
    void testBuyNow() {
        when(auth.getName()).thenReturn("b@test.com");
        assertEquals("redirect:/buyer/cart/checkout", buyerController.buyNow(1L, auth, redirectAttributes));
        verify(cartService).addToCart(1L, "b@test.com");
    }

    // =================================================================
    // 6. FAVOURITES
    // =================================================================
    @Test
    void testViewFavourites() {
        when(auth.getName()).thenReturn("b@test.com");
        when(userRepository.findByEmail("b@test.com")).thenReturn(Optional.of(new User()));
        when(favouriteRepository.findByBuyer(any())).thenReturn(new ArrayList<>());
        assertEquals("buyer/favourites", buyerController.viewFavourites(auth, model));
    }

    @Test
    void testToggleFavouriteAdd() {
        User user = new User(); Product p = new Product();
        when(auth.getName()).thenReturn("b@test.com");
        when(userRepository.findByEmail("b@test.com")).thenReturn(Optional.of(user));
        when(productService.getProductById(1L)).thenReturn(p);
        when(favouriteRepository.findByBuyerAndProduct(user, p)).thenReturn(Optional.empty()); // Does not exist

        String view = buyerController.toggleFavourite(1L, "favourites", auth, redirectAttributes);
        assertEquals("redirect:/buyer/favourites", view);
        verify(favouriteRepository).save(any());
    }

    @Test
    void testToggleFavouriteRemove() {
        User user = new User(); Product p = new Product();
        when(auth.getName()).thenReturn("b@test.com");
        when(userRepository.findByEmail("b@test.com")).thenReturn(Optional.of(user));
        when(productService.getProductById(1L)).thenReturn(p);
        when(favouriteRepository.findByBuyerAndProduct(user, p)).thenReturn(Optional.of(new Favourite())); // Exists

        String view = buyerController.toggleFavourite(1L, "products", auth, redirectAttributes);
        assertEquals("redirect:/buyer/products", view);
        verify(favouriteRepository).delete(any());
    }

    // =================================================================
    // 7. REVIEWS & NOTIFICATIONS
    // =================================================================
    @Test
    void testSubmitReviewSuccess() {
        when(auth.getName()).thenReturn("b@test.com");
        assertEquals("redirect:/buyer/orders", buyerController.submitReview(auth, 1L, 1L, 5, "Good", redirectAttributes));
        verify(reviewService).addReview(1L, 1L, 5, "Good", "b@test.com");
    }

    @Test
    void testSubmitReviewException() {
        when(auth.getName()).thenReturn("b@test.com");
        doThrow(new RuntimeException("Review failed")).when(reviewService).addReview(anyLong(), anyLong(), anyInt(), anyString(), anyString());
        assertEquals("redirect:/buyer/orders", buyerController.submitReview(auth, 1L, 1L, 5, "Good", redirectAttributes));
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Review failed");
    }

    @Test
    void testNotifications() {
        when(auth.getName()).thenReturn("b@test.com");
        when(notificationService.getUserNotifications("b@test.com")).thenReturn(new ArrayList<>());

        assertEquals("buyer/notifications", buyerController.viewBuyerNotifications(auth, model));
        assertEquals(0L, buyerController.getBuyerUnreadCount(auth));
        assertEquals("redirect:/buyer/notifications", buyerController.markBuyerNotificationAsRead(1L, auth));
        assertEquals("redirect:/buyer/notifications", buyerController.deleteNotification(1L, auth));
    }

    @Test
    void testOrderSuccessPage() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(new Order()));
        when(paymentRepository.findByOrder_OrderId(1L)).thenReturn(new Payment());
        assertEquals("buyer/orders-success", buyerController.orderSuccess(1L, model));
    }
}