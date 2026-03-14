package com.revshop.config;

import com.revshop.entity.SecurityQuestion;
import com.revshop.entity.Category;
import com.revshop.repo.SecurityQuestionRepository;
import com.revshop.repo.CategoryRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    /**
     * Add default security questions if table is empty
     */
    @Bean
    CommandLineRunner initQuestions(SecurityQuestionRepository repository) {
        return args -> {
            if (repository.count() == 0) {

                SecurityQuestion q1 = new SecurityQuestion();
                q1.setQuestionText("What is your first pet name?");
                repository.save(q1);

                SecurityQuestion q2 = new SecurityQuestion();
                q2.setQuestionText("What is your first school name?");
                repository.save(q2);

                SecurityQuestion q3 = new SecurityQuestion();
                q3.setQuestionText("In which city were you born?");
                repository.save(q3);

                System.out.println("✅ Default Security Questions Inserted");
            }
        };
    }

    /**
     * Add default categories if table is empty
     */
//    @Bean
//    CommandLineRunner initCategories(CategoryRepository categoryRepository) {
//        return args -> {
//
//            if (categoryRepository.count() == 0) {
//
//                categoryRepository.save(
//                        Category.builder()
//                                .categoryName("Electronics")
//                                .description("Electronic Items")
//                                .build()
//                );
//
//                categoryRepository.save(
//                        Category.builder()
//                                .categoryName("Fashion")
//                                .description("Clothing & Accessories")
//                                .build()
//                );
//
//                categoryRepository.save(
//                        Category.builder()
//                                .categoryName("Books")
//                                .description("Books & Study")
//                                .build()
//                );
//
//                categoryRepository.save(
//                        Category.builder()
//                                .categoryName("Home & Kitchen")
//                                .description("Home Products")
//                                .build()
//                );
//
//                categoryRepository.save(
//                        Category.builder()
//                                .categoryName("Groceries")
//                                .description("Daily Essentials")
//                                .build()
//                );
//
//                System.out.println("✅ Default Categories Inserted");
//            }
//        };
//    }

    @Bean
    CommandLineRunner initCategories(CategoryRepository categoryRepository) {
        return args -> {

            insertCategoryIfNotExists(categoryRepository, "Electronics", "TV, Audio, Cameras, Accessories");

            insertCategoryIfNotExists(categoryRepository, "Mobiles", "Smartphones & Mobile Accessories");

            insertCategoryIfNotExists(categoryRepository, "Laptops", "Laptops, Computers & Accessories");

            insertCategoryIfNotExists(categoryRepository, "Fashion", "Men, Women & Kids Clothing");

            insertCategoryIfNotExists(categoryRepository, "Footwear", "Shoes, Sandals & Sneakers");

            insertCategoryIfNotExists(categoryRepository, "Watches", "Smart Watches & Analog Watches");

            insertCategoryIfNotExists(categoryRepository, "Home & Kitchen", "Home Appliances & Kitchen Items");

            insertCategoryIfNotExists(categoryRepository, "Furniture", "Sofas, Beds, Tables & Chairs");

            insertCategoryIfNotExists(categoryRepository, "Books", "Educational & Story Books");

            insertCategoryIfNotExists(categoryRepository, "Groceries", "Daily Needs & Food Items");

            insertCategoryIfNotExists(categoryRepository, "Beauty", "Cosmetics & Personal Care");

            insertCategoryIfNotExists(categoryRepository, "Sports", "Sports & Fitness Equipment");

            insertCategoryIfNotExists(categoryRepository, "Toys", "Kids Toys & Games");

            insertCategoryIfNotExists(categoryRepository, "Automotive", "Bike & Car Accessories");

            insertCategoryIfNotExists(categoryRepository, "Stationery", "Office & School Supplies");

            insertCategoryIfNotExists(categoryRepository, "Health", "Health Care & Medical Items");

            insertCategoryIfNotExists(categoryRepository, "Jewellery", "Gold, Silver & Artificial Jewellery");

            System.out.println("✅ Default Categories Inserted");
        };
    }
    private void insertCategoryIfNotExists(CategoryRepository repo, String name, String desc) {

        if (!repo.existsByCategoryName(name)) {

            Category category = new Category();
            category.setCategoryName(name);
            category.setDescription(desc);

            repo.save(category);
        }
    }
}