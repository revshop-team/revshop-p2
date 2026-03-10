package com.revshop.repo;

import com.revshop.entity.SecurityQuestion;
import com.revshop.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User dummyUser;

    @BeforeEach
    void setUp() {
        SecurityQuestion sq = new SecurityQuestion();
        sq.setQuestionId(1L);

        dummyUser = new User();
        dummyUser.setUserId(1L);
        dummyUser.setEmail("matloob@test.com");
        dummyUser.setPassword("securepass123");
        dummyUser.setRole("BUYER");
        dummyUser.setSecurityQuestion(sq);
        dummyUser.setSecurityAnswer("Fluffy");
        dummyUser.setCreatedAt(LocalDateTime.now());
    }

    // 1. Testing custom findByEmail (Found)
    @Test
    void testFindByEmail_Found() {
        Mockito.when(userRepository.findByEmail("matloob@test.com")).thenReturn(Optional.of(dummyUser));

        Optional<User> result = userRepository.findByEmail("matloob@test.com");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("BUYER", result.get().getRole());
    }

    // 2. Testing custom findByEmail (Not Found)
    @Test
    void testFindByEmail_NotFound() {
        Mockito.when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        Optional<User> result = userRepository.findByEmail("unknown@test.com");

        Assertions.assertFalse(result.isPresent());
    }

    // 3. Testing built-in save method
    @Test
    void testSaveUser() {
        Mockito.when(userRepository.save(dummyUser)).thenReturn(dummyUser);

        User savedUser = userRepository.save(dummyUser);

        Assertions.assertNotNull(savedUser);
        Assertions.assertEquals(1L, savedUser.getUserId());
    }

    // 4. Testing built-in findById (Found)
    @Test
    void testFindById_Found() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(dummyUser));

        Optional<User> result = userRepository.findById(1L);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("matloob@test.com", result.get().getEmail());
    }

    // 5. Testing built-in findById (Not Found)
    @Test
    void testFindById_NotFound() {
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<User> result = userRepository.findById(99L);

        Assertions.assertFalse(result.isPresent());
    }

    // 6. Testing built-in findAll (With Data)
    @Test
    void testFindAll_Found() {
        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(dummyUser));

        List<User> result = userRepository.findAll();

        Assertions.assertEquals(1, result.size());
    }

    // 7. Testing built-in findAll (Empty)
    @Test
    void testFindAll_Empty() {
        Mockito.when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<User> result = userRepository.findAll();

        Assertions.assertTrue(result.isEmpty());
    }

    // 8. Testing built-in deleteById method
    @Test
    void testDeleteById() {
        userRepository.deleteById(1L);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
    }

    // 9. Testing built-in delete method (passing the object)
    @Test
    void testDelete() {
        userRepository.delete(dummyUser);

        Mockito.verify(userRepository, Mockito.times(1)).delete(dummyUser);
    }

    // 10. Testing built-in count method
    @Test
    void testCount() {
        Mockito.when(userRepository.count()).thenReturn(50L);

        long result = userRepository.count();

        Assertions.assertEquals(50L, result);
    }

    // 11. Testing built-in existsById (True)
    @Test
    void testExistsById_True() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);

        boolean exists = userRepository.existsById(1L);

        Assertions.assertTrue(exists);
    }

    // 12. Testing built-in existsById (False)
    @Test
    void testExistsById_False() {
        Mockito.when(userRepository.existsById(99L)).thenReturn(false);

        boolean exists = userRepository.existsById(99L);

        Assertions.assertFalse(exists);
    }
}