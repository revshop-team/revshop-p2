package com.revshop.controller;

import com.revshop.entity.*;
import com.revshop.repo.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/buyer/payment")
public class PaymentController {

    private static final Logger logger =
            LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    /* ================= PAYMENT PAGE ================= */

    @GetMapping("/{orderId}")
    public String paymentPage(@PathVariable Long orderId, Model model) {

        logger.info("Payment page requested for orderId: {}", orderId);

        Order order = orderRepository.findById(orderId).orElseThrow();

        model.addAttribute("order", order);

        return "buyer/payment";
    }

    /* ================= OTP PAGE ================= */

    @GetMapping("/otp")
    public String otpPage(@RequestParam Long orderId, Model model){

        logger.info("OTP verification page opened for orderId: {}", orderId);

        model.addAttribute("orderId", orderId);

        return "buyer/payment-otp";
    }

    /* ================= PROCESS PAYMENT ================= */

    @GetMapping("/process")
    public String processPayment(@RequestParam Long orderId,
                                 @RequestParam String status) {

        logger.info("Processing payment for orderId: {} with status: {}", orderId, status);

        Payment payment = paymentRepository.findByOrder_OrderId(orderId);

        Order order = payment.getOrder();

        if ("success".equals(status)) {

            logger.info("Payment successful for orderId: {}", orderId);

            payment.setPaymentStatus("SUCCESS");
            payment.setPaidAt(LocalDateTime.now());

            order.setStatus("PLACED");

            paymentRepository.save(payment);
            orderRepository.save(order);

            return "redirect:/buyer/payment/success/" + orderId;

        } else {

            logger.warn("Payment failed for orderId: {}", orderId);

            payment.setPaymentStatus("FAILED");

            order.setStatus("PAYMENT_FAILED");

            paymentRepository.save(payment);
            orderRepository.save(order);

            return "redirect:/buyer/payment/cancel/" + orderId;
        }
    }

    /* ================= SUCCESS PAGE ================= */

    @GetMapping("/success/{orderId}")
    public String paymentSuccess(@PathVariable Long orderId, Model model){

        logger.info("Payment success page opened for orderId: {}", orderId);

        Order order = orderRepository.findById(orderId).orElseThrow();

        model.addAttribute("order", order);

        return "buyer/payment-success";
    }
    @GetMapping("/cancel/{orderId}")
    public String paymentCancel(@PathVariable Long orderId, Model model){

        Order order = orderRepository.findById(orderId).orElseThrow();

        Payment payment = paymentRepository.findByOrder_OrderId(orderId);

        if(payment != null){
            payment.setPaymentStatus("FAILED");
            paymentRepository.save(payment);
        }

        order.setStatus("PENDING_PAYMENT");
        orderRepository.save(order);

        model.addAttribute("order", order);

        return "buyer/payment-cancel";
    }
}