package com.revshop.repo;

import com.revshop.entity.BuyerDetails;
import com.revshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuyerDetailsRepository extends JpaRepository<BuyerDetails, Long> {

//    Optional<BuyerDetails> findByUserEmail(String email);

//    Optional<User> findByEmail(String email);

    Optional<BuyerDetails> findByUser_Email(String email);

}
