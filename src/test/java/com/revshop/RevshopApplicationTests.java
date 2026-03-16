package com.revshop;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class RevshopApplicationTests {

    @Test
    void testMainMethod() {
        // 1. We mock the SpringApplication class so it DOES NOT start the server
        try (MockedStatic<SpringApplication> mockedSpringApp = Mockito.mockStatic(SpringApplication.class)) {

            // 2. Tell Mockito to just return a fake context if it sees "SpringApplication.run"
            mockedSpringApp.when(() -> SpringApplication.run(eq(RevshopApplication.class), any(String[].class)))
                    .thenReturn(Mockito.mock(ConfigurableApplicationContext.class));

            // 3. Call your main method!
            RevshopApplication.main(new String[]{});

            // 4. Verify that your main method successfully executed the run command
            mockedSpringApp.verify(() -> SpringApplication.run(eq(RevshopApplication.class), any(String[].class)));
        }
    }
}