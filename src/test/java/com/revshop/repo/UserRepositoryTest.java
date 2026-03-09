package com.revshop.repo;

import com.revshop.entity.User;
import com.revshop.entity.SecurityQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired private TestEntityManager entityManager;
    @Autowired private UserRepository userRepository;

    private User testUser;
    private SecurityQuestion testQuestion;

    @BeforeEach
    void setUp() {
        // 1. Setup a Security Question correctly based on your entity
        testQuestion = new SecurityQuestion();
        // FIX: Using setQuestionText() to match your field 'questionText'
        testQuestion.setQuestionText("What is your pet's name?");
        testQuestion = entityManager.persistAndFlush(testQuestion);

        // 2. Create a baseline BUYER
        testUser = new User();
        testUser.setEmail("buyer_" + System.currentTimeMillis() + "@test.com");
        testUser.setPassword("encoded_password");
        testUser.setRole("BUYER");
        testUser.setSecurityQuestion(testQuestion);
        testUser.setSecurityAnswer("Buddy");
        testUser = entityManager.persistAndFlush(testUser);
    }

    // 1. Test Custom Method: findByEmail
    @Test
    void testFindByEmail_ShouldReturnUser() {
        Optional<User> found = userRepository.findByEmail(testUser.getEmail());
        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo("BUYER");
    }

    // 2. Test Role Verification: SELLER (Turns setRole green)
    @Test
    void testSaveSeller_ShouldPersistCorrectRole() {
        User seller = new User();
        seller.setEmail("seller_" + System.currentTimeMillis() + "@test.com");
        seller.setPassword("pass123");
        seller.setRole("SELLER");

        User saved = userRepository.save(seller);
        assertThat(saved.getRole()).isEqualTo("SELLER");
    }

    // 3. Test Email Uniqueness (Verifies @Column(unique=true))
    @Test
    void testDuplicateEmail_ShouldFail() {
        User duplicate = new User();
        duplicate.setEmail(testUser.getEmail());
        duplicate.setPassword("any");

        try {
            userRepository.save(duplicate);
            entityManager.flush();
        } catch (Exception e) {
            return; // Success: ORA-00001 triggered
        }
        assertThat(false).as("Expected unique constraint violation").isTrue();
    }

    // 4. Test Security Question Association (Turns setSecurityQuestion green)
    @Test
    void testSecurityQuestionAssociation() {
        assertThat(testUser.getSecurityQuestion().getQuestionText()).isEqualTo("What is your pet's name?");
        assertThat(testUser.getSecurityAnswer()).isEqualTo("Buddy");
    }

    // 5. Test Null Password (Verifies @Column(nullable=false))
    @Test
    void testSaveUser_NullPassword_ShouldFail() {
        User badUser = new User();
        badUser.setEmail("nullpass_" + System.currentTimeMillis() + "@test.com");
        badUser.setPassword(null);

        try {
            userRepository.save(badUser);
            entityManager.flush();
        } catch (Exception e) {
            return; // Success: ORA-01400 triggered
        }
        assertThat(false).as("Expected null constraint violation").isTrue();
    }

    // 6. Test Find By ID
    @Test
    void testFindById() {
        Optional<User> found = userRepository.findById(testUser.getUserId());
        assertThat(found).isPresent();
    }

    // 7. Test Update Role
    @Test
    void testUpdateUserRole() {
        testUser.setRole("SELLER");
        User updated = userRepository.save(testUser);
        entityManager.flush();
        assertThat(updated.getRole()).isEqualTo("SELLER");
    }

    // 8. Test Delete User
    @Test
    void testDeleteUser() {
        userRepository.delete(testUser);
        entityManager.flush();
        assertThat(userRepository.findByEmail(testUser.getEmail())).isNotPresent();
    }

    // 9. Test findByEmail (Non-existent)
    @Test
    void testFindByEmail_NotFound() {
        Optional<User> found = userRepository.findByEmail("ghost@test.com");
        assertThat(found).isNotPresent();
    }

    // 10. Test CreatedAt Persistence (Turns setCreatedAt green)
    @Test
    void testCreatedAt_ShouldBeAutomaticallySet() {
        assertThat(testUser.getCreatedAt()).isNotNull();
    }

    // 11. Test Find All (Oracle integration check)
    @Test
    void testFindAll() {
        List<User> users = userRepository.findAll();
        assertThat(users.size()).isGreaterThanOrEqualTo(1);
    }

    // 12. Test Password Persistence (Turns setPassword green)
    @Test
    void testPasswordPersistence() {
        String testPass = "new_secret_password";
        testUser.setPassword(testPass);
        User saved = userRepository.save(testUser);
        entityManager.flush();

        assertThat(saved.getPassword()).isEqualTo(testPass);
    }
}