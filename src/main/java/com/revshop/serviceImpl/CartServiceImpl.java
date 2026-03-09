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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);


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

        logger.info("Add to cart request received. ProductId: {}, BuyerEmail: {}", productId, buyerEmail);


        // GET BUYER
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> {
                    logger.error("Buyer not found with email: {}", buyerEmail);
                    return new BuyerNotFoundException("Buyer not found");
                });

        logger.debug("Buyer found with ID: {}", buyer.getUserId());


        // FIND OR CREATE CART
        Cart cart = cartRepository.findByBuyer(buyer)
                .orElseGet(() -> {
                    logger.info("Cart not found for buyer. Creating new cart.");

                    Cart newCart = new Cart();
                    newCart.setBuyer(buyer);
                    newCart.setCreatedAt(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });

        // GET PRODUCT
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.error("Product not found with id: {}", productId);

                    return new ProductNotFoundException("Product not found");
                });
        logger.debug("Product found: {}", product.getProductName());

        // ⭐ STOCK CHECK (ADD THIS BLOCK)
        if (product.getStock() <= 0) {
            logger.warn("Product {} is out of stock", product.getProductName());

            throw new RuntimeException("Product is out of stock");
        }
        //  CHECK IF PRODUCT ALREADY IN CART
        Optional<CartItem> existingItem =
                cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem.isPresent()) {

            CartItem item = existingItem.get();

            // ⭐ PREVENT EXCEEDING STOCK (ADD THIS)
            if (item.getQuantity() + 1 > product.getStock()) {
                logger.warn("Stock limit exceeded for product: {}", product.getProductName());

                throw new RuntimeException("Cannot add more than available stock");
            }


            item.setQuantity(item.getQuantity() + 1);
            cartItemRepository.save(item);

            logger.info("Product quantity increased in cart. Product: {}, Quantity: {}",
                    product.getProductName(), item.getQuantity());

        } else {

            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setSeller(product.getSeller());
            newItem.setQuantity(1);

            cartItemRepository.save(newItem);

            logger.info("New product added to cart: {}", product.getProductName());

        }
    }

    @Override
    public Cart getCartByBuyer(String buyerEmail) {
        logger.info("Fetching cart for buyer email: {}", buyerEmail);

        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> {
                    logger.error("Buyer not found while fetching cart: {}", buyerEmail);
                    return new BuyerNotFoundException("Buyer not found");
                });

        return cartRepository.findByBuyer(buyer)
                .orElseGet(() -> {
                    logger.warn("Cart not found for buyer. Creating new cart.");

                    Cart newCart = new Cart();
                        newCart.setBuyer(buyer);
                        newCart.setCreatedAt(LocalDateTime.now());
                        return cartRepository.save(newCart);
                });
    }
    @Override
    @Transactional
    public void increaseQuantity(Long cartItemId) {
        logger.info("Increasing cart item quantity. CartItemId: {}", cartItemId);


        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> {
                    logger.error("Cart item not found with id: {}", cartItemId);
                    return new RuntimeException("Cart item not found");
                });

        Product product = item.getProduct();

        // 🚨 CRITICAL STOCK VALIDATION (THIS WAS MISSING)
        if (item.getQuantity() >= product.getStock()) {
            logger.warn("Stock limit reached for product: {}", product.getProductName());

            throw new RuntimeException("Maximum stock limit reached for: " + product.getProductName());
        }

        item.setQuantity(item.getQuantity() + 1);
        cartItemRepository.save(item);
        logger.info("Cart item quantity increased. New quantity: {}", item.getQuantity());

    }

    @Override
    @Transactional
    public void decreaseQuantity(Long cartItemId) {
        logger.info("Decreasing cart item quantity. CartItemId: {}", cartItemId);


        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> {
                    logger.error("Cart item not found with id: {}", cartItemId);
                    return new CartItemNotFoundException("Cart item not found");
                });

        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
            cartItemRepository.save(item);
            logger.info("Cart item quantity decreased. New quantity: {}", item.getQuantity());


        } else {
            cartItemRepository.delete(item);
            logger.warn("Cart item removed because quantity reached zero. CartItemId: {}", cartItemId);

        }
    }

    @Override
    public void removeCartItem(Long cartItemId) {
        logger.info("Removing cart item. CartItemId: {}", cartItemId);

        cartItemRepository.deleteById(cartItemId);
        logger.info("Cart item removed successfully. CartItemId: {}", cartItemId);

    }
}
