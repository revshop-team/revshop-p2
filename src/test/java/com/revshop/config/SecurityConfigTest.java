package com.revshop.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class SecurityConfigTest {

    @Mock private UserDetailsService userDetailsService;
    @Mock private CustomSuccessHandler successHandler;

    @InjectMocks private SecurityConfig securityConfig;

    @Test
    void testPasswordEncoderBean() {
        BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder);
    }

    @Test
    void testAuthenticationProviderBean() {
        assertNotNull(securityConfig.authenticationProvider());
    }
}