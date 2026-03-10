package com.revshop.serviceImpl;

import com.revshop.entity.Category;
import com.revshop.repo.CategoryRepository;
import com.revshop.serviceInterfaces.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);


    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    @Override
    public List<Category> getAllCategories() {

        logger.info("Fetching all categories");

        List<Category> categories = categoryRepository.findAll();

        logger.debug("Total categories found: {}", categories.size());

        return categories;
    }
}
