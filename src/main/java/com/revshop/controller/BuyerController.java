package com.revshop.controller;

import com.revshop.entity.*;
import com.revshop.repo.ReviewRepository;
import com.revshop.serviceInterfaces.*;
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
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BuyerService buyerService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReviewRepository reviewRepository;


    // buyer's home page
    @GetMapping("/home")
    public String buyerHome(Authentication authentication, Model model) {

        String buyerEmail = authentication.getName();

        List<Category> categories = categoryService.getAllCategories();

        PageRequest pageable = PageRequest.of(
                0,
                8,
                Sort.by("createdAt").descending()
        );

        Page<Product> latestProducts =
                productService.getActiveProducts(pageable);

//        model.addAttribute("categories", categories);
        model.addAttribute("latestProducts", latestProducts.getContent());
        model.addAttribute("buyerEmail", buyerEmail);

        return "buyer/home";
    }

//    ------------------------------------------------------------------------------------------------
    
    // buyer profile
    @GetMapping("/profile")
    public String viewProfile(@RequestParam(defaultValue = "false") boolean edit,
                              Authentication authentication,
                              Model model) {

        String email = authentication.getName();

        // 1️⃣ Existing profile logic (KEEP THIS)
        BuyerDetails buyerDetails =
                buyerService.getBuyerDetailsByEmail(email);

        // 2️⃣ New stats logic (ADD THIS)
        List<Order> orders =
                orderService.getOrdersByBuyer(email);

        int totalOrders = orders.size();

        double totalSpending = 0;
        for (Order order : orders) {
            totalSpending += order.getTotalAmount();
        }

        // 3️⃣ Add everything to model (KEEP + ADD)
        model.addAttribute("buyerDetails", buyerDetails);
        model.addAttribute("editMode", edit);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalSpending", totalSpending);

        return "buyer/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(Authentication authentication,
                                @ModelAttribute BuyerDetails buyerDetails) {

        String email = authentication.getName();

        buyerService.updateBuyerDetails(email, buyerDetails);

        return "redirect:/buyer/profile";
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
        List<Review> reviews =
                reviewRepository.findByProduct_ProductIdOrderByReviewDateDesc(id);

        model.addAttribute("product", product);
        model.addAttribute("reviews", reviews);

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

    // increase the cart items quantity
    @GetMapping("/cart/increase/{id}")
    public String increaseQty(@PathVariable Long id) {

        cartService.increaseQuantity(id);

        return "redirect:/buyer/cart";
    }

    // decrease the cart items quantity
    @GetMapping("/cart/decrease/{id}")
    public String decreaseQty(@PathVariable Long id) {

        cartService.decreaseQuantity(id);

        return "redirect:/buyer/cart";
    }


    // checkout page render's
    @GetMapping("/cart/checkout")
    public String checkoutPage() {
        return "buyer/checkout";
    }

    // checkout the cart items
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

    // see all order's of buyer
    @GetMapping("/orders")
    public String viewOrders(Authentication authentication, Model model) {

        String buyerEmail = authentication.getName();

        List<Order> orders = orderService.getOrdersByBuyer(buyerEmail);

        model.addAttribute("orders", orders);
        model.addAttribute("buyerEmail", buyerEmail);

        return "buyer/orders";
    }

    @PostMapping("/review")
    public String submitReview(@RequestParam Long orderId,
                               @RequestParam Long productId,
                               @RequestParam Integer rating,
                               @RequestParam String comment,
                               java.security.Principal principal) {

        reviewService.addReview(
                orderId,
                productId,
                rating,
                comment,
                principal.getName()
        );

        return "redirect:/buyer/orders";
    }
}
