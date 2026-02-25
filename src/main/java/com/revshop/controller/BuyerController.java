package com.revshop.controller;

import com.revshop.entity.Cart;
import com.revshop.entity.Order;
import com.revshop.entity.Product;
import com.revshop.serviceInterfaces.CartService;
import com.revshop.serviceInterfaces.OrderService;
import com.revshop.serviceInterfaces.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/buyer")
public class BuyerController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;


    @GetMapping("/home")
    public String buyerHome() {
        return "buyer/home";
    }

    // see buyer order's
//    @GetMapping("/orders")
//    public String buyerOrders() {
//        return "buyer/orders";
//    }

    // see buyer profile
    @GetMapping("/profile")
    public String buyerProfile() {
        return "buyer/profile";
    }

    //  view all products exist in RevShop
    @GetMapping("/products")
    public String viewProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "default") String sort,
            Model model) {

        Sort sorting = Sort.unsorted();

        if (sort.equals("priceAsc")) {
            sorting = Sort.by("sellingPrice").ascending();
        } else if (sort.equals("priceDesc")) {
            sorting = Sort.by("sellingPrice").descending();
        } else if (sort.equals("newest")) {
            sorting = Sort.by("createdAt").descending();
        }

        PageRequest pageable = PageRequest.of(page, size, sorting);

        Page<Product> productPage = productService.getActiveProducts(pageable);

        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("sort", sort);

        return "buyer/products";
    }

    // view product details
    @GetMapping("/product/{id}")
    public String viewProductDetails(@PathVariable Long id, Model model) {

        Product product = productService.getProductById(id);

        model.addAttribute("product", product);

        return "buyer/product-details";
    }

    // add item to cart
    @GetMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id,
                            Authentication authentication) {

        String buyerEmail = authentication.getName();
        cartService.addToCart(id, buyerEmail);

        return "redirect:/buyer/products";
    }

    // see cart
    @GetMapping("/cart")
    public String viewCart(Model model,
                           Authentication authentication) {

        String buyerEmail = authentication.getName();

        Cart cart = cartService.getCartByBuyer(buyerEmail);

        model.addAttribute("cart", cart);

        return "buyer/cart";
    }

    // increase the cart items
    @GetMapping("/cart/increase/{id}")
    public String increaseQty(@PathVariable Long id) {

        cartService.increaseQuantity(id);

        return "redirect:/buyer/cart";
    }

    // decrease the cart items
    @GetMapping("/cart/decrease/{id}")
    public String decreaseQty(@PathVariable Long id) {

        cartService.decreaseQuantity(id);

        return "redirect:/buyer/cart";
    }


    // IMPORTANT: This was missing
    @GetMapping("/cart/checkout")
    public String checkoutPage() {
        return "buyer/checkout";
    }

    @PostMapping("/cart/checkout")
    public String checkout(Authentication authentication,
                           @RequestParam String fullName,
                           @RequestParam String phone,
                           @RequestParam String addressLine1,
                           @RequestParam String addressLine2,
                           @RequestParam String city,
                           @RequestParam String state,
                           @RequestParam String pincode) {

        String buyerEmail = authentication.getName();

        orderService.checkout(
                buyerEmail,
                fullName,
                phone,
                addressLine1,
                addressLine2,
                city,
                state,
                pincode
        );

        return "redirect:/buyer/orders";
    }

    @GetMapping("/orders")
    public String viewOrders(Authentication authentication, Model model) {

        String buyerEmail = authentication.getName();

        List<Order> orders = orderService.getOrdersByBuyer(buyerEmail);

        model.addAttribute("orders", orders);

        return "buyer/orders";
    }
}
