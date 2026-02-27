package com.revshop.serviceImpl;

import com.revshop.entity.BuyerDetails;
import com.revshop.entity.User;
import com.revshop.exceptions.UserNotFoundException;
import com.revshop.repo.BuyerDetailsRepository;
import com.revshop.repo.UserRepository;
import com.revshop.serviceInterfaces.BuyerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuyerServiceImpl implements BuyerService {

    private final BuyerDetailsRepository buyerDetailsRepository;
    private final UserRepository userRepository;

    public BuyerServiceImpl(BuyerDetailsRepository buyerDetailsRepository,
                            UserRepository userRepository) {
        this.buyerDetailsRepository = buyerDetailsRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BuyerDetails getBuyerDetailsByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return buyerDetailsRepository.findById(user.getUserId())
                .orElseGet(() -> {
                    BuyerDetails empty = new BuyerDetails();
                    empty.setUser(user);

                    return empty;
                });
    }

    @Override
    public void updateBuyerDetails(String email,
                                   BuyerDetails updatedDetails) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        BuyerDetails existing = buyerDetailsRepository
                .findById(user.getUserId())
                .orElse(new BuyerDetails());

        existing.setUser(user);
        existing.setFullName(updatedDetails.getFullName());
        existing.setGender(updatedDetails.getGender());
        existing.setDateOfBirth(updatedDetails.getDateOfBirth());
        existing.setPhone(updatedDetails.getPhone());

        buyerDetailsRepository.save(existing);
    }
}
