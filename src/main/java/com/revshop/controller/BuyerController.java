package com.revshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/buyer")
public class BuyerController {

    @GetMapping("/home")
    public String buyerHome() {
        return "buyer/home";
    }

    @GetMapping("/orders")
    public String buyerOrders() {
        return "buyer/orders";
    }

    @GetMapping("/profile")
    public String buyerProfile() {
        return "buyer/profile";
    }

}
