package com.revshop.config;

import com.revshop.repo.CategoryRepository;
import com.revshop.repo.SecurityQuestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataInitializerTest {

    @Mock private SecurityQuestionRepository questionRepo;
    @Mock private CategoryRepository categoryRepo;

    @InjectMocks private DataInitializer dataInitializer;

    @Test
    void testInitQuestionsWhenEmpty() throws Exception {
        // Setup: Repo says it has 0 items
        when(questionRepo.count()).thenReturn(0L);

        // Action
        CommandLineRunner runner = dataInitializer.initQuestions(questionRepo);
        runner.run();

        // Check: Verify save was called at least once
        verify(questionRepo, atLeastOnce()).save(any());
    }

    @Test
    void testInitCategoriesWhenNew() throws Exception {
        // Setup: Repo says category does not exist
        when(categoryRepo.existsByCategoryName(anyString())).thenReturn(false);

        // Action
        CommandLineRunner runner = dataInitializer.initCategories(categoryRepo);
        runner.run();

        // Check: Verify categories are saved
        verify(categoryRepo, atLeastOnce()).save(any());
    }
}