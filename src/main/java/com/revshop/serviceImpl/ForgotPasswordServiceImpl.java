package com.revshop.serviceImpl;

import com.revshop.entity.User;
import com.revshop.repo.UserRepository;
import com.revshop.serviceInterfaces.ForgotPasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {
    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordServiceImpl.class);


    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public ForgotPasswordServiceImpl(UserRepository userRepository,
                                     BCryptPasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean verifyEmail(String email) {
        logger.info("Verifying email for forgot password request: {}", email);

        boolean exists = userRepository.findByEmail(email).isPresent();
        if (exists) {
            logger.debug("Email found in database: {}", email);
        } else {
            logger.warn("Email not found in database: {}", email);
        }

        return exists;
    }

    @Override
    public String getSecurityQuestion(String email) {
        logger.info("Fetching security question for email: {}", email);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            logger.error("User not found while fetching security question: {}", email);
            return null;
        }

        if (user.getSecurityQuestion() == null) {
            logger.warn("Security question not set for user: {}", email);
            return null;
        }

        logger.debug("Security question retrieved for user: {}", email);


        return user.getSecurityQuestion().getQuestionText();
    }

    @Override
    public boolean resetPassword(String email, String securityAnswer, String newPassword) {
        logger.info("Password reset attempt for email: {}", email);


        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            logger.error("User not found during password reset: {}", email);

            return false;
        }

        if (!user.getSecurityAnswer().equalsIgnoreCase(securityAnswer)) {
            logger.warn("Incorrect security answer provided for email: {}", email);

            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("Password reset successful for user: {}", email);


        return true;
    }
}