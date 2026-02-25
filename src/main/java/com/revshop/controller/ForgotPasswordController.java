package com.revshop.controller;

import com.revshop.serviceInterfaces.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotPasswordController {

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/verify-email")
    public String verifyEmail(@RequestParam String email, Model model) {

        boolean exists = forgotPasswordService.verifyEmail(email);

        if (!exists) {
            model.addAttribute("error", "Email not registered!");
            return "forgot-password";
        }

        String question = forgotPasswordService.getSecurityQuestion(email);

        model.addAttribute("showResetForm", true);
        model.addAttribute("email", email);
        model.addAttribute("question", question);

        return "forgot-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email,
                                @RequestParam String securityAnswer,
                                @RequestParam String newPassword,
                                Model model) {

        boolean success = forgotPasswordService.resetPassword(email, securityAnswer, newPassword);

        if (!success) {
            model.addAttribute("error", "Incorrect security answer!");
            model.addAttribute("showResetForm", true);
            model.addAttribute("email", email);
            model.addAttribute("question",
                    forgotPasswordService.getSecurityQuestion(email));
            return "forgot-password";
        }

        model.addAttribute("success", "Password changed successfully!");
        return "forgot-password";
    }
}