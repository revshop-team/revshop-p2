package com.revshop.service;

import com.revshop.entity.User;
import com.revshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // 🔥 from SecurityConfig bean

    @Override
    public User registerUser(User user) {

        // 🔐 ENCODE PASSWORD BEFORE SAVING (CRITICAL)
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Default role if not set
        if (user.getRole() == null) {
            user.setRole("BUYER");
        }

        return userRepository.save(user);
    }
}