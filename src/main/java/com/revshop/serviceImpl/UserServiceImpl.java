package com.revshop.serviceImpl;

import com.revshop.entity.User;
import com.revshop.exceptions.EmailAlreadyExistsException;
import com.revshop.exceptions.UserNotFoundException;
import com.revshop.repo.UserRepository;
import com.revshop.serviceInterfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerUser(User user) {
        logger.info("User registration request received for email: {}", user.getEmail());


        if (userRepository.findByEmail(user.getEmail()).isPresent()){
            logger.warn("Registration failed. Email already exists: {}", user.getEmail());

            throw new EmailAlreadyExistsException(("Email is already registered"));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
        logger.info("User registered successfully with email: {}", user.getEmail());

    }

    @Override
    public User findByEmail(String email) {
        logger.info("Fetching user by email: {}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new UserNotFoundException("User not found: " + email);
                });

    }

    @Override
    public User getUserById(Long userId) {
        logger.info("Fetching user by ID: {}", userId);

        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with id: {}", userId);
                    return new RuntimeException("User not found");
                });
    }


}
