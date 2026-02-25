package com.revshop.serviceImpl;

import com.revshop.entity.Category;
import com.revshop.repo.CategoryRepository;
import com.revshop.serviceInterfaces.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    @Override
    public List<Category> getAllCategories() {
        return List.of();
    }
}
