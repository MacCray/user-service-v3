package org.intensiv.userapi.service;

import org.intensiv.dao.UserDAO;
import org.intensiv.userapi.entity.User;
import org.intensiv.userapi.exception.UserNotFoundException;
import org.intensiv.userapi.exception.UserValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
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

    static Stream<Arguments> invalidUserProviderForCreate() {
        return Stream.of(
                Arguments.of(Named.of("Имя = null", new User(null, "email@gmail.com", 1))),
                Arguments.of(Named.of("Имя = пустое", new User("", "email@gmail.com", 1))),
                Arguments.of(Named.of("Имя = пробелы", new User("   ", "email@gmail.com", 1))),
                Arguments.of(Named.of("Email = null", new User("Иван", null, 1))),
                Arguments.of(Named.of("Email = пустой", new User("Иван", "", 1))),
                Arguments.of(Named.of("Email = пробелы", new User("Иван", "  ", 1))),
                Arguments.of(Named.of("Возраст = null", new User("Иван", "email@gmail.com", null))),
                Arguments.of(Named.of("Возраст > 150", new User("Иван", "email@gmail.com", 200))),
                Arguments.of(Named.of("Возраст < 0", new User("Иван", "email@gmail.com", -10))));
    }

    static Stream<Arguments> invalidUserProviderForDelete() {
        return Stream.of(
                Arguments.of(Named.of("Id = null", new User("Иван", "email@gmail.com", 1) {
                    {
                        setId(null);
                    }
                })),
                Arguments.of(Named.of("Id = 0", new User("Иван", "email@gmail.com", 1) {
                    {
                        setId(0L);
                    }
                })),
                Arguments.of(Named.of("Id < 0", new User("Иван", "email@gmail.com", 1) {
                    {
                        setId(-10L);
                    }
                })));
    }

    static Stream<Arguments> invalidUserProviderForUpdate() {
        return Stream.concat(invalidUserProviderForDelete(), invalidUserProviderForCreate());
    }

    @BeforeEach
    void setUpValidUser() {
        validUser = new User("Roman", "email@gmail.com", 26);
        validUser.setId(1L);
    }

    @Test
    void createUser_withValidUser_shouldCallSave() {
        userService.createUser(validUser);
        verify(userDAO).save(validUser);
    }

    @ParameterizedTest(name = "Тест - {index}: Создание пользователя с [{0}] выбрасывает исключение")
    @MethodSource("invalidUserProviderForCreate")
    @NullSource
    void createUser_withInvalidUser_shouldThrowException(User user) {
        assertThrows(UserValidationException.class, () -> userService.createUser(user));
        verify(userDAO, never()).save(any());
    }

    @ParameterizedTest(name = "Тест - {index}: Получения пользователя с id = [{0}] возвращает корректного пользователя")
    @ValueSource(longs = {1L, 10L, 3543L, Long.MAX_VALUE})
    void getUser_withValidId_shouldReturnUser(Long id) {
        when(userDAO.findById(id)).thenReturn(validUser);

        User result = userService.getUser(id);

        assertEquals(result, validUser);
        verify(userDAO).findById(id);
    }

    @ParameterizedTest(name = "Тест - {index}: Получения пользователя с id = [{0}] выбрасывает исключение")
    @ValueSource(longs = {0L, -10L})
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

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        List<User> expectedUsers = List.of(validUser);
        when(userDAO.findAll()).thenReturn(expectedUsers);

        List<User> result = userService.getAllUsers();

        assertEquals(expectedUsers, result);
        verify(userDAO).findAll();
    }

    @Test
    void updateUser_withValidUser_shouldUpdateUser() {
        userService.updateUser(validUser);
        verify(userDAO).update(validUser);
    }

    @ParameterizedTest(name = "Тест - {index}: Обновление пользователя с [{0}] выбрасывает исключение")
    @MethodSource("invalidUserProviderForUpdate")
    @NullSource
    void updateUser_withInvalidUser_shouldThrowException(User user) {
        assertThrows(UserValidationException.class, () -> userService.updateUser(user));
        verify(userDAO, never()).update(any());
    }

    @Test
    void deleteUser_withValidUser_shouldDeleteUser() {
        userService.deleteUser(validUser);
        verify(userDAO).delete(validUser);
    }

    @ParameterizedTest(name = "Тест - {index}: Удаление пользователя с [{0}] выбрасывает исключение")
    @MethodSource("invalidUserProviderForDelete")
    @NullSource
    void deleteUser_withInvalidUser_shouldThrowException(User user) {
        assertThrows(UserValidationException.class, () -> userService.deleteUser(user));
        verify(userDAO, never()).delete(any());
    }
}
