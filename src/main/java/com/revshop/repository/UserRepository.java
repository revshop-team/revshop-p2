package com.revshop.repository;

import com.revshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // For login (email-based authentication)
    Optional<User> findByEmail(String email);
}