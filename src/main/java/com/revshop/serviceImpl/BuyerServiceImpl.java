package com.revshop.serviceImpl;

import com.revshop.entity.BuyerDetails;
import com.revshop.entity.User;
import com.revshop.exceptions.UserNotFoundException;
import com.revshop.repo.BuyerDetailsRepository;
import com.revshop.repo.SellerDetailsRepository;
import com.revshop.repo.UserRepository;
import com.revshop.serviceInterfaces.BuyerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuyerServiceImpl implements BuyerService {
    private static final Logger logger = LoggerFactory.getLogger(BuyerServiceImpl.class);

    private final BuyerDetailsRepository buyerDetailsRepository;
    private final UserRepository userRepository;
    private final SellerDetailsRepository sellerDetailsRepository;

    public BuyerServiceImpl(BuyerDetailsRepository buyerDetailsRepository,
                            UserRepository userRepository,SellerDetailsRepository sellerDetailsRepository) {
        this.buyerDetailsRepository = buyerDetailsRepository;
        this.userRepository = userRepository;
        this.sellerDetailsRepository = sellerDetailsRepository;
    }

    @Override
    public BuyerDetails getBuyerDetailsByEmail(String email) {
        logger.info("Fetching buyer details for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> { logger.error("User not found with email: {}", email);
                    return new UserNotFoundException("User not found");
                });

        logger.debug("User found with ID: {}", user.getUserId());

        return buyerDetailsRepository.findById(user.getUserId())
                .orElseGet(() -> {
                    logger.warn("BuyerDetails not found for userId: {}. Returning empty object.", user.getUserId());
                    BuyerDetails empty = new BuyerDetails();
                    empty.setUser(user);

                    return empty;
                });
    }

    @Override
    public void updateBuyerDetails(String email,
                                   BuyerDetails updatedDetails) {

        logger.info("Updating buyer details for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->{  logger.error("User not found while updating buyer details: {}", email);
                      return  new UserNotFoundException("User not found");
                });


        BuyerDetails existing = buyerDetailsRepository
                .findById(user.getUserId())
                .orElse(new BuyerDetails());

        String newPhone = updatedDetails.getPhone();

        // ⭐ CHECK BUYER DUPLICATE (excluding self)
        boolean buyerPhoneExists =
                buyerDetailsRepository.existsByPhone(newPhone);

        if (buyerPhoneExists &&
                !existing.getPhone().equals(newPhone)) {

            throw new RuntimeException("Phone already used by another buyer");
        }

        // ⭐ CHECK SELLER DUPLICATE
        boolean sellerPhoneExists =
                sellerDetailsRepository.existsByPhone(newPhone);

        if (sellerPhoneExists) {
            throw new RuntimeException("Phone already used by seller");
        }

        existing.setUser(user);
        existing.setFullName(updatedDetails.getFullName());
        existing.setGender(updatedDetails.getGender());
        existing.setDateOfBirth(updatedDetails.getDateOfBirth());
        existing.setPhone(updatedDetails.getPhone());

        buyerDetailsRepository.save(existing);
        logger.info("Buyer details successfully updated for userId: {}", user.getUserId());

    }
}
