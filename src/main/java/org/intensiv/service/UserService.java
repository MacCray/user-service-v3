package org.intensiv.service;

import org.intensiv.dao.UserDAO;
import org.intensiv.entity.User;
import org.intensiv.exception.UserNotFoundException;
import org.intensiv.exception.UserValidationException;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
        log.info("UserService инициализирован");
    }

    public void createUser(User user) {
        validateUser(user);
        log.debug("Запрос на создание пользователя name={} email={}", user.getName(), user.getEmail());
        userDAO.save(user);
        log.info("Пользователь создан name={} email={}", user.getName(), user.getEmail());
    }

    public User getUser(Long id) {
        if (id == null || id <= 0) {
            throw new UserValidationException("ID пользователя должен быть положительным числом");
        }
        log.debug("Получение пользователя по id={}", id);
        User user = userDAO.findById(id);
        if (user == null) {
            log.debug("Пользователь не найден id={}", id);
            throw new UserNotFoundException("Пользователь с ID " + id + " не найден");
        }
        return user;
    }

    public List<User> getAllUsers() {
        log.debug("Получение всех пользователей");
        return userDAO.findAll();
    }

    public void updateUser(User user) {
        if (user == null || user.getId() == null) {
            throw new UserValidationException("Пользователь и его ID не могут быть null");
        }
        validateUser(user);
        log.debug("Обновление пользователя id={} name={}", user.getId(), user.getName());
        userDAO.update(user);
        log.info("Пользователь обновлен id={}", user.getId());
    }

    public void deleteUser(User user) {
        if (user == null || user.getId() == null) {
            throw new UserValidationException("Пользователь и его ID не могут быть null");
        }
        log.debug("Удаление пользователя id={} name={}", user.getId(), user.getName());
        userDAO.delete(user);
        log.info("Пользователь удален id={}", user.getId());
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserValidationException("Пользователь не может быть null");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new UserValidationException("Имя пользователя не может быть пустым");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new UserValidationException("Email пользователя не может быть пустым");
        }
        if (user.getAge() == null || user.getAge() < 0 || user.getAge() > 150) {
            throw new UserValidationException("Возраст должен быть от 0 до 150 лет");
        }
    }
}
