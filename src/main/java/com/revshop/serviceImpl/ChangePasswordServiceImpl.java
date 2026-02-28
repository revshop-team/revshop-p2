package com.revshop.serviceImpl;

import com.revshop.entity.User;
import com.revshop.repo.UserRepository;
import com.revshop.serviceInterfaces.ChangePasswordService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class ChangePasswordServiceImpl implements ChangePasswordService {

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

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return "User not found";
        }

        // check current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return "Current password is incorrect";
        }

        // check new password match
        if (!newPassword.equals(confirmPassword)) {
            return "New passwords do not match";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "success";
    }
}
