package com.revshop.controller;

import com.revshop.entity.Category;
import com.revshop.entity.Product;
import com.revshop.entity.SellerDetails;
import com.revshop.entity.User;
import com.revshop.repo.CategoryRepository;
import com.revshop.serviceInterfaces.NotificationService;
import com.revshop.serviceInterfaces.ProductService;
import com.revshop.serviceInterfaces.SellerService;
import com.revshop.serviceInterfaces.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/seller")
public class SellerController {

    private final ProductService productService;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final SellerService sellerService;
    private final NotificationService notificationService;

    public SellerController(ProductService productService,
                            UserService userService,
                            CategoryRepository categoryRepository, SellerService sellerService, NotificationService notificationService) {

        this.productService = productService;
        this.userService = userService;
        this.categoryRepository = categoryRepository;
        this.sellerService = sellerService;
        this.notificationService = notificationService;
    }

    // RENDER SELLER'S DASHBOARD
    @GetMapping("/dashboard")
    public String sellerDashboard() {
        return "seller/dashboard";
    }

    // SELLER PRODUCTS
    @GetMapping("/products")
    public String sellerProducts() {
        return "seller/products";
    }

    // SELLER'S SALES
    @GetMapping("/sales")
    public String sellerSales() {
        return "seller/sales";
    }

    // show add product page
    @GetMapping("/add-product")
    public String showAddProductForm(Model model) {

        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        System.out.println("Categories: " + categoryRepository.findAll().size());
        return "seller/add-product";
    }

//    // SAVE PRODUCTS
//    @PostMapping("/save-product")
//    public String saveProduct(@ModelAttribute("product") Product product,
//                              @RequestParam(value = "newCategoryName", required = false) String newCategoryName,
//                              Authentication authentication,
//                              Model model){
//
//        String email = authentication.getName();
//        User seller = userService.findByEmail(email);
//
//        Category selectedCategory = product.getCategory();
//
//        boolean hasDropdown = selectedCategory != null && selectedCategory.getCategoryId() != null;
//        boolean hasManual = newCategoryName != null && !newCategoryName.trim().isEmpty();
//
//        // CASE: BOTH FILLED
//        if (hasDropdown && hasManual) {
//            model.addAttribute("error", "Select OR enter category, not both.");
//            model.addAttribute("categories", categoryRepository.findAll());
//            return "seller/add-product";
//        }
//
//        // CASE: NONE FILLED
//        if (!hasDropdown && !hasManual) {
//            model.addAttribute("error", "Please select or enter a category.");
//            model.addAttribute("categories", categoryRepository.findAll());
//            return "seller/add-product";
//        }
//
//        // CASE: MANUAL CATEGORY ENTERED
//        if (hasManual) {
//
//            Category category = categoryRepository
//                    .findByCategoryNameIgnoreCase(newCategoryName.trim())
//                    .orElseGet(() -> {
//                        Category newCategory = new Category();
//                        newCategory.setCategoryName(newCategoryName.trim());
//                        newCategory.setDescription("Added by seller");
//                        return categoryRepository.save(newCategory);
//                    });
//
//            product.setCategory(category);
//        }
//
//        product.setSeller(seller);
//        product.setCreatedAt(LocalDateTime.now());
//        product.setIsActive(1);
//
//        productService.saveProduct(product, seller);
//
//        return "redirect:/seller/my-products";
//    }
//
//    // VIEW SELLER'S PRODUCTS
//    @GetMapping("/my-products")
//    public String viewProducts(Model model,
//                               Authentication authentication){
//
//        String email = authentication.getName();
//        User seller = userService.findByEmail(email);
//
//        model.addAttribute("products",
//                productService.getProductBySeller(seller));
//
//        return "seller/my-products";
//    }



    @GetMapping("/profile")
    public String showProfile(Model model, Authentication authentication) {

        String email = authentication.getName();  // logged-in email

        User user = userService.findByEmail(email);

        SellerDetails details = sellerService.getSellerDetails(user.getUserId());

        if (details == null) {
            details = new SellerDetails();
        }

        model.addAttribute("sellerDetails", details);

        return "seller/seller-profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute SellerDetails details,
                                Authentication authentication) {

        String email = authentication.getName(); // logged-in user email

        User user = userService.findByEmail(email);

        sellerService.saveOrUpdateSeller(user, details);

        return "redirect:/seller/profile?success";
    }

    // SAVE PRODUCTS
    @PostMapping("/save-product")
    public String saveProduct(@ModelAttribute("product") Product product,
                              @RequestParam(value = "newCategoryName", required = false) String newCategoryName,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              Authentication authentication,
                              Model model){

        System.out.println("INSIDE SAVE PRODUCT METHOD");
        String email = authentication.getName();
        User seller = userService.findByEmail(email);

        Category selectedCategory = product.getCategory();

        boolean hasDropdown = selectedCategory != null && selectedCategory.getCategoryId() != null;
        boolean hasManual = newCategoryName != null && !newCategoryName.trim().isEmpty();

        System.out.println("Selected Category: " + selectedCategory);
        System.out.println("Category ID: " +
                (selectedCategory != null ? selectedCategory.getCategoryId() : "NULL"));

        System.out.println("New Category Name: " + newCategoryName);

        System.out.println("hasDropdown: " + hasDropdown);
        System.out.println("hasManual: " + hasManual);

        // ❌ BOTH FILLED
        if (hasDropdown && hasManual) {
            model.addAttribute("error", "Select OR enter category, not both.");
            model.addAttribute("categories", categoryRepository.findAll());
            System.out.println("Categories: " + categoryRepository.findAll().size());
            return "seller/add-product";
        }

        // ❌ NONE FILLED
        if (!hasDropdown && !hasManual) {
            model.addAttribute("error", "Please select or enter a category.");
            model.addAttribute("categories", categoryRepository.findAll());
            return "seller/add-product";
        }

        // ✅ MANUAL CATEGORY
        if (hasManual) {

            Category category = categoryRepository
                    .findByCategoryNameIgnoreCase(newCategoryName.trim())
                    .orElseGet(() -> {
                        Category newCategory = new Category();
                        newCategory.setCategoryName(newCategoryName.trim());
                        newCategory.setDescription("Added by seller");
                        return categoryRepository.save(newCategory);
                    });

            product.setCategory(category);
        }

        // ✅ IMAGE SAVE (if provided)
        if (imageFile != null && !imageFile.isEmpty()) {

            String uploadDir = "src/main/resources/static/product-images/";
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();

            try {
                Path path = Paths.get(uploadDir + fileName);
                Files.createDirectories(path.getParent());
                Files.write(path, imageFile.getBytes());

                product.setImageName(fileName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        product.setSeller(seller);
        product.setCreatedAt(LocalDateTime.now());
        product.setIsActive(1);

        productService.saveProduct(product, seller);

        return "redirect:/seller/my-products";
    }


    // VIEW SELLER'S PRODUCTS
    @GetMapping("/my-products")
    public String viewProducts(Model model,
                               Authentication authentication){

        String email = authentication.getName();
        User seller = userService.findByEmail(email);

        model.addAttribute("products",
                productService.getProductBySeller(seller));

        return "seller/my-products";
    }
    // Show edit page
    @GetMapping("/edit-product/{id}")
    public String showEditProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/seller/my-products?error";
        }
        model.addAttribute("product", product);
        return "seller/edit-product";
    }



    // Update product (from modal form)
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id,
                                @RequestParam String productName,
                                @RequestParam String description,
                                @RequestParam String manufacturer,
                                @RequestParam Double mrp,
                                @RequestParam Double sellingPrice,
                                @RequestParam Integer stock,
                                @RequestParam Integer stockThreshold) {

        // fetch existing product
        Product product = productService.getProductById(id);

        // update only editable fields
        product.setProductName(productName);
        product.setDescription(description);
        product.setManufacturer(manufacturer);
        product.setMrp(mrp);
        product.setSellingPrice(sellingPrice);
        product.setStock(stock);
        product.setStockThreshold(stockThreshold);

        productService.saveOrUpdateProduct(product);
        return "redirect:/seller/my-products";
    }


    @GetMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/seller/my-products?deleted";
    }


    // List unread notifications
    @GetMapping("/notifications")
    public String viewNotifications(Model model, Authentication authentication) {
        User seller = userService.findByEmail(authentication.getName());
        model.addAttribute("notifications", notificationService.getUnreadNotifications(seller));
        return "seller/notifications";
    }

    // Mark notification as read and redirect to orders page
    @GetMapping("/notifications/read/{id}")
    public String markNotificationRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/seller/orders";
    }

    @ModelAttribute
    public void addNotificationsToModel(Model model, Authentication authentication) {
        if(authentication != null) {
            User seller = userService.findByEmail(authentication.getName());
            var notifications = notificationService.getUnreadNotifications(seller);
            model.addAttribute("notifications", notifications);
            model.addAttribute("unreadCount", notifications.size());
        }
    }
}
