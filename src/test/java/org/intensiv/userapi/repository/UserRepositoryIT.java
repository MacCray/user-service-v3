package org.intensiv.userapi.repository;

import org.intensiv.userapi.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
public class UserRepositoryIT {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> pgContainer = new PostgreSQLContainer<>("postgres:16.3")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");
    @Autowired
    private UserRepository userRepository;
    private User validUser;

    @BeforeEach
    void init() {
        validUser = new User("Roman", "email@gmail.com", 26);
    }

    @Test
    @DisplayName("Should persist user when saving a valid user")
    void save_withValidUser_shouldPersistUser() {
        userRepository.save(validUser);

        Optional<User> found = userRepository.findById(validUser.getId());
        assertTrue(found.isPresent());
        assertEquals(validUser.getName(), found.get().getName());
        assertEquals(validUser.getEmail(), found.get().getEmail());
        assertEquals(validUser.getAge(), found.get().getAge());
    }

    @Test
    @DisplayName("Should throw exception when saving user with duplicate email")
    void save_withDuplicateEmail_shouldThrow() {
        User user = new User("Ivan", validUser.getEmail(), 20);
        userRepository.save(validUser);

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(user));
    }

    @Test
    @DisplayName("Should return user when user exists by ID")
    void findById_whenUserExists_shouldReturnUser() {
        userRepository.save(validUser);
        Long id = validUser.getId();

        Optional<User> found = userRepository.findById(id);
        assertTrue(found.isPresent());
        assertEquals(validUser.getName(), found.get().getName());
        assertEquals(validUser.getEmail(), found.get().getEmail());
        assertEquals(validUser.getAge(), found.get().getAge());
    }

    @Test
    @DisplayName("Should return empty when user does not exist by ID")
    void findById_whenUserDoesNotExists_shouldReturnNull() {
        Optional<User> found = userRepository.findById(1L);

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should return all users when users exist")
    void findAll_whenUsersExist_shouldReturnAllUsers() {
        User user = new User("Ivan", "my@mail.com", 20);
        User user2 = new User("Oleg", "oleg@gmail.com", 25);
        userRepository.save(user);
        userRepository.save(user2);
        userRepository.save(validUser);

        List<User> users = userRepository.findAll();

        assertEquals(3, users.size());
        List<String> names = users.stream().map(User::getName).toList();
        assertTrue(names.contains("Ivan"));
        assertTrue(names.contains("Oleg"));
        assertTrue(names.contains("Roman"));
        List<String> emails = users.stream().map(User::getEmail).toList();
        assertTrue(emails.contains("email@gmail.com"));
        assertTrue(emails.contains("my@mail.com"));
        assertTrue(emails.contains("oleg@gmail.com"));
        List<Integer> ages = users.stream().map(User::getAge).toList();
        assertTrue(ages.contains(20));
        assertTrue(ages.contains(25));
        assertTrue(ages.contains(26));
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void findAll_WhenNoUsersExist_ShouldReturnEmptyList() {
        List<User> users = userRepository.findAll();

        assertTrue(users.isEmpty());
    }

    @Test
    @DisplayName("Should update user when updating with valid data")
    void update_withValidUser_shouldUpdateUser() {
        userRepository.save(validUser);
        Long userId = validUser.getId();

        validUser.setEmail("NewEmail@gmail.com");
        validUser.setAge(27);
        userRepository.save(validUser);

        Optional<User> updated = userRepository.findById(userId);
        assertEquals("NewEmail@gmail.com", updated.get().getEmail());
        assertEquals(27, updated.get().getAge());
    }

    @Test
    @DisplayName("Should throw exception when updating user with duplicate email")
    void update_withDuplicateEmail_shouldThrow() {
        User user = new User("Ivan", "my@mail.com", 20);
        userRepository.save(validUser);
        userRepository.save(user);
        validUser.setEmail("my@mail.com");
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(validUser));
    }

    @Test
    @DisplayName("Should delete user when deleting a valid user")
    void delete_withValidUser_shouldDeleteUser() {
        userRepository.save(validUser);
        Long userId = validUser.getId();

        userRepository.delete(validUser);

        Optional<User> deleted = userRepository.findById(userId);
        assertTrue(deleted.isEmpty());
    }
}
