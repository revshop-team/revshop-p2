package com.revshop.exceptions;

import com.revshop.entity.User;
import com.revshop.serviceInterfaces.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {
    private final UserService userService;

    public GlobalControllerAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("businessName")
    public String addBusinessName(Authentication authentication) {
        if (authentication != null) {
            User user = userService.findByEmail(authentication.getName());
            return user.getSellerDetails() != null
                    ? user.getSellerDetails().getBusinessName()
                    : "Seller";
        }
        return "Seller";
    }
}
