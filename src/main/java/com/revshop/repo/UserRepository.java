package com.revshop.repo;

import com.revshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     *
     * @param email
     * @return  Optional<User>
     */
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Add this for username check
}
