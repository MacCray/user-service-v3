package org.intensiv.userapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.intensiv.userapi.dto.request.CreateUserRequestDto;
import org.intensiv.userapi.dto.request.UpdateUserRequestDto;
import org.intensiv.userapi.dto.response.UserResponseDto;
import org.intensiv.userapi.exception.DuplicateEmailException;
import org.intensiv.userapi.exception.UserNotFoundException;
import org.intensiv.userapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WebMvcTest(UserController.class)
public class UserControllerTests {
    private static final Long USER_ID = 1L;
    @MockitoBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private UserResponseDto userResponseDto;
    private CreateUserRequestDto createUserRequestDto;
    private UpdateUserRequestDto updateUserRequestDto;

    @BeforeEach
    void setUp() {
        createUserRequestDto = new CreateUserRequestDto("Роман Красиков", "krasikov.roman@gmail.com", 26);
        updateUserRequestDto = new UpdateUserRequestDto("Роман Красиков", "krasikov.roman.new@gmail.com", 27);
        userResponseDto = new UserResponseDto(1L, "Роман Красиков", "krasikov.roman@gmail.com");
    }

    @Test
    @DisplayName("Should create user")
    void createUser_WithValidData_ReturnsUser() throws Exception {
        when(userService.createUser(any(CreateUserRequestDto.class))).thenReturn(userResponseDto);

        mockMvc.perform(post("/userapi/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Роман Красиков"))
                .andExpect(jsonPath("$.email").value("krasikov.roman@gmail.com"));

        verify(userService).createUser(any(CreateUserRequestDto.class));
    }

    @Test
    @DisplayName("Should return 400 when creating user with invalid data")
    void createUser_WithInvalidData_Returns400() throws Exception {
        CreateUserRequestDto invalidDto = new CreateUserRequestDto("", "", 20);

        mockMvc.perform(post("/userapi/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(CreateUserRequestDto.class));
    }

    @Test
    @DisplayName("Should return 409 when email already exists")
    void createUser_WhenEmailExists_ShouldReturn409() throws Exception {
        CreateUserRequestDto request = new CreateUserRequestDto("Роман", "duplicate@gmail.com", 20);

        when(userService.createUser(any())).thenThrow(new DuplicateEmailException("Duplicate email: duplicate@gmail.com"));

        mockMvc.perform(post("/userapi/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Duplicate email: duplicate@gmail.com"));
    }

    @Test
    @DisplayName("Should get user by id")
    void getUser_WithValidId_ReturnsUser() throws Exception {
        when(userService.getUser(USER_ID)).thenReturn(userResponseDto);

        mockMvc.perform(get("/userapi/users/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Роман Красиков"))
                .andExpect(jsonPath("$.email").value("krasikov.roman@gmail.com"));

        verify(userService).getUser(USER_ID);
    }

    @Test
    @DisplayName("Should return 400 when getting user with invalid id")
    void getUser_WithInvalidId_Returns400() throws Exception {
        mockMvc.perform(get("/userapi/users/{id}", 0))
                .andExpect(status().isBadRequest());

        verify(userService, never()).getUser(any(Long.class));
    }

    @Test
    @DisplayName("Should return 404 when getting non-existent user")
    void getUser_UserNotFound_Returns404() throws Exception {
        when(userService.getUser(999L)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/userapi/users/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    @DisplayName("Should get all users")
    void getAllUsers_ReturnsUserList() throws Exception {
        List<UserResponseDto> users = Arrays.asList(
                userResponseDto,
                new UserResponseDto(2L, "Красиков Роман", "roman.krasikov@gmail.com")
        );
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/userapi/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Роман Красиков"))
                .andExpect(jsonPath("$[0].email").value("krasikov.roman@gmail.com"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Красиков Роман"))
                .andExpect(jsonPath("$[1].email").value("roman.krasikov@gmail.com"));
        verify(userService).getAllUsers();
    }

    @Test
    @DisplayName("Should update user")
    void updateUser_WithValidIdAndData_ReturnsUpdatedUser() throws Exception {
        UserResponseDto updatedUser = new UserResponseDto(1L, "Роман Красиков", "krasikov.roman.new@gmail.com");
        when(userService.updateUser(eq(USER_ID), any(UpdateUserRequestDto.class))).thenReturn(updatedUser);

        mockMvc.perform(patch("/userapi/users/{id}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Роман Красиков"))
                .andExpect(jsonPath("$.email").value("krasikov.roman.new@gmail.com"));
        verify(userService).updateUser(eq(USER_ID), any(UpdateUserRequestDto.class));
    }

    @Test
    @DisplayName("Should return 400 when updating user with invalid id")
    void updateUser_WithInvalidId_Returns400() throws Exception {
        mockMvc.perform(patch("/userapi/users/{id}", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(any(Long.class), any(UpdateUserRequestDto.class));
    }

    @Test
    @DisplayName("Should return 400 when updating user with invalid data")
    void updateUser_InvalidData_Returns400() throws Exception {
        UpdateUserRequestDto invalidDto = new UpdateUserRequestDto("", "krasikov@gmail.com", 20);

        mockMvc.perform(patch("/userapi/users/{id}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(any(Long.class), any(UpdateUserRequestDto.class));
    }

    @Test
    @DisplayName("Should return 409 when updating with already existing email")
    void updateUser_WithExistingEmail_Returns409() throws Exception {
        when(userService.updateUser(eq(USER_ID), any(UpdateUserRequestDto.class)))
                .thenThrow(new DuplicateEmailException("User with email already exists"));

        mockMvc.perform(patch("/userapi/users/{id}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequestDto)))
                .andExpect(status().isConflict());

        verify(userService).updateUser(eq(USER_ID), any(UpdateUserRequestDto.class));
    }

    @Test
    @DisplayName("Should delete user and return 204")
    void deleteUser_WithValidId_Returns204() throws Exception {
        doNothing().when(userService).deleteUser(USER_ID);

        mockMvc.perform(delete("/userapi/users/{id}", USER_ID))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(USER_ID);
    }

    @Test
    @DisplayName("Should return 400 when deleting user with invalid id")
    void deleteUser_InvalidId_Returns400() throws Exception {
        mockMvc.perform(delete("/userapi/users/{id}", 0))
                .andExpect(status().isBadRequest());

        verify(userService, never()).deleteUser(any(Long.class));
    }
}
