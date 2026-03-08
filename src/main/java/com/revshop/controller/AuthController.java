package com.revshop.controller;

import com.revshop.entity.Product;
import com.revshop.entity.User;
import com.revshop.repo.SecurityQuestionRepository;
import com.revshop.repo.UserRepository;
import com.revshop.serviceImpl.UserServiceImpl;
import com.revshop.serviceInterfaces.CategoryService;
import com.revshop.serviceInterfaces.ProductService;
import com.revshop.serviceInterfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
public class AuthController {

    private final SecurityQuestionRepository securityQuestionRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;

    public AuthController(SecurityQuestionRepository securityQuestionRepository,
                          UserRepository userRepository,
                          BCryptPasswordEncoder passwordEncoder,
                          ProductService productService,
                          CategoryService categoryService,
                          UserServiceImpl userService) {

        this.securityQuestionRepository = securityQuestionRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.productService = productService;
        this.categoryService = categoryService;
        this.userService = userService;
    }


    // Test endpoint
    @GetMapping("/test")
    public String test() {
        return "Auth API Working!";
    }

    // default / home page
    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("products", productService.get12Products());
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
//    @PostMapping("/register-user")
//    public String register(User user) {
//        userService.registerUser(user);
//        return "redirect:/login";
//    }

    @PostMapping("/register-user")
    public String register(User user,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        try {

            userService.registerUser(user);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Registration successful 🎉 Please login"
            );

            return "redirect:/login";

        } catch (Exception e) {

            model.addAttribute("error",
                    "Email already registered");

            model.addAttribute("user", user);
            model.addAttribute("questions",
                    securityQuestionRepository.findAll());

            return "register";
        }
    }

    // login form render's
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // show access denied page for unauthorize request
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }

}