package com.revshop.controller;

import com.revshop.entity.BuyerDetails;
import com.revshop.entity.Product;
import com.revshop.entity.SellerDetails;
import com.revshop.entity.User;
import com.revshop.exceptions.EmailAlreadyExistsException;
import com.revshop.repo.SecurityQuestionRepository;
import com.revshop.repo.UserRepository;
import com.revshop.serviceImpl.UserServiceImpl;
import com.revshop.serviceInterfaces.CategoryService;
import com.revshop.serviceInterfaces.ProductService;
import com.revshop.serviceInterfaces.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


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
        logger.info("Test endpoint accessed");

        return "Auth API Working!";
    }

    // default / home page
    @GetMapping("/")
    public String home(Model model) {
        logger.info("Home page requested");


        model.addAttribute("products", productService.get12Products());
        model.addAttribute("categories", categoryService.getAllCategories());

        return "public-home";
    }

    // Register page render's
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        logger.info("Register page requested");

        User user = new User();
        // Initialize BuyerDetails and link to user
        BuyerDetails buyer = new BuyerDetails();
        buyer.setUser(user);         // important!
        user.setBuyerDetails(buyer); // cascade will work

        // Initialize SellerDetails and link to user
        SellerDetails seller = new SellerDetails();
        seller.setUser(user);        // important!
        user.setSellerDetails(seller);

        model.addAttribute("user", user);

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
    public String register(@Valid @ModelAttribute("user") User user, BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        logger.info("User registration attempt for email: {}", user.getEmail());
        System.out.println(user.getEmail());

        if (user.getBuyerDetails() != null) {
            user.getBuyerDetails().setUser(user);
        }
        if (user.getSellerDetails() != null) {
            user.getSellerDetails().setUser(user);
        }

        // Check for validation errors
        if (result.hasErrors()) {
            logger.warn("Validation errors while registering user: {}", result.getAllErrors());

            // Re-add questions for the dropdown
            model.addAttribute("questions", securityQuestionRepository.findAll());
            return "register";  // stay on form with errors displayed
        }

        try {

            userService.registerUser(user);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Registration successful 🎉 Please login"
            );
            return "redirect:/login";

        }

        catch (DataIntegrityViolationException e) {

            logger.error("Database constraint violation", e);

            model.addAttribute("error", "Duplicate value detected");
            model.addAttribute("user",user);

            model.addAttribute("questions", securityQuestionRepository.findAll());

            return "register";
        }

        catch (RuntimeException e) {

            logger.warn("Registration validation failed: {}", e.getMessage());

            model.addAttribute("error", e.getMessage());
            model.addAttribute("user",user);

            model.addAttribute("questions", securityQuestionRepository.findAll());

            return "register";
        }
        catch (Exception e) {

            logger.error("Unexpected registration error", e);

            model.addAttribute("user",user);
            model.addAttribute("error", "Something went wrong");
            model.addAttribute("questions", securityQuestionRepository.findAll());

            return "register";
        }
    }

    // login form render's
    @GetMapping("/login")
    public String showLoginForm() {

        logger.info("Login page requested");
        return "login";
    }

    // show access denied page for unauthorize request
    @GetMapping("/access-denied")
    public String accessDenied() {

        logger.warn("Access denied page accessed");
        return "access-denied";
    }

}