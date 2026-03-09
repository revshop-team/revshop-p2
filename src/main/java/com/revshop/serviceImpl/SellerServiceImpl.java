package com.revshop.serviceImpl;

import com.revshop.entity.SellerDetails;
import com.revshop.entity.User;
import com.revshop.repo.SellerDetailsRepository;
import com.revshop.serviceInterfaces.SellerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SellerServiceImpl implements SellerService {
    private static final Logger logger = LoggerFactory.getLogger(SellerServiceImpl.class);


    private final SellerDetailsRepository sellerRepo;

    public SellerServiceImpl(SellerDetailsRepository sellerRepo) {
        this.sellerRepo = sellerRepo;
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
