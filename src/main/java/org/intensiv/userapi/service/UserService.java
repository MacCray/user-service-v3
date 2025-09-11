package org.intensiv.userapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.intensiv.userapi.dto.request.CreateUserRequestDto;
import org.intensiv.userapi.dto.request.UpdateUserRequestDto;
import org.intensiv.userapi.dto.response.UserResponseDto;
import org.intensiv.userapi.entity.User;
import org.intensiv.userapi.exception.DuplicateEmailException;
import org.intensiv.userapi.exception.UserNotFoundException;
import org.intensiv.userapi.mapper.UserMapper;
import org.intensiv.userapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDto createUser(CreateUserRequestDto requestDto) {
        log.debug("Запрос на создание пользователя name={} email={}", requestDto.name(), requestDto.email());
        if (userRepository.existsByEmail(requestDto.email())) {
            throw new DuplicateEmailException("Пользователь с email " + requestDto.email() + " уже существует");
        }
        User user = userMapper.toUserEntity(requestDto);
        UserResponseDto responseDto = userMapper.toUserResponseDto(userRepository.save(user));
        log.info("Пользователь создан name={} email={}", requestDto.name(), requestDto.email());
        return responseDto;
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUser(Long id) {
        log.debug("Получение пользователя по id={}", id);
        return userRepository.findById(id).map(userMapper::toUserResponseDto).orElseThrow(() -> new UserNotFoundException("User c id:" + id + " не найден"));
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        log.debug("Получение всех пользователей");
        return userRepository.findAll().stream().map(userMapper::toUserResponseDto).toList();
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UpdateUserRequestDto updatedUserDto) {
        log.debug("Обновление пользователя id={} name={}", id, updatedUserDto.name());
        if (userRepository.existsByEmailAndIdNot(updatedUserDto.email(), id)) {
            throw new DuplicateEmailException("Пользователь с email " + updatedUserDto.email() + " уже существует");
        }
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User c id:" + id + " не найден"));
        userMapper.updateUserFromDto(updatedUserDto, user);
        userRepository.save(user);

        log.info("Пользователь обновлен id={}", user.getId());
        return userMapper.toUserResponseDto(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.debug("Удаление пользователя id={}", id);
        userRepository.delete(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User c id:" + id + " не найден")));
        log.info("Пользователь удален id={}", id);
    }
}
