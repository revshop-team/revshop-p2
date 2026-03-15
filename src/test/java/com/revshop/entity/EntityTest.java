package com.revshop.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class EntityTest {

    // ==========================================
    // 1. USER ENTITY
    // ==========================================
    @Test
    void testUserEntity() {
        User user = new User();
        user.setUserId(1L);
        user.setEmail("test@mail.com");
        user.setPassword("password123");
        user.setRole("BUYER");
        user.setSecurityAnswer("Fluffy");

        assertEquals(1L, user.getUserId());
        assertEquals("test@mail.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("BUYER", user.getRole());
        assertEquals("Fluffy", user.getSecurityAnswer());
        assertNotNull(user.toString()); // Covers Lombok @ToString
    }

    @Test
    void testUserBuilder() {
        User user = User.builder()
                .userId(2L)
                .email("b@b.com")
                .role("SELLER")
                .build();
        assertEquals(2L, user.getUserId());
        assertEquals("SELLER", user.getRole());
        assertNotNull(user.toString());
    }

    // ==========================================
    // 2. BUYER DETAILS ENTITY
    // ==========================================
    @Test
    void testBuyerDetailsEntity() {
        BuyerDetails buyer = new BuyerDetails();
        buyer.setBuyerId(1L);
        buyer.setFullName("Prashanth");
        buyer.setGender("Male");
        buyer.setDateOfBirth(LocalDate.of(2000, 1, 1));
        buyer.setPhone("9876543210");

        assertEquals("Prashanth", buyer.getFullName());
        assertEquals("Male", buyer.getGender());
        assertNotNull(buyer.toString());
    }

    @Test
    void testBuyerDetailsBuilder() {
        BuyerDetails buyer = BuyerDetails.builder()
                .buyerId(2L)
                .fullName("John")
                .build();
        assertEquals(2L, buyer.getBuyerId());
        assertEquals("John", buyer.getFullName());
    }

    // ==========================================
    // 3. SELLER DETAILS ENTITY
    // ==========================================
    @Test
    void testSellerDetailsEntity() {
        SellerDetails seller = new SellerDetails();
        seller.setSellerId(2L);
        seller.setBusinessName("Tech Shop");
        seller.setGstNumber("29ABCDE1234F1Z5");
        seller.setAddress("Guntakal");
        seller.setPhone("9876543210");

        assertEquals("Tech Shop", seller.getBusinessName());
        assertEquals("29ABCDE1234F1Z5", seller.getGstNumber());
        assertNotNull(seller.toString());
    }

    @Test
    void testSellerDetailsBuilder() {
        SellerDetails seller = SellerDetails.builder()
                .sellerId(3L)
                .businessName("My Shop")
                .build();
        assertEquals("My Shop", seller.getBusinessName());
    }

    // ==========================================
    // 4. CATEGORY ENTITY
    // ==========================================
    @Test
    void testCategoryEntity() {
        Category category = new Category();
        category.setCategoryId(10L);
        category.setCategoryName("Electronics");
        category.setDescription("Electronic items");
        category.setProducts(new ArrayList<>());

        assertEquals("Electronics", category.getCategoryName());
        assertNotNull(category.toString());
    }

    @Test
    void testCategoryBuilder() {
        Category cat = Category.builder()
                .categoryId(1L)
                .categoryName("Books")
                .build();
        assertEquals("Books", cat.getCategoryName());
    }

    // ==========================================
    // 5. PRODUCT ENTITY
    // ==========================================
    @Test
    void testProductEntity() {
        Product product = new Product();
        LocalDateTime now = LocalDateTime.now();
        product.setProductId(100L);
        product.setProductName("Laptop");
        product.setMrp(80000.0);
        product.setSellingPrice(72000.0);
        product.setStock(10);
        product.setIsActive(1);
        product.setCreatedAt(now);

        assertEquals("Laptop", product.getProductName());
        assertEquals(80000.0, product.getMrp());
        assertEquals(1, product.getIsActive());
        assertNotNull(product.toString());
    }

    @Test
    void testProductBuilder() {
        Product p = Product.builder()
                .productId(5L)
                .productName("Phone")
                .sellingPrice(500.0)
                .build();
        assertEquals("Phone", p.getProductName());
        assertEquals(500.0, p.getSellingPrice());
    }

    // ==========================================
    // 6. CART & CART ITEM ENTITIES
    // ==========================================
    @Test
    void testCartEntity() {
        Cart cart = new Cart();
        cart.setCartId(1L);
        cart.setCreatedAt(LocalDateTime.now());
        assertEquals(1L, cart.getCartId());
        assertNotNull(cart.toString());
    }

    @Test
    void testCartBuilder() {
        Cart cart = Cart.builder().cartId(2L).build();
        assertEquals(2L, cart.getCartId());
    }

    @Test
    void testCartItemEntity() {
        CartItem item = new CartItem();
        item.setCartItemId(1L);
        item.setQuantity(2);
        assertEquals(2, item.getQuantity());
        assertNotNull(item.toString());
    }

    @Test
    void testCartItemBuilder() {
        CartItem item = CartItem.builder().cartItemId(5L).quantity(10).build();
        assertEquals(10, item.getQuantity());
    }

    // ==========================================
    // 7. ORDER, ORDER ITEM & ORDER ADDRESS
    // ==========================================
    @Test
    void testOrderEntity() {
        Order order = new Order();
        order.setOrderId(1L);
        order.setStatus("PLACED");
        order.setTotalAmount(5000.0);
        assertEquals("PLACED", order.getStatus());
        assertNotNull(order.toString());
    }

    @Test
    void testOrderBuilder() {
        Order order = Order.builder().orderId(3L).status("DELIVERED").build();
        assertEquals("DELIVERED", order.getStatus());
    }

    @Test
    void testOrderItemEntity() {
        OrderItem item = new OrderItem();
        item.setOrderItemId(1L);
        item.setPrice(2000.0);
        item.setQuantity(1);
        assertEquals(2000.0, item.getPrice());
        assertNotNull(item.toString());
    }

    @Test
    void testOrderItemBuilder() {
        OrderItem item = OrderItem.builder().orderItemId(2L).price(100.0).build();
        assertEquals(100.0, item.getPrice());
    }

    @Test
    void testOrderAddressEntity() {
        OrderAddress address = new OrderAddress();
        address.setAddressId(1L);
        address.setFullName("John");
        address.setCity("Bangalore");
        assertEquals("Bangalore", address.getCity());
        assertNotNull(address.toString());
    }

    @Test
    void testOrderAddressBuilder() {
        OrderAddress address = OrderAddress.builder().addressId(5L).city("Mumbai").build();
        assertEquals("Mumbai", address.getCity());
    }

    // ==========================================
    // 8. PAYMENT ENTITY
    // ==========================================
    @Test
    void testPaymentEntity() {
        Payment payment = new Payment();
        payment.setPaymentId(1L);
        payment.setPaymentStatus("SUCCESS");
        payment.setAmount(1000.0);
        assertEquals("SUCCESS", payment.getPaymentStatus());
        assertNotNull(payment.toString());
    }

    @Test
    void testPaymentBuilder() {
        Payment p = Payment.builder().paymentId(2L).paymentStatus("FAILED").build();
        assertEquals("FAILED", p.getPaymentStatus());
    }

    // ==========================================
    // 9. FAVOURITE & REVIEW ENTITIES
    // ==========================================
    @Test
    void testFavouriteEntity() {
        Favourite favourite = new Favourite();
        favourite.setFavouriteId(1L);
        assertEquals(1L, favourite.getFavouriteId());
        assertNotNull(favourite.toString());
    }

    @Test
    void testFavouriteBuilder() {
        Favourite f = Favourite.builder().favouriteId(3L).build();
        assertEquals(3L, f.getFavouriteId());
    }

    @Test
    void testReviewEntity() {
        Review review = new Review();
        review.setReviewId(1L);
        review.setRating(5);
        review.setReviewComment("Good");
        assertEquals(5, review.getRating());
        assertNotNull(review.toString());
    }

    @Test
    void testReviewBuilder() {
        Review r = Review.builder().reviewId(4L).rating(4).build();
        assertEquals(4, r.getRating());
    }

    // ==========================================
    // 10. PRODUCT VIEW, NOTIFICATION & SECURITY Q
    // ==========================================
    @Test
    void testProductViewEntity() {
        ProductView view = new ProductView();
        view.setViewTime(LocalDateTime.now());
        assertNotNull(view.getViewTime());
        assertNotNull(view.toString());
    }

    @Test
    void testSecurityQuestionEntity() {
        SecurityQuestion question = new SecurityQuestion();
        question.setQuestionId(1L);
        question.setQuestionText("Pet name?");
        assertEquals("Pet name?", question.getQuestionText());
        assertNotNull(question.toString());
    }

    @Test
    void testSecurityQuestionBuilder() {
        SecurityQuestion q = SecurityQuestion.builder().questionId(2L).questionText("City?").build();
        assertEquals("City?", q.getQuestionText());
    }

    @Test
    void testNotificationEntity() {
        Notification notification = new Notification();
        notification.setNotificationId(1L);
        notification.setMessage("Order placed");
        assertEquals("Order placed", notification.getMessage());
        assertNotNull(notification.toString());
    }

    @Test
    void testNotificationBuilder() {
        Notification n = Notification.builder().notificationId(5L).message("Hi").build();
        assertEquals("Hi", n.getMessage());
    }
}