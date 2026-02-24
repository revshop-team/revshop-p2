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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {

    private final SecurityQuestionRepository securityQuestionRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(SecurityQuestionRepository securityQuestionRepository,
                          UserRepository userRepository,
                          BCryptPasswordEncoder passwordEncoder) {
        this.securityQuestionRepository = securityQuestionRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    private UserServiceImpl userService;

    // Test endpoint
    @GetMapping("/test")
    public String test() {
        return "Auth API Working!";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("questions", securityQuestionRepository.findAll());
        return "register";
    }

    @PostMapping("/register-user")
    public String register(User user) {
        userService.registerUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        System.out.println("displaying login form");
        return "login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }

    // forgot password page render api
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/verify-email")
    public String verifyEmail(@RequestParam String email, Model model) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            model.addAttribute("email", user.getEmail());
            model.addAttribute("question", user.getSecurityQuestion().getQuestionText());
            model.addAttribute("showResetForm", true);

            return "forgot-password";
        }

        model.addAttribute("error", "Email not found");
        return "forgot-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email,
                                @RequestParam String securityAnswer,
                                @RequestParam String newPassword,
                                Model model) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {

            User user = optionalUser.get();

            if (user.getSecurityAnswer().equalsIgnoreCase(securityAnswer)) {

                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);

                model.addAttribute("success", "Password changed successfully");
                return "forgot-password";
            }
        }

        model.addAttribute("error", "Invalid security answer");
        model.addAttribute("showResetForm", true);
        model.addAttribute("email", email);

        return "forgot-password";
    }

}