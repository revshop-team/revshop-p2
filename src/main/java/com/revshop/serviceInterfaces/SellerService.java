package com.revshop.serviceInterfaces;

import com.revshop.entity.SellerDetails;
import com.revshop.entity.User;

public interface SellerService {

    SellerDetails getSellerDetails(Long userId);

    void saveOrUpdateSeller(User user, SellerDetails details);

    boolean existsByUserId(Long userId);

}
