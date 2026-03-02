package com.revshop.serviceInterfaces;

import com.revshop.entity.Cart;

public interface CartService {

    void addToCart(Long productId, String buyerEmail);

    Cart getCartByBuyer(String buyerEmail);

    void increaseQuantity(Long cartItemId);

    void decreaseQuantity(Long cartItemId);

    void removeCartItem(Long cartItemId);
}
