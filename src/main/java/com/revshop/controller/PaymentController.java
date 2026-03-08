package com.revshop.controller;
import com.revshop.entity.*;
import com.revshop.repo.*;
import com.revshop.serviceInterfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/buyer/payment")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/{orderId}")
    public String paymentPage(@PathVariable Long orderId, Model model) {

        Order order = orderRepository.findById(orderId).orElseThrow();

        model.addAttribute("order", order);
//        model.addAttribute("amount", order.getTotalAmount());

        return "buyer/payment";
    }

    @GetMapping("/process")
    public String processPayment(@RequestParam Long orderId,
                                 @RequestParam String status) {

        Payment payment =
                paymentRepository.findByOrder_OrderId(orderId);

        Order order = payment.getOrder();

        if ("success".equals(status)) {

            payment.setPaymentStatus("SUCCESS");
            payment.setPaidAt(LocalDateTime.now());

            order.setStatus("PLACED");

        } else {

            payment.setPaymentStatus("FAILED");
            order.setStatus("PAYMENT_FAILED");
        }

        paymentRepository.save(payment);
        orderRepository.save(order);

        return "redirect:/buyer/payment/success/" + orderId;
    }
    @GetMapping("/success/{orderId}")
    public String paymentSuccess(@PathVariable Long orderId, Model model){

        Order order = orderRepository.findById(orderId).orElseThrow();

        model.addAttribute("order", order);

        return "buyer/payment-success";
    }
}
