package com.revshop.repo;

import com.revshop.entity.BuyerDetails;
import com.revshop.entity.SellerDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerDetailsRepository extends JpaRepository<SellerDetails, Long> {

    Optional<SellerDetails> findByUserUserId(Long userId);
    Optional<SellerDetails> findByBusinessName(String businessName); // unique business name check

    boolean existsByBusinessName(String businessName);
    boolean existsByGstNumber(String gstNumber);
    boolean existsByPhone(String phone);
    Optional<SellerDetails> findByUser_Email(String email);
}