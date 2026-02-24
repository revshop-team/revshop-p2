package com.revshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/seller")
public class SellerController {

    @GetMapping("/dashboard")
    public String sellerDashboard() {
        return "seller/dashboard";
    }

    @GetMapping("/products")
    public String sellerProducts() {
        return "seller/products";
    }

    @GetMapping("/sales")
    public String sellerSales() {
        return "seller/sales";
    }
}
