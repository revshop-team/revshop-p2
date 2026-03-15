package com.revshop.serviceImpl;

import com.revshop.entity.BuyerDetails;
import com.revshop.entity.SellerDetails;
import com.revshop.entity.User;
import com.revshop.exceptions.EmailAlreadyExistsException;
import com.revshop.exceptions.UserNotFoundException;
import com.revshop.repo.BuyerDetailsRepository;
import com.revshop.repo.SellerDetailsRepository;
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
    private final BuyerDetailsRepository buyerDetailsRepository;
    private final SellerDetailsRepository sellerDetailsRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           BuyerDetailsRepository buyerDetailsRepository,
                           SellerDetailsRepository sellerDetailsRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.buyerDetailsRepository = buyerDetailsRepository;
        this.sellerDetailsRepository = sellerDetailsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerUser(User user) {

        // EMAIL check
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // ===== BUYER VALIDATION =====
        if ("BUYER".equals(user.getRole())) {

            BuyerDetails buyer = user.getBuyerDetails();

            if (buyer == null) {
                throw new RuntimeException("Buyer details required");
            }

            if (buyerDetailsRepository.existsByFullName(buyer.getFullName())) {
                throw new RuntimeException("Full name already taken");
            }

            // ⭐ IMPORTANT GLOBAL PHONE CHECK
            if (buyerDetailsRepository.existsByPhone(buyer.getPhone())
                    || sellerDetailsRepository.existsByPhone(buyer.getPhone())) {

                throw new RuntimeException("Phone already registered in system");
            }

            buyer.setUser(user);
        }

        // ===== SELLER VALIDATION =====
        if ("SELLER".equals(user.getRole())) {

            SellerDetails seller = user.getSellerDetails();

            if (seller == null) {
                throw new RuntimeException("Seller details required");
            }

            if (sellerDetailsRepository.existsByBusinessName(seller.getBusinessName())) {
                throw new RuntimeException("Business name already exists");
            }

            if (sellerDetailsRepository.existsByGstNumber(seller.getGstNumber())) {
                throw new RuntimeException("GST already registered");
            }

            // ⭐ IMPORTANT GLOBAL PHONE CHECK
            if (sellerDetailsRepository.existsByPhone(seller.getPhone())
                    || buyerDetailsRepository.existsByPhone(seller.getPhone())) {

                throw new RuntimeException("Phone already registered in system");
            }

            seller.setUser(user);
        }

        // PASSWORD encode
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
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
