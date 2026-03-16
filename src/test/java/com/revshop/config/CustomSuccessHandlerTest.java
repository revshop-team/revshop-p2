package com.revshop.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomSuccessHandlerTest {

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private Authentication authentication;

    @InjectMocks private CustomSuccessHandler handler;

    @Test
    void testSellerRedirect() throws Exception {
        // Setup: Mock a Seller role
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_SELLER")))
                .when(authentication).getAuthorities();

        // Action
        handler.onAuthenticationSuccess(request, response, authentication);

        // Check: Verify redirect to seller dashboard
        verify(response).sendRedirect("/seller/dashboard");
    }

    @Test
    void testBuyerRedirect() throws Exception {
        // Setup: Mock a Buyer role
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_BUYER")))
                .when(authentication).getAuthorities();

        // Action
        handler.onAuthenticationSuccess(request, response, authentication);

        // Check: Verify redirect to buyer home
        verify(response).sendRedirect("/buyer/home");
    }

    @Test
    void testDefaultRedirect() throws Exception {
        // Setup: Mock no matching roles
        doReturn(Collections.emptyList()).when(authentication).getAuthorities();

        // Action
        handler.onAuthenticationSuccess(request, response, authentication);

        // Check: Verify redirect to login error
        verify(response).sendRedirect("/login?error");
    }
}