package com.revshop.repo;

import com.revshop.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Ensure "Electronics" exists for search tests, but don't crash if it's already there
        Optional<Category> existing = categoryRepository.findByCategoryNameIgnoreCase("Electronics");

        if (existing.isPresent()) {
            testCategory = existing.get();
        } else {
            testCategory = new Category();
            testCategory.setCategoryName("Electronics");
            testCategory.setDescription("Devices and Gadgets");
            testCategory = entityManager.persistAndFlush(testCategory);
        }
    }

    // 1. Test Saving a Category (Uses a unique name to avoid ORA-00001)
    @Test
    void testSaveCategory_ShouldPersistData() {
        Category category = new Category();
        category.setCategoryName("Fashion_" + System.currentTimeMillis());
        category.setDescription("Clothing and Accessories");

        Category saved = categoryRepository.save(category);

        assertThat(saved.getCategoryId()).isNotNull();
    }

    // 2. Test Custom Method: findByCategoryNameIgnoreCase
    @Test
    void testFindByCategoryNameIgnoreCase_ShouldReturnCategory() {
        Optional<Category> found = categoryRepository.findByCategoryNameIgnoreCase("electronics");

        assertThat(found).isPresent();
        assertThat(found.get().getCategoryName()).isEqualTo("Electronics");
    }

    // 3. Test Custom Method: existsByCategoryName
    @Test
    void testExistsByCategoryName_ShouldReturnTrue() {
        boolean exists = categoryRepository.existsByCategoryName("Electronics");
        assertThat(exists).isTrue();
    }

    // 4. Test Find All
    @Test
    void testFindAll_ShouldReturnAtLeastOne() {
        List<Category> all = categoryRepository.findAll();
        assertThat(all.size()).isGreaterThanOrEqualTo(1);
    }

    // 5. Test Update Category
    @Test
    void testUpdateCategory_ShouldChangeDescription() {
        // We update a unique temporary category to be safe
        Category tempCat = new Category();
        tempCat.setCategoryName("UpdateTest_" + System.currentTimeMillis());
        tempCat = entityManager.persistAndFlush(tempCat);

        tempCat.setDescription("New Desc");
        Category updated = categoryRepository.save(tempCat);
        entityManager.flush();

        assertThat(updated.getDescription()).isEqualTo("New Desc");
    }

    // 6. Test Delete Category (FIXES ORA-02292)
    @Test
    void testDeleteCategory_ShouldRemoveRecord() {
        // Create a BRAND NEW category that we know has ZERO products attached
        Category emptyCat = new Category();
        emptyCat.setCategoryName("DeleteMe_" + System.currentTimeMillis());
        emptyCat = entityManager.persistAndFlush(emptyCat);
        Long id = emptyCat.getCategoryId();

        categoryRepository.delete(emptyCat);
        entityManager.flush();

        Optional<Category> found = categoryRepository.findById(id);
        assertThat(found).isNotPresent();
    }

    // 7. Test Unique Constraint
    @Test
    void testUniqueCategoryName_ShouldPreventDuplicates() {
        Category duplicate = new Category();
        duplicate.setCategoryName("Electronics");

        try {
            categoryRepository.save(duplicate);
            entityManager.flush();
        } catch (Exception e) {
            // Success: Oracle blocked the duplicate
            return;
        }
        assertThat(false).as("Expected unique constraint violation but none occurred").isTrue();
    }

    // 8. Test Find By ID
    @Test
    void testFindById() {
        Optional<Category> found = categoryRepository.findById(testCategory.getCategoryId());
        assertThat(found).isPresent();
    }

    // 9. Test Exists By Name (False Case)
    @Test
    void testExistsByCategoryName_False() {
        boolean exists = categoryRepository.existsByCategoryName("Unknown_Category_123");
        assertThat(exists).isFalse();
    }

    // 10. Test Null Name Failure
    @Test
    void testSaveCategory_NullName_ShouldFail() {
        Category badCat = new Category();
        badCat.setCategoryName(null);

        try {
            categoryRepository.save(badCat);
            entityManager.flush();
        } catch (Exception e) {
            return;
        }
        assertThat(false).as("Expected null constraint violation").isTrue();
    }

    // 11. Test Count
    @Test
    void testCount() {
        assertThat(categoryRepository.count()).isGreaterThanOrEqualTo(1L);
    }

    // 12. Test Description Length (Boundary Test)
    @Test
    void testSaveCategory_LongDescription() {
        Category longDescCat = new Category();
        longDescCat.setCategoryName("LongDesc_" + System.currentTimeMillis());
        longDescCat.setDescription("A".repeat(255)); // Max length from your entity

        Category saved = categoryRepository.save(longDescCat);
        assertThat(saved.getDescription().length()).isEqualTo(255);
    }
}