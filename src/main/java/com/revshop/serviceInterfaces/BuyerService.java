package com.revshop.serviceInterfaces;

import com.revshop.entity.BuyerDetails;

public interface BuyerService {

    BuyerDetails getBuyerDetailsByEmail(String email);

    void updateBuyerDetails(String email, BuyerDetails buyerDetails);
}