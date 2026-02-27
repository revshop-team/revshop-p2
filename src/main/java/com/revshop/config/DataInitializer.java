package com.revshop.config;

import com.revshop.entity.SecurityQuestion;
import com.revshop.repo.SecurityQuestionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    /**
     *
     * @param repository
     * @return add the security questions to DB if security questions table is EMPTY
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
            }
        };
    }
}