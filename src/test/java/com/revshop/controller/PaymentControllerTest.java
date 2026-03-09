package com.revshop.controller;

import com.revshop.entity.Order;
import com.revshop.entity.Payment;
import com.revshop.repo.OrderRepository;
import com.revshop.repo.PaymentRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentRepository paymentRepository;

    @MockBean
    private OrderRepository orderRepository;

    // PAYMENT PAGE
    @Test
    void testPaymentPage() throws Exception {

        Order order = new Order();

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        mockMvc.perform(get("/buyer/payment/1")
                        .with(SecurityMockMvcRequestPostProcessors.user("buyer@test.com")))
                .andExpect(status().isOk())
                .andExpect(view().name("buyer/payment"));
    }

    // PROCESS PAYMENT SUCCESS
    @Test
    void testProcessPaymentSuccess() throws Exception {

        Order order = new Order();
        Payment payment = new Payment();
        payment.setOrder(order);

        when(paymentRepository.findByOrder_OrderId(1L)).thenReturn(payment);

        mockMvc.perform(get("/buyer/payment/process")
                        .param("orderId","1")
                        .param("status","success")
                        .with(SecurityMockMvcRequestPostProcessors.user("buyer@test.com")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/buyer/payment/success/1"));
    }

    // PROCESS PAYMENT FAILED
    @Test
    void testProcessPaymentFailed() throws Exception {

        Order order = new Order();
        Payment payment = new Payment();
        payment.setOrder(order);

        when(paymentRepository.findByOrder_OrderId(2L)).thenReturn(payment);

        mockMvc.perform(get("/buyer/payment/process")
                        .param("orderId","2")
                        .param("status","failed")
                        .with(SecurityMockMvcRequestPostProcessors.user("buyer@test.com")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/buyer/payment/success/2"));
    }

    // PAYMENT SUCCESS PAGE
    @Test
    void testPaymentSuccessPage() throws Exception {

        Order order = new Order();

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        mockMvc.perform(get("/buyer/payment/success/1")
                        .with(SecurityMockMvcRequestPostProcessors.user("buyer@test.com")))
                .andExpect(status().isOk())
                .andExpect(view().name("buyer/payment-success"));
    }

    // PAYMENT SUCCESS PAGE WITH DIFFERENT ORDER
    @Test
    void testPaymentSuccessPageAnotherOrder() throws Exception {

        Order order = new Order();

        when(orderRepository.findById(5L))
                .thenReturn(Optional.of(order));

        mockMvc.perform(get("/buyer/payment/success/5")
                        .with(SecurityMockMvcRequestPostProcessors.user("buyer@test.com")))
                .andExpect(status().isOk())
                .andExpect(view().name("buyer/payment-success"));
    }
}