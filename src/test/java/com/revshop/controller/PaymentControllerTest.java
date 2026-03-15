package com.revshop.controller;

import com.revshop.entity.Order;
import com.revshop.entity.Payment;
import com.revshop.repo.OrderRepository;
import com.revshop.repo.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private Model model;
    @InjectMocks private PaymentController controller;

    @Test
    public void testPaymentPage() {
        Order order = new Order();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        assertEquals("buyer/payment", controller.paymentPage(1L, model));
    }

    @Test
    public void testOtpPage() {
        assertEquals("buyer/payment-otp", controller.otpPage(1L, model));
    }

    @Test
    public void testProcessPaymentSuccess() {
        Order order = new Order();
        Payment payment = new Payment();
        payment.setOrder(order);
        when(paymentRepository.findByOrder_OrderId(1L)).thenReturn(payment);

        assertEquals("redirect:/buyer/payment/success/1", controller.processPayment(1L, "success"));
        assertEquals("SUCCESS", payment.getPaymentStatus());
    }

    @Test
    public void testProcessPaymentFail() {
        Order order = new Order();
        Payment payment = new Payment();
        payment.setOrder(order);
        when(paymentRepository.findByOrder_OrderId(1L)).thenReturn(payment);

        assertEquals("redirect:/buyer/payment/cancel/1", controller.processPayment(1L, "fail"));
        assertEquals("FAILED", payment.getPaymentStatus());
    }

    @Test
    public void testPaymentSuccess() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(new Order()));
        assertEquals("buyer/payment-success", controller.paymentSuccess(1L, model));
    }

    @Test
    public void testPaymentCancel() {
        Order order = new Order();
        Payment payment = new Payment();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrder_OrderId(1L)).thenReturn(payment);

        assertEquals("buyer/payment-cancel", controller.paymentCancel(1L, model));
        assertEquals("FAILED", payment.getPaymentStatus());
        assertEquals("PENDING_PAYMENT", order.getStatus());
    }
}