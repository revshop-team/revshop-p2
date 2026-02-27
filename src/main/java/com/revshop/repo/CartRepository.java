package com.revshop.repo;

import com.revshop.entity.Cart;
import com.revshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * @param buyer
     * @return  Optional<Cart>
     */
    Optional<Cart> findByBuyer(User buyer);

}
