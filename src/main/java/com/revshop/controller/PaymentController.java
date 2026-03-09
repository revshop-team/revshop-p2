package com.revshop.controller;
import com.revshop.entity.*;
import com.revshop.repo.*;
import com.revshop.serviceInterfaces.*;
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

    @GetMapping("/{orderId}")
    public String paymentPage(@PathVariable Long orderId, Model model) {
        logger.info("Payment page requested for orderId: {}", orderId);

        Order order = orderRepository.findById(orderId).orElseThrow();

        model.addAttribute("order", order);
//        model.addAttribute("amount", order.getTotalAmount());
        logger.debug("Order details loaded for payment page. OrderId: {}", orderId);

        return "buyer/payment";
    }

    @GetMapping("/process")
    public String processPayment(@RequestParam Long orderId,
                                 @RequestParam String status) {
        logger.info("Processing payment for orderId: {} with status: {}", orderId, status);

        Payment payment =
                paymentRepository.findByOrder_OrderId(orderId);

        Order order = payment.getOrder();

        if ("success".equals(status)) {
            logger.info("Payment successful for orderId: {}", orderId);

            payment.setPaymentStatus("SUCCESS");
            payment.setPaidAt(LocalDateTime.now());

            order.setStatus("PLACED");

        } else {
            logger.warn("Payment failed for orderId: {}", orderId);

            payment.setPaymentStatus("FAILED");
            order.setStatus("PAYMENT_FAILED");
        }

        paymentRepository.save(payment);
        orderRepository.save(order);
        logger.debug("Payment and order status updated for orderId: {}", orderId);

        return "redirect:/buyer/payment/success/" + orderId;
    }
    @GetMapping("/success/{orderId}")
    public String paymentSuccess(@PathVariable Long orderId, Model model){
        logger.info("Payment success page opened for orderId: {}", orderId);

        Order order = orderRepository.findById(orderId).orElseThrow();

        model.addAttribute("order", order);

        return "buyer/payment-success";
    }
}
