package com.revshop.repo;

import com.revshop.entity.Cart;
import com.revshop.entity.CartItem;
import com.revshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * @param cart
     * @param product
     * @return
     */
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    /**
     * @param cart
     * @return
     */
    List<CartItem> findByCart(Cart cart);

    void deleteByCartItemId(Long cartItemId);

}