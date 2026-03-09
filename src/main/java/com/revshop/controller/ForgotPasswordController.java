package com.revshop.controller;

import com.revshop.serviceInterfaces.ForgotPasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotPasswordController {
    private static final Logger logger =
            LoggerFactory.getLogger(ForgotPasswordController.class);

    private final ForgotPasswordService forgotPasswordService;

    public ForgotPasswordController(ForgotPasswordService forgotPasswordService) {
        this.forgotPasswordService = forgotPasswordService;
    }

    // FORGOT PASSWORD RENDER'S
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        logger.info("Forgot password page requested");

        return "forgot-password";
    }

    // VERIFY THE USER EMAIL IS REGISTER OR NOT
    @PostMapping("/verify-email")
    public String verifyEmail(@RequestParam String email, Model model) {
        logger.info("Password reset email verification attempt for {}", email);

        boolean exists = forgotPasswordService.verifyEmail(email);

        if (!exists) {
            logger.warn("Email not registered: {}", email);

            model.addAttribute("error", "Email not registered!");
            return "forgot-password";
        }

        String question = forgotPasswordService.getSecurityQuestion(email);
        logger.debug("Security question fetched for {}", email);

        model.addAttribute("showResetForm", true);
        model.addAttribute("email", email);
        model.addAttribute("question", question);

        return "forgot-password";
    }

    // UPATE NEW PASSWORD
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email,
                                @RequestParam String securityAnswer,
                                @RequestParam String newPassword,
                                Model model) {
        logger.info("Password reset attempt for {}", email);

        boolean success = forgotPasswordService.resetPassword(email, securityAnswer, newPassword);

        if (!success) {
            model.addAttribute("error", "Incorrect security answer!");
            model.addAttribute("showResetForm", true);
            model.addAttribute("email", email);
            model.addAttribute("question",
                    forgotPasswordService.getSecurityQuestion(email));
            return "forgot-password";
        }
        logger.info("Password successfully reset for {}", email);

        model.addAttribute("success", "Password changed successfully!");
        return "forgot-password";
    }
}