package com.revshop.repo;

import com.revshop.entity.SellerDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerDetailsRepository extends JpaRepository<SellerDetails, Long> {

    Optional<SellerDetails> findByUserUserId(Long userId);
}