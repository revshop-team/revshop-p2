package com.revshop.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EntityCoverageTest {

    @Mock
    User user;

    @Mock
    Product product;

    @Mock
    Order order;

    @Test
    void testEntityBuildersAndGetters() {

        SecurityQuestion question = SecurityQuestion.builder()
                .questionId(1L)
                .questionText("Pet name?")
                .build();
        assertEquals("Pet name?", question.getQuestionText());

        User u = User.builder()
                .userId(1L)
                .email("test@mail.com")
                .password("1234")
                .role("BUYER")
                .securityQuestion(question)
                .securityAnswer("Tom")
                .build();

        assertEquals("BUYER", u.getRole());

        BuyerDetails buyerDetails = BuyerDetails.builder()
                .buyerId(1L)
                .user(u)
                .fullName("John Doe")
                .gender("Male")
                .dateOfBirth(LocalDate.of(2000,1,1))
                .phone("9999999999")
                .build();

        assertEquals("John Doe", buyerDetails.getFullName());

        SellerDetails sellerDetails = SellerDetails.builder()
                .sellerId(2L)
                .user(u)
                .businessName("Tech Store")
                .gstNumber("GST123")
                .address("Hyderabad")
                .phone("8888888888")
                .build();

        assertEquals("Tech Store", sellerDetails.getBusinessName());

        Category category = Category.builder()
                .categoryId(1L)
                .categoryName("Electronics")
                .description("Devices")
                .products(new ArrayList<>())
                .build();

        assertEquals("Electronics", category.getCategoryName());

        Cart cart = Cart.builder()
                .cartId(1L)
                .buyer(u)
                .cartItems(new ArrayList<>())
                .build();

        assertNotNull(cart.getBuyer());

        CartItem cartItem = CartItem.builder()
                .cartItemId(1L)
                .cart(cart)
                .product(product)
                .seller(u)
                .quantity(2)
                .build();

        assertEquals(2, cartItem.getQuantity());

        Order o = Order.builder()
                .orderId(1L)
                .buyer(u)
                .totalAmount(1000.0)
                .status("PLACED")
                .orderItems(new ArrayList<>())
                .build();

        assertEquals("PLACED", o.getStatus());

        OrderItem orderItem = OrderItem.builder()
                .orderItemId(1L)
                .order(o)
                .product(product)
                .seller(u)
                .quantity(1)
                .price(500.0)
                .build();

        assertEquals(500.0, orderItem.getPrice());

        OrderAddress address = OrderAddress.builder()
                .addressId(1L)
                .order(o)
                .addressType("SHIPPING")
                .fullName("John Doe")
                .phone("9999999999")
                .addressLine1("Street 1")
                .city("Hyderabad")
                .state("Telangana")
                .pincode("500001")
                .build();

        assertEquals("Hyderabad", address.getCity());

        Payment payment = Payment.builder()
                .paymentId(1L)
                .order(o)
                .paymentMethod("UPI")
                .paymentStatus("SUCCESS")
                .amount(1000.0)
                .paidAt(LocalDateTime.now())
                .build();

        assertEquals("SUCCESS", payment.getPaymentStatus());

        Favourite favourite = Favourite.builder()
                .favouriteId(1L)
                .buyer(u)
                .product(product)
                .build();

        assertNotNull(favourite.getProduct());

        Notification notification = Notification.builder()
                .notificationId(1L)
                .user(u)
                .message("Order placed")
                .order(o)
                .isRead("N")
                .createdAt(LocalDateTime.now())
                .build();

        assertEquals("Order placed", notification.getMessage());
    }
}