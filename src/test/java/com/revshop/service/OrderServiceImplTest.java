package com.revshop.service;

import com.revshop.entity.*;
import com.revshop.exceptions.BuyerNotFoundException;
import com.revshop.repo.*;
import com.revshop.serviceImpl.OrderServiceImpl;
import com.revshop.serviceInterfaces.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private CartRepository cartRepository;
    @Mock private ProductRepository productRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private OrderAddressRepository orderAddressRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User testBuyer;
    private Cart testCart;
    private Product testProduct;
    private final String email = "buyer@test.com";

    @BeforeEach
    void setUp() {
        testBuyer = new User();
        testBuyer.setUserId(1L);
        testBuyer.setEmail(email);

        testProduct = new Product();
        testProduct.setProductId(10L);
        testProduct.setProductName("Laptop");
        testProduct.setStock(10);
        testProduct.setStockThreshold(2);
        testProduct.setSellingPrice(1000.0);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(2);

        testCart = new Cart();
        testCart.setBuyer(testBuyer);
        testCart.setCartItems(new ArrayList<>(List.of(cartItem)));
    }

    // --- CHECKOUT TESTS ---

    @Test
    void checkout_Success_COD() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testBuyer));
        when(cartRepository.findByBuyer(testBuyer)).thenReturn(Optional.of(testCart));

        Order order = orderService.checkout(email, "John Doe", "1234567890", "A1", "A2", "City", "State", "500001", "COD");

        assertThat(order.getStatus()).isEqualTo("PLACED");
        verify(paymentRepository).save(argThat(p -> p.getPaymentStatus().equals("PENDING")));
        verify(orderRepository).save(any());
    }

    @Test
    void checkout_Success_OnlinePayment() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testBuyer));
        when(cartRepository.findByBuyer(testBuyer)).thenReturn(Optional.of(testCart));

        Order order = orderService.checkout(email, "John Doe", "1234567890", "A1", "A2", "City", "State", "500001", "CARD");

        assertThat(order.getStatus()).isEqualTo("PENDING_PAYMENT");
        verify(paymentRepository).save(argThat(p -> p.getPaymentStatus().equals("PENDING")));
    }

    @Test
    void checkout_Throws_InsufficientStock() {
        testCart.getCartItems().get(0).setQuantity(100); // Exceeds stock of 10
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testBuyer));
        when(cartRepository.findByBuyer(testBuyer)).thenReturn(Optional.of(testCart));

        assertThatThrownBy(() -> orderService.checkout(email, "Name", "Phone", "A1", "A2", "City", "State", "000", "COD"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    void checkout_TriggersLowStockNotification() {
        testCart.getCartItems().get(0).setQuantity(9); // Leaves 1, which is below threshold 2
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testBuyer));
        when(cartRepository.findByBuyer(testBuyer)).thenReturn(Optional.of(testCart));

        orderService.checkout(email, "Name", "Phone", "A1", "A2", "City", "State", "000", "COD");

        verify(notificationRepository, atLeastOnce()).save(any(Notification.class));
    }

    @Test
    void checkout_Throws_CartEmpty() {
        testCart.setCartItems(List.of());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testBuyer));
        when(cartRepository.findByBuyer(testBuyer)).thenReturn(Optional.of(testCart));

        assertThatThrownBy(() -> orderService.checkout(email, "Name", "Phone", "A1", "A2", "City", "State", "000", "COD"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cart is empty");
    }

    @Test
    void checkout_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.checkout(email, "Name", "Phone", "A1", "A2", "City", "State", "000", "COD"))
                .isInstanceOf(RuntimeException.class);
    }

    // --- GET ORDERS TESTS ---

    @Test
    void getOrdersByBuyer_Success() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testBuyer));
        when(orderRepository.findByBuyer(testBuyer)).thenReturn(new ArrayList<>());

        List<Order> results = orderService.getOrdersByBuyer(email);
        assertThat(results).isNotNull();
    }

    @Test
    void getOrdersByBuyer_Throws_BuyerNotFound() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.getOrdersByBuyer(email))
                .isInstanceOf(BuyerNotFoundException.class);
    }

    // --- MARK AS DELIVERED TESTS ---

    @Test
    void markAsDelivered_Success_UpdatesPayment() {
        Order order = new Order();
        order.setStatus("PLACED");
        order.setBuyer(testBuyer);
        Payment payment = new Payment();
        payment.setPaymentStatus("PENDING");

        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrder_OrderId(100L)).thenReturn(payment);

        orderService.markAsDelivered(100L);

        assertThat(order.getStatus()).isEqualTo("DELIVERED");
        assertThat(payment.getPaymentStatus()).isEqualTo("SUCCESS");
        verify(notificationRepository).save(any());
    }

    @Test
    void markAsDelivered_OrderNotFound_ThrowsException() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.markAsDelivered(100L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void markAsDelivered_Ignores_IfAlreadyDelivered() {
        Order order = new Order();
        order.setStatus("DELIVERED");
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        orderService.markAsDelivered(100L);

        verify(orderRepository, never()).save(order);
    }

    // --- OTHER TESTS ---

    @Test
    void checkout_ClearsCart_AfterSuccess() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testBuyer));
        when(cartRepository.findByBuyer(testBuyer)).thenReturn(Optional.of(testCart));

        orderService.checkout(email, "John Doe", "1234567890", "A1", "A2", "City", "State", "500001", "COD");

        assertThat(testCart.getCartItems()).isEmpty();
        verify(cartRepository).save(testCart);
    }
}