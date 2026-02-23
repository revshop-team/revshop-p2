//package com.revshop.controller;
//
//import com.revshop.entity.User;
//import com.revshop.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//
//@Controller
//public class AuthController {
//
//    @Autowired
//    private UserService userService;
//
//    // Show register page (Thymeleaf later)
//    @GetMapping("/register")
//    public String showRegisterPage() {
//        return "register";
//    }
//
//    // Handle Buyer/Seller Registration
//    @PostMapping("/register")
//    public String registerUser(@ModelAttribute User user) {
//
//        userService.registerUser(user); // 🔥 password encoded inside service
//
//        return "redirect:/login";
//    }
//
//    // Custom login page mapping
//    @GetMapping("/login")
//    public String loginPage() {
//        return "login";
//    }
//}
package com.revshop.controller;

import com.revshop.entity.User;
import com.revshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    // Register Buyer (No UI required)
    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    // Test endpoint
    @GetMapping("/test")
    public String test() {
        return "Auth API Working!";
    }
}