package com.revshop.exceptions;

import com.revshop.entity.SellerDetails;
import com.revshop.entity.User;
import com.revshop.serviceInterfaces.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalControllerAdviceTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private GlobalControllerAdvice controllerAdvice;

    @Test
    void testAddBusinessName_WhenAuthenticationIsNull() {
        // Action: Pass null authentication
        String result = controllerAdvice.addBusinessName(null);

        // Check: Should return fallback "Seller"
        assertEquals("Seller", result);
    }

    @Test
    void testAddBusinessName_WhenSellerDetailsAreNull() {
        // Setup: Logged in, but no seller details attached to user
        User user = new User();

        when(authentication.getName()).thenReturn("seller@test.com");
        when(userService.findByEmail("seller@test.com")).thenReturn(user);

        // Action
        String result = controllerAdvice.addBusinessName(authentication);

        // Check: Should return fallback "Seller"
        assertEquals("Seller", result);
    }

    @Test
    void testAddBusinessName_Success() {
        // Setup: Logged in with valid seller details and business name
        User user = new User();
        SellerDetails details = new SellerDetails();
        details.setBusinessName("RevShop Global");
        user.setSellerDetails(details);

        when(authentication.getName()).thenReturn("seller@test.com");
        when(userService.findByEmail("seller@test.com")).thenReturn(user);

        // Action
        String result = controllerAdvice.addBusinessName(authentication);

        // Check: Should return the exact business name
        assertEquals("RevShop Global", result);
    }
}