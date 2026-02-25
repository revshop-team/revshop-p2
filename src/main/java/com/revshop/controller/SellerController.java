package com.revshop.controller;

import com.revshop.entity.Category;
import com.revshop.entity.Product;
import com.revshop.entity.User;
import com.revshop.repo.CategoryRepository;
import com.revshop.serviceInterfaces.ProductService;
import com.revshop.serviceInterfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/seller")
public class SellerController {

    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;

    @Autowired
    private CategoryRepository categoryRepository;

    public SellerController(ProductService productService,
                            UserService userService,
                            CategoryRepository categoryRepository) {

        this.productService = productService;
        this.userService = userService;
        this.categoryRepository = categoryRepository;
    }

    // render seller's dashboard
    @GetMapping("/dashboard")
    public String sellerDashboard() {
        return "seller/dashboard";
    }

    // seller products
    @GetMapping("/products")
    public String sellerProducts() {
        return "seller/products";
    }

    // seller's sales
    @GetMapping("/sales")
    public String sellerSales() {
        return "seller/sales";
    }

    // Show Add Product Page
    @GetMapping("/add-product")
    public String showAddProductForm(Model model) {

        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());

        return "seller/add-product";
    }

    // save products
    @PostMapping("/save-product")
    public String saveProduct(@ModelAttribute("product") Product product,
                              @RequestParam(value = "newCategoryName", required = false) String newCategoryName,
                              Authentication authentication,
                              Model model){

        String email = authentication.getName();
        User seller = userService.findByEmail(email);

        Category selectedCategory = product.getCategory();

        boolean hasDropdown = selectedCategory != null && selectedCategory.getCategoryId() != null;
        boolean hasManual = newCategoryName != null && !newCategoryName.trim().isEmpty();

        // Case: Both filled
        if (hasDropdown && hasManual) {
            model.addAttribute("error", "Select OR enter category, not both.");
            model.addAttribute("categories", categoryRepository.findAll());
            return "seller/add-product";
        }

        // Case: None filled
        if (!hasDropdown && !hasManual) {
            model.addAttribute("error", "Please select or enter a category.");
            model.addAttribute("categories", categoryRepository.findAll());
            return "seller/add-product";
        }

        // Case: Manual category entered
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

        product.setSeller(seller);
        product.setCreatedAt(LocalDateTime.now());
        product.setIsActive(1);

        productService.saveProduct(product, seller);

        return "redirect:/seller/my-products";
    }

    // view seller's products
    @GetMapping("/my-products")
    public String viewProducts(Model model,
                               Authentication authentication){

        String email = authentication.getName();
        User seller = userService.findByEmail(email);

        model.addAttribute("products",
                productService.getProductBySeller(seller));

        return "seller/my-products";
    }
}
