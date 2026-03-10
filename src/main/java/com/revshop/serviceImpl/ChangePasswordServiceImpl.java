package com.revshop.serviceImpl;

import com.revshop.entity.User;
import com.revshop.repo.UserRepository;
import com.revshop.serviceInterfaces.ChangePasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class ChangePasswordServiceImpl implements ChangePasswordService {
    private static final Logger logger = LoggerFactory.getLogger(ChangePasswordServiceImpl.class);


    private UserRepository userRepository;
    private  BCryptPasswordEncoder passwordEncoder;

    public ChangePasswordServiceImpl(UserRepository userRepository,
                                     BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String changePassword(String email,
                                 String currentPassword,
                                 String newPassword,
                                 String confirmPassword) {
        logger.info("Password change request received for email: {}", email);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            logger.error("User not found with email: {}", email);

            return "User not found";
        }
        logger.debug("User found with ID: {}", user.getUserId());


        // check current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            logger.warn("Incorrect current password entered for email: {}", email);

            return "Current password is incorrect";
        }

        // check new password match
        if (!newPassword.equals(confirmPassword)) {
            logger.warn("New password and confirm password do not match for email: {}", email);

            return "New passwords do not match";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("Password successfully changed for user: {}", email);


        return "success";
    }
}
