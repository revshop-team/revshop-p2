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
import com.revshop.repo.SecurityQuestionRepository;
import com.revshop.repo.UserRepository;
import com.revshop.serviceImpl.UserServiceImpl;
import com.revshop.serviceInterfaces.CategoryService;
import com.revshop.serviceInterfaces.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class AuthController {

    private final SecurityQuestionRepository securityQuestionRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ProductService productService;
    private final CategoryService categoryService;

    public AuthController(SecurityQuestionRepository securityQuestionRepository,
                          UserRepository userRepository,
                          BCryptPasswordEncoder passwordEncoder,
                          ProductService productService,
                          CategoryService categoryService) {

        this.securityQuestionRepository = securityQuestionRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @Autowired
    private UserServiceImpl userService;

    // Test endpoint
    @GetMapping("/test")
    public String test() {
        return "Auth API Working!";
    }

    // default / home page
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "public-home";
    }

    // Register page render's
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("questions", securityQuestionRepository.findAll());
        return "register";
    }

    // register the user
    @PostMapping("/register-user")
    public String register(User user) {
        userService.registerUser(user);
        return "redirect:/login";
    }

    // login form render's
    @GetMapping("/login")
    public String showLoginForm() {
//        System.out.println("displaying login form");
        return "login";
    }

    // show access denied page for unauthorize request
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }

}