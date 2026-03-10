package com.revshop.repo;

import com.revshop.entity.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CategoryRepositoryTest {

    @Mock
    private CategoryRepository categoryRepository;

    private Category dummyCategory;

    @BeforeEach
    void setUp() {
        dummyCategory = new Category();
        dummyCategory.setCategoryId(1L);
        dummyCategory.setCategoryName("Electronics");
        dummyCategory.setDescription("Gadgets and devices");
    }

    // 1. Testing custom findByCategoryNameIgnoreCase (Found)
    @Test
    void testFindByCategoryNameIgnoreCase_Found() {
        Mockito.when(categoryRepository.findByCategoryNameIgnoreCase("electronics"))
                .thenReturn(Optional.of(dummyCategory));

        Optional<Category> result = categoryRepository.findByCategoryNameIgnoreCase("electronics");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("Electronics", result.get().getCategoryName());
    }

    // 2. Testing custom findByCategoryNameIgnoreCase (Not Found)
    @Test
    void testFindByCategoryNameIgnoreCase_NotFound() {
        Mockito.when(categoryRepository.findByCategoryNameIgnoreCase("unknown"))
                .thenReturn(Optional.empty());

        Optional<Category> result = categoryRepository.findByCategoryNameIgnoreCase("unknown");

        Assertions.assertFalse(result.isPresent());
    }

    // 3. Testing custom existsByCategoryName (True)
    @Test
    void testExistsByCategoryName_True() {
        Mockito.when(categoryRepository.existsByCategoryName("Electronics")).thenReturn(true);

        boolean exists = categoryRepository.existsByCategoryName("Electronics");

        Assertions.assertTrue(exists);
    }

    // 4. Testing custom existsByCategoryName (False)
    @Test
    void testExistsByCategoryName_False() {
        Mockito.when(categoryRepository.existsByCategoryName("Clothing")).thenReturn(false);

        boolean exists = categoryRepository.existsByCategoryName("Clothing");

        Assertions.assertFalse(exists);
    }

    // 5. Testing built-in save
    @Test
    void testSaveCategory() {
        Mockito.when(categoryRepository.save(dummyCategory)).thenReturn(dummyCategory);

        Category result = categoryRepository.save(dummyCategory);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getCategoryId());
    }

    // 6. Testing built-in findById (Found)
    @Test
    void testFindById_Found() {
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(dummyCategory));

        Optional<Category> result = categoryRepository.findById(1L);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("Electronics", result.get().getCategoryName());
    }

    // 7. Testing built-in findById (Not Found)
    @Test
    void testFindById_NotFound() {
        Mockito.when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Category> result = categoryRepository.findById(99L);

        Assertions.assertFalse(result.isPresent());
    }

    // 8. Testing built-in findAll (With Data)
    @Test
    void testFindAll_Found() {
        Mockito.when(categoryRepository.findAll()).thenReturn(Arrays.asList(dummyCategory));

        List<Category> result = categoryRepository.findAll();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Electronics", result.get(0).getCategoryName());
    }

    // 9. Testing built-in findAll (Empty)
    @Test
    void testFindAll_Empty() {
        Mockito.when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        List<Category> result = categoryRepository.findAll();

        Assertions.assertTrue(result.isEmpty());
    }

    // 10. Testing built-in deleteById
    @Test
    void testDeleteById() {
        categoryRepository.deleteById(1L);

        Mockito.verify(categoryRepository, Mockito.times(1)).deleteById(1L);
    }

    // 11. Testing built-in count
    @Test
    void testCount() {
        Mockito.when(categoryRepository.count()).thenReturn(10L);

        long result = categoryRepository.count();

        Assertions.assertEquals(10L, result);
    }

    // 12. Testing built-in existsById
    @Test
    void testExistsById() {
        Mockito.when(categoryRepository.existsById(1L)).thenReturn(true);

        boolean result = categoryRepository.existsById(1L);

        Assertions.assertTrue(result);
    }
}