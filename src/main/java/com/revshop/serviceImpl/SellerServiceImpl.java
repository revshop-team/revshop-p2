package com.revshop.serviceImpl;

import com.revshop.entity.SellerDetails;
import com.revshop.entity.User;
import com.revshop.repo.BuyerDetailsRepository;
import com.revshop.repo.SellerDetailsRepository;
import com.revshop.serviceInterfaces.SellerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SellerServiceImpl implements SellerService {
    private static final Logger logger = LoggerFactory.getLogger(SellerServiceImpl.class);


    private final SellerDetailsRepository sellerRepo;

    private BuyerDetailsRepository buyerDetailsRepository;

    public SellerServiceImpl(SellerDetailsRepository sellerRepo,BuyerDetailsRepository buyerDetailsRepository) {
        this.sellerRepo = sellerRepo;
        this.buyerDetailsRepository = buyerDetailsRepository;
    }

    @Override
    public SellerDetails getSellerDetails(Long userId) {
        logger.info("Fetching seller details for userId: {}", userId);

        SellerDetails sellerDetails = sellerRepo.findByUserUserId(userId)
                .orElse(null);

        if (sellerDetails == null) {
            logger.warn("Seller details not found for userId: {}", userId);
        } else {
            logger.debug("Seller details found for userId: {}", userId);
        }

        return sellerDetails;
    }

    @Override
    public void saveOrUpdateSeller(User user, SellerDetails details) {
        logger.info("Saving or updating seller details for userId: {}", user.getUserId());

        SellerDetails existing =
                sellerRepo.findByUser_Email(user.getEmail())
                        .orElse(new SellerDetails());

        String newPhone = details.getPhone();

        // ⭐ SELLER DUPLICATE CHECK (exclude self)
        boolean sellerPhoneExists =
                sellerRepo.existsByPhone(newPhone);

        if (sellerPhoneExists &&
                existing.getPhone() != null &&
                !existing.getPhone().equals(newPhone)) {

            throw new RuntimeException("Phone already used by another seller");
        }

        Optional<SellerDetails> businessExists =
                sellerRepo.findByBusinessName(details.getBusinessName());

        if (businessExists.isPresent() &&
                !businessExists.get().getSellerId()
                        .equals(existing.getSellerId())) {

            throw new RuntimeException("Business name already used by another seller");
        }
        // ⭐ BUYER DUPLICATE CHECK
        boolean buyerPhoneExists =
                buyerDetailsRepository.existsByPhone(newPhone);

        if (buyerPhoneExists) {
            throw new RuntimeException("Phone already used by a buyer");
        }

        details.setUser(user);   // important for @MapsId
        sellerRepo.save(details);
        logger.debug("Seller details saved successfully for userId: {}", user.getUserId());

    }

    @Override
    public boolean existsByUserId(Long userId) {
        logger.info("Checking if seller exists for userId: {}", userId);

        boolean exists = sellerRepo.findByUserUserId(userId).isPresent();

        logger.debug("Seller existence for userId {} : {}", userId, exists);

        return exists;
    }
}
