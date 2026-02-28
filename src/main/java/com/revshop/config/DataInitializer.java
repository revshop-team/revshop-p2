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

            if (categoryRepository.count() == 0) {

                Category c1 = new Category();
                c1.setCategoryName("Electronics");
                c1.setDescription("Electronic Items");
                categoryRepository.save(c1);

                Category c2 = new Category();
                c2.setCategoryName("Fashion");
                c2.setDescription("Clothing & Accessories");
                categoryRepository.save(c2);

                Category c3 = new Category();
                c3.setCategoryName("Books");
                c3.setDescription("Books & Study");
                categoryRepository.save(c3);

                Category c4 = new Category();
                c4.setCategoryName("Home & Kitchen");
                c4.setDescription("Home Products");
                categoryRepository.save(c4);

                Category c5 = new Category();
                c5.setCategoryName("Groceries");
                c5.setDescription("Daily Essentials");
                categoryRepository.save(c5);

                System.out.println("✅ Default Categories Inserted");
            }
        };
    }
}