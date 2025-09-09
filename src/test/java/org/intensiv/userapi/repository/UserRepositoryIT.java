package org.intensiv.userapi.repository;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class UserRepositoryIT {
    @Container
    public static final PostgreSQLContainer<?> pgContainer = new PostgreSQLContainer<>("postgres:16.3")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");
    private UserDAO userDAO;
    private User validUser;

    @BeforeAll
    static void setUpDatabase() throws Exception {
        try (Connection connection = DriverManager.getConnection(
                pgContainer.getJdbcUrl(),
                pgContainer.getUsername(),
                pgContainer.getPassword())) {

            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            );

            liquibase.update(new Contexts(), new LabelExpression());
        }

        System.setProperty("hibernate.connection.url", pgContainer.getJdbcUrl());
        System.setProperty("hibernate.connection.username", pgContainer.getUsername());
        System.setProperty("hibernate.connection.password", pgContainer.getPassword());
    }

    @BeforeEach
    void init() {
        userDAO = new UserDAOImpl(HibernateUtil.getSessionFactory());

        HibernateUtil.getSessionFactory().inTransaction(session ->
                session.createMutationQuery("DELETE FROM User").executeUpdate()
        );
        validUser = new User("Roman", "email@gmail.com", 26);
    }

    @Test
    void save_withValidUser_shouldPersistUser() {
        userDAO.save(validUser);

        User found = userDAO.findById(validUser.getId());
        assertNotNull(found);
        assertEquals(validUser.getName(), found.getName());
        assertEquals(validUser.getEmail(), found.getEmail());
        assertEquals(validUser.getAge(), found.getAge());
    }

    @Test
    void save_withDuplicateEmail_shouldThrow() {
        User user = new User("Ivan", validUser.getEmail(), 20);
        userDAO.save(validUser);

        assertThrows(RuntimeException.class, () -> userDAO.save(user));
    }

    @Test
    void findById_whenUserExists_shouldReturnUser() {
        userDAO.save(validUser);
        Long id = validUser.getId();

        User found = userDAO.findById(id);
        assertNotNull(found);
        assertEquals(validUser.getName(), found.getName());
        assertEquals(validUser.getEmail(), found.getEmail());
        assertEquals(validUser.getAge(), found.getAge());
    }

    @Test
    void findById_whenUserDoesNotExists_shouldReturnNull() {
        User found = userDAO.findById(1L);

        assertNull(found);
    }

    @Test
    void findAll_whenUsersExist_shouldReturnAllUsers() {
        User user = new User("Ivan", "my@mail.com", 20);
        User user2 = new User("Oleg", "oleg@gmail.com", 25);
        userDAO.save(user);
        userDAO.save(user2);
        userDAO.save(validUser);

        List<User> users = userDAO.findAll();

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
    void findAll_WhenNoUsersExist_ShouldReturnEmptyList() {
        List<User> users = userDAO.findAll();

        assertTrue(users.isEmpty());
    }

    @Test
    void update_withValidUser_shouldUpdateUser() {
        userDAO.save(validUser);
        Long userId = validUser.getId();

        validUser.setEmail("newEmail@gmail.com");
        validUser.setAge(27);
        userDAO.update(validUser);

        User updated = userDAO.findById(userId);
        assertEquals("newEmail@gmail.com", updated.getEmail());
        assertEquals(27, updated.getAge());
    }

    @Test
    void update_withDuplicateEmail_shouldThrow() {
        User user = new User("Ivan", "my@mail.com", 20);
        userDAO.save(validUser);
        userDAO.save(user);
        validUser.setEmail("my@mail.com");
        assertThrows(HibernateException.class, () -> userDAO.update(validUser));
    }

    @Test
    void delete_withValidUser_shouldDeleteUser() {
        userDAO.save(validUser);
        Long userId = validUser.getId();

        userDAO.delete(validUser);

        User deleted = userDAO.findById(userId);
        assertNull(deleted);
    }
}
