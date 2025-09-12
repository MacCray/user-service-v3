package org.intensiv.userapi.service;

import org.intensiv.userapi.dto.request.CreateUserRequestDto;
import org.intensiv.userapi.dto.request.UpdateUserRequestDto;
import org.intensiv.userapi.dto.response.UserResponseDto;
import org.intensiv.userapi.entity.User;
import org.intensiv.userapi.exception.DuplicateEmailException;
import org.intensiv.userapi.exception.UserNotFoundException;
import org.intensiv.userapi.mapper.UserMapper;
import org.intensiv.userapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {
    private static final Long USER_ID = 1L;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserService userService;

    private CreateUserRequestDto createUserRequestDto;
    private UpdateUserRequestDto updateUserRequestDto;
    private UserResponseDto userResponseDto;
    private User user;

    @BeforeEach
    void setUp() {
        createUserRequestDto = new CreateUserRequestDto("Роман Красиков", "krasikov.roman@gmail.com", 26);
        updateUserRequestDto = new UpdateUserRequestDto("Роман Красиков", "krasikov.roman.new@gmail.com", 27);
        userResponseDto = new UserResponseDto(1L, "Роман Красиков", "krasikov.roman@gmail.com");
        user = new User("Роман Красиков", "krasikov.roman@gmail.com", 26);
        user.setId(1L);
    }

    @Test
    @DisplayName("Should create user when email doesn't exist")
    void createUser_WhenEmailDoesNotExist_ShouldCreateUser() {
        when(userRepository.existsByEmail(createUserRequestDto.email())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserEntity(createUserRequestDto)).thenReturn(user);
        when(userMapper.toUserResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.createUser(createUserRequestDto);

        assertNotNull(result);
        assertEquals(userResponseDto, result);
        verify(userRepository).existsByEmail(createUserRequestDto.email());
        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserResponseDto(user);
    }

    @Test
    @DisplayName("Should throw DuplicateEmailException when email already exists")
    void createUser_WhenEmailExists_ShouldThrowDuplicateEmailException() {
        when(userRepository.existsByEmail(createUserRequestDto.email())).thenReturn(true);

        DuplicateEmailException exception = assertThrows(
                DuplicateEmailException.class,
                () -> userService.createUser(createUserRequestDto)
        );

        assertEquals("Пользователь с email " + createUserRequestDto.email() + " уже существует",
                exception.getMessage());
        verify(userRepository).existsByEmail(createUserRequestDto.email());
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toUserResponseDto(any(User.class));
    }

    @Test
    @DisplayName("Should get user by id when user exists")
    void getUser_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.getUser(USER_ID);

        assertNotNull(result);
        assertEquals(userResponseDto, result);
        verify(userRepository).findById(USER_ID);
        verify(userMapper).toUserResponseDto(user);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user doesn't exist")
    void getUser_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.getUser(USER_ID)
        );

        assertEquals("User c id:" + USER_ID + " не найден", exception.getMessage());
        verify(userRepository).findById(USER_ID);
        verify(userMapper, never()).toUserResponseDto(any(User.class));
    }

    @Test
    @DisplayName("Should return all users")
    void getAllUsers_WhenUsersExist_ShouldReturnAllUsers() {
        User user2 = new User("Jane Doe", "jane.doe@example.com", 30);
        user2.setId(2L);
        UserResponseDto userResponseDto2 = new UserResponseDto(2L, "Jane Doe", "jane.doe@example.com");

        List<User> users = Arrays.asList(user, user2);
        List<UserResponseDto> expectedResponse = Arrays.asList(userResponseDto, userResponseDto2);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toUserResponseDto(user)).thenReturn(userResponseDto);
        when(userMapper.toUserResponseDto(user2)).thenReturn(userResponseDto2);

        List<UserResponseDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedResponse, result);
        verify(userRepository).findAll();
        verify(userMapper, times(2)).toUserResponseDto(any(User.class));
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void getAllUsers_WhenNoUsersExist_ShouldReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponseDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
        verify(userMapper, never()).toUserResponseDto(any(User.class));
    }

    @Test
    @DisplayName("Should update user when user exists and email is unique")
    void updateUser_WhenUserExistsAndEmailIsUnique_ShouldUpdateUser() {
        UserResponseDto updatedUserResponseDto = new UserResponseDto(USER_ID, "John Updated", "john.updated@example.com");

        when(userRepository.existsByEmailAndIdNot(updateUserRequestDto.email(), USER_ID)).thenReturn(false);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponseDto(user)).thenReturn(updatedUserResponseDto);

        UserResponseDto result = userService.updateUser(USER_ID, updateUserRequestDto);

        assertNotNull(result);
        assertEquals(updatedUserResponseDto, result);
        verify(userRepository).existsByEmailAndIdNot(updateUserRequestDto.email(), USER_ID);
        verify(userRepository).findById(USER_ID);
        verify(userMapper).updateUserFromDto(updateUserRequestDto, user);
        verify(userRepository).save(user);
        verify(userMapper).toUserResponseDto(user);
    }

    @Test
    @DisplayName("Should throw DuplicateEmailException when updating with existing email")
    void updateUser_WhenEmailExists_ShouldThrowDuplicateEmailException() {
        when(userRepository.existsByEmailAndIdNot(updateUserRequestDto.email(), USER_ID)).thenReturn(true);

        DuplicateEmailException exception = assertThrows(
                DuplicateEmailException.class,
                () -> userService.updateUser(USER_ID, updateUserRequestDto)
        );

        assertEquals("Пользователь с email " + updateUserRequestDto.email() + " уже существует",
                exception.getMessage());
        verify(userRepository).existsByEmailAndIdNot(updateUserRequestDto.email(), USER_ID);
        verify(userRepository, never()).findById(USER_ID);
        verify(userMapper, never()).updateUserFromDto(any(), any());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when updating non-existent user")
    void updateUser_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
        when(userRepository.existsByEmailAndIdNot(updateUserRequestDto.email(), USER_ID)).thenReturn(false);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUser(USER_ID, updateUserRequestDto)
        );

        assertEquals("User c id:" + USER_ID + " не найден", exception.getMessage());
        verify(userRepository).existsByEmailAndIdNot(updateUserRequestDto.email(), USER_ID);
        verify(userRepository).findById(USER_ID);
        verify(userMapper, never()).updateUserFromDto(any(), any());
    }

    @Test
    @DisplayName("Should delete user successfully when user exists")
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        when(userRepository.deleteUserById(USER_ID)).thenReturn(1);

        assertDoesNotThrow(() -> userService.deleteUser(USER_ID));

        verify(userRepository).deleteUserById(USER_ID);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when deleting non-existent user")
    void deleteUser_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
        when(userRepository.deleteUserById(USER_ID)).thenReturn(0);

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.deleteUser(USER_ID)
        );

        assertEquals("User c id:" + USER_ID + " не найден", exception.getMessage());
        verify(userRepository).deleteUserById(USER_ID);
    }
}
