package org.intensiv.service;

import org.intensiv.dao.UserDAO;
import org.intensiv.entity.User;
import org.intensiv.exception.UserNotFoundException;
import org.intensiv.exception.UserValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {
    @Mock
    private UserDAO userDAO;
    @InjectMocks
    private UserService userService;
    private User validUser;

    static Stream<User> invalidUserProviderForCreate() {
        return Stream.of(
                new User(null, "email@gmail.com", 1) {
                    {
                        setId(1L);
                    }
                },
                new User("", "email@gmail.com", 1) {
                    {
                        setId(1L);
                    }
                },
                new User("   ", "email@gmail.com", 1) {
                    {
                        setId(1L);
                    }
                },
                new User("Иван", null, 1) {
                    {
                        setId(1L);
                    }
                },
                new User("Иван", "", 1) {
                    {
                        setId(1L);
                    }
                },
                new User("Иван", "  ", 1) {
                    {
                        setId(1L);
                    }
                },
                new User("Иван", "email@gmail.com", null) {
                    {
                        setId(1L);
                    }
                },
                new User("Иван", "email@gmail.com", 200) {
                    {
                        setId(1L);
                    }
                },
                new User("Иван", "email@gmail.com", -10) {
                    {
                        setId(1L);
                    }
                });
    }

    static Stream<User> invalidUserProviderForDelete() {
        return Stream.of(
                new User("Иван", "email@gmail.com", 1) {
                    {
                        setId(null);
                    }
                },
                new User("Иван", "email@gmail.com", 1) {
                    {
                        setId(0L);
                    }
                },
                new User("Иван", "email@gmail.com", 1) {
                    {
                        setId(-10L);
                    }
                });
    }

    static Stream<User> invalidUserProviderForUpdate() {
        return Stream.concat(invalidUserProviderForDelete(), invalidUserProviderForCreate());
    }

    @BeforeEach
    void setUpValidUser() {
        validUser = new User("Roman", "email@gmail.com", 26);
        validUser.setId(1L);
    }

    // createUser tests
    @Test
    void createUser_withValidUser_shouldCallSave() {
        userService.createUser(validUser);
        verify(userDAO).save(validUser);
    }

    @ParameterizedTest
    @MethodSource("invalidUserProviderForCreate")
    @NullSource
    void createUser_withInvalidUser_shouldThrowException(User user) {
        assertThrows(UserValidationException.class, () -> userService.createUser(user));
        verify(userDAO, never()).save(any());
    }

    // getUser tests
    @ParameterizedTest
    @ValueSource(longs = { 1L, 10L, 3543L, Long.MAX_VALUE })
    void getUser_withValidId_shouldReturnUser(Long id) {
        when(userDAO.findById(id)).thenReturn(validUser);

        User result = userService.getUser(id);

        assertEquals(result, validUser);
        verify(userDAO).findById(id);
    }

    @ParameterizedTest
    @ValueSource(longs = { 0L, -10L })
    @NullSource
    void getUser_withInvalidId_shouldThrowException(Long id) {
        assertThrows(UserValidationException.class, () -> userService.getUser(id));
        verify(userDAO, never()).findById(id);
    }

    @Test
    void getUser_whenUserNotFound_shouldThrowException() {
        when(userDAO.findById(1L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.getUser(1L));
        verify(userDAO).findById(1L);
    }

    // getAllUsers tests
    @Test
    void getAllUsers_shouldReturnAllUsers() {
        List<User> expectedUsers = List.of(validUser);
        when(userDAO.findAll()).thenReturn(expectedUsers);

        List<User> result = userService.getAllUsers();

        assertEquals(expectedUsers, result);
        verify(userDAO).findAll();
    }

    // updateUser tests
    @Test
    void updateUser_withValidUser_shouldUpdateUser() {
        userService.updateUser(validUser);
        verify(userDAO).update(validUser);
    }

    @ParameterizedTest
    @MethodSource("invalidUserProviderForUpdate")
    @NullSource
    void updateUser_withInvalidUser_shouldThrowException(User user) {
        assertThrows(UserValidationException.class, () -> userService.updateUser(user));
        verify(userDAO, never()).update(any());
    }

    // deleteUser tests
    @Test
    void deleteUser_withValidUser_shouldDeleteUser() {
        userService.deleteUser(validUser);
        verify(userDAO).delete(validUser);
    }

    @ParameterizedTest
    @MethodSource("invalidUserProviderForDelete")
    @NullSource
    void deleteUser_withInvalidUser_shouldThrowException(User user) {
        assertThrows(UserValidationException.class, () -> userService.deleteUser(user));
        verify(userDAO, never()).delete(any());
    }
}
