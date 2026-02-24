package com.revshop.serviceImpl;

import com.revshop.entity.User;
import com.revshop.exceptions.EmailAlreadyExistsException;
import com.revshop.repo.UserRepository;
import com.revshop.serviceInterfaces.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerUser(User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new EmailAlreadyExistsException(("Email is already registered"));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
    }



}
