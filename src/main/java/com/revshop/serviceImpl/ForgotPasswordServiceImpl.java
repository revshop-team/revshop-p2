package com.revshop.serviceImpl;

import com.revshop.entity.User;
import com.revshop.repo.UserRepository;
import com.revshop.serviceInterfaces.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public ForgotPasswordServiceImpl(UserRepository userRepository,
                                     BCryptPasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean verifyEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public String getSecurityQuestion(String email) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || user.getSecurityQuestion() == null) {
            return null;
        }

        return user.getSecurityQuestion().getQuestionText();
    }

    @Override
    public boolean resetPassword(String email, String securityAnswer, String newPassword) {

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return false;
        }

        if (!user.getSecurityAnswer().equalsIgnoreCase(securityAnswer)) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return true;
    }
}