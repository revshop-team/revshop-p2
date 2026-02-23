package com.revshop.repository;

import com.revshop.entity.Cart;
import com.revshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByBuyer(User buyer);
}