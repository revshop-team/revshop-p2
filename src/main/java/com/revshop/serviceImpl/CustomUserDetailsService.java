package com.revshop.serviceImpl;

import com.revshop.entity.User;
import com.revshop.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);


    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Authentication attempt for user email: {}", username);


        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.error("User not found during authentication for email: {}", username);
                    return new UsernameNotFoundException("user not found");
                });
        logger.debug("User authenticated successfully. Role: {}", user.getRole());


        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole())   // example: BUYER / SELLER
                .build();
    }
}
