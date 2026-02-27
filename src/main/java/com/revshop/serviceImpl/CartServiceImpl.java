package com.revshop.serviceImpl;

import com.revshop.entity.Cart;
import com.revshop.entity.CartItem;
import com.revshop.entity.Product;
import com.revshop.entity.User;
import com.revshop.exceptions.BuyerNotFoundException;
import com.revshop.exceptions.CartItemNotFoundException;
import com.revshop.exceptions.ProductNotFoundException;
import com.revshop.repo.CartItemRepository;
import com.revshop.repo.CartRepository;
import com.revshop.repo.ProductRepository;
import com.revshop.repo.UserRepository;
import com.revshop.serviceInterfaces.CartService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }


    @Override
    @Transactional
    public void addToCart(Long productId, String buyerEmail) {

        // GET BUYER
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new BuyerNotFoundException("Buyer not found"));

        // FIND OR CREATE CART
        Cart cart = cartRepository.findByBuyer(buyer)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setBuyer(buyer);
                    newCart.setCreatedAt(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });

        // GET PRODUCT
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        // ⭐ STOCK CHECK (ADD THIS BLOCK)
        if (product.getStock() <= 0) {
            throw new RuntimeException("Product is out of stock");
        }
        //  CHECK IF PRODUCT ALREADY IN CART
        Optional<CartItem> existingItem =
                cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem.isPresent()) {

            CartItem item = existingItem.get();

            // ⭐ PREVENT EXCEEDING STOCK (ADD THIS)
            if (item.getQuantity() + 1 > product.getStock()) {
                throw new RuntimeException("Cannot add more than available stock");
            }

            item.setQuantity(item.getQuantity() + 1);
            cartItemRepository.save(item);
            item.setQuantity(item.getQuantity() + 1);
            cartItemRepository.save(item);

        } else {

            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setSeller(product.getSeller());
            newItem.setQuantity(1);

            cartItemRepository.save(newItem);
        }
    }

    @Override
    public Cart getCartByBuyer(String buyerEmail) {
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new BuyerNotFoundException("Buyer not found"));

        return cartRepository.findByBuyer(buyer)
                .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setBuyer(buyer);
                        newCart.setCreatedAt(LocalDateTime.now());
                        return cartRepository.save(newCart);
                });
    }
    @Override
    @Transactional
    public void increaseQuantity(Long cartItemId) {

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Product product = item.getProduct();

        // 🚨 CRITICAL STOCK VALIDATION (THIS WAS MISSING)
        if (item.getQuantity() >= product.getStock()) {
            throw new RuntimeException("Maximum stock limit reached for: " + product.getProductName());
        }

        item.setQuantity(item.getQuantity() + 1);
        cartItemRepository.save(item);
    }

    @Override
    @Transactional
    public void decreaseQuantity(Long cartItemId) {

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() ->
                        new CartItemNotFoundException("Cart item not found"));

        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
            cartItemRepository.save(item);

        } else {
            cartItemRepository.delete(item);
        }
    }
}
