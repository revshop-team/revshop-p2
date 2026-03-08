package com.revshop.repo;

import com.revshop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * @param categoryName
     * @return Optional<Category>
     */
    Optional<Category> findByCategoryNameIgnoreCase(String categoryName);
    boolean existsByCategoryName(String categoryName);
}
