package com.revshop.serviceImpl;

import com.revshop.entity.SellerDetails;
import com.revshop.entity.User;
import com.revshop.repo.SellerDetailsRepository;
import com.revshop.serviceInterfaces.SellerService;
import org.springframework.stereotype.Service;

@Service
public class SellerServiceImpl implements SellerService {

    private final SellerDetailsRepository sellerRepo;

    public SellerServiceImpl(SellerDetailsRepository sellerRepo) {
        this.sellerRepo = sellerRepo;
    }

    @Override
    public SellerDetails getSellerDetails(Long userId) {
        return sellerRepo.findByUserUserId(userId)
                .orElse(null);
    }

    @Override
    public void saveOrUpdateSeller(User user, SellerDetails details) {

        details.setUser(user);   // important for @MapsId
        sellerRepo.save(details);
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return sellerRepo.findByUserUserId(userId).isPresent();
    }
}
