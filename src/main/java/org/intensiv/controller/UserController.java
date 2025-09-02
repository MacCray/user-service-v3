package org.intensiv.controller;

import lombok.extern.slf4j.Slf4j;
import org.intensiv.entity.User;
import org.intensiv.exception.UserNotFoundException;
import org.intensiv.exception.UserValidationException;
import org.intensiv.service.UserService;

import java.util.List;
import java.util.Scanner;

@Slf4j
public class UserController {
    private final Scanner scanner;
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
        this.scanner = new Scanner(System.in);
    }

    public void runApplication() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = getIntInput("Выберите действие: ");

            switch (choice) {
                case 1 -> addUser();
                case 2 -> getUser();
                case 3 -> listUsers();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 0 -> running = false;
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private void printMenu() {
        System.out.println("1 - Добавить User");
        System.out.println("2 - Найти User");
        System.out.println("3 - Показать всех User");
        System.out.println("4 - Обновить User");
        System.out.println("5 - Удалить User");
        System.out.println("0 - Выход");
    }

    private void addUser() {
        try {
            System.out.print("Имя: ");
            String name = scanner.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("Имя не может быть пустым");
                return;
            }

            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            if (email.isEmpty()) {
                System.out.println("Email не может быть пустым");
                return;
            }

            int age = getIntInput("Возраст: ");

            if (age < 0 || age > 150) {
                System.out.println("Некорректный возраст");
                return;
            }

            User user = new User(name, email, age);
            userService.createUser(user);
            System.out.println("Пользователь успешно создан!");

        } catch (UserValidationException e) {
            System.out.println("Ошибка валидации: " + e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка при добавлении пользователя", e);
            System.out.println("Произошла неожиданная ошибка при создании пользователя");
        }
    }

    private void getUser() {
        try {
            User user = userService.getUser(getLongInput("User ID:"));
            System.out.printf("ID: %d | %s | %s | %d лет%n",
                    user.getId(), user.getName(), user.getEmail(), user.getAge());
        } catch (UserNotFoundException e) {
            System.out.println("Пользователь не найден: " + e.getMessage());
        } catch (UserValidationException e) {
            System.out.println("Ошибка валидации: " + e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка при получении пользователя", e);
            System.out.println("Произошла неожиданная ошибка при получении пользователя");
        }
    }

    private void listUsers() {
        try {
            List<User> users = userService.getAllUsers();

            if (users.isEmpty()) {
                System.out.println("Users не найдены");
                return;
            }

            System.out.println("\n--- Список User ---");
            users.forEach(user ->
                    System.out.printf("ID: %d | %s | %s | %d лет%n",
                            user.getId(), user.getName(), user.getEmail(), user.getAge())
            );

        } catch (Exception e) {
            log.error("Ошибка при получении списка пользователей", e);
            System.out.println("Произошла неожиданная ошибка при получении списка пользователей");
        }
    }

    private void updateUser() {
        try {
            Long id = getLongInput("User ID для обновления: ");
            User user = userService.getUser(id);

            System.out.printf("Текущие данные: ID=%d, Имя='%s', Email='%s', Возраст=%d%n",
                    user.getId(), user.getName(), user.getEmail(), user.getAge());
            System.out.println("Оставьте поле пустым, чтобы не изменять его");

            System.out.print("Новое имя: ");
            String newName = scanner.nextLine().trim();
            if (!newName.isEmpty()) {
                user.setName(newName);
            }

            System.out.print("Новый email: ");
            String newEmail = scanner.nextLine().trim();
            if (!newEmail.isEmpty()) {
                user.setEmail(newEmail);
            }

            System.out.print("Новый возраст: ");
            String ageInput = scanner.nextLine().trim();
            if (!ageInput.isEmpty()) {
                try {
                    int newAge = Integer.parseInt(ageInput);
                    if (newAge >= 0 && newAge <= 150) {
                        user.setAge(newAge);
                    } else {
                        System.out.println("Некорректный возраст, оставлен прежний");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Некорректный формат возраста, оставлен прежний");
                }
            }

            userService.updateUser(user);
            System.out.println("Пользователь обновлен!");

        } catch (UserNotFoundException e) {
            System.out.println("Пользователь не найден: " + e.getMessage());
        } catch (UserValidationException e) {
            System.out.println("Ошибка валидации: " + e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка при обновлении пользователя", e);
            System.out.println("Произошла неожиданная ошибка при обновлении пользователя");
        }
    }

    private void deleteUser() {
        try {
            Long id = getLongInput("User ID для удаления: ");
            User user = userService.getUser(id);

            System.out.println("Удалить User: " + user + " ? (Y/N)");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if ("y".equals(confirmation)) {
                userService.deleteUser(user);
                System.out.println("User удален!");
            } else {
                System.out.println("Отменено");
            }

        } catch (UserNotFoundException e) {
            System.out.println("Пользователь не найден: " + e.getMessage());
        } catch (UserValidationException e) {
            System.out.println("Ошибка валидации: " + e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя", e);
            System.out.println("Произошла неожиданная ошибка при удалении пользователя");
        }
    }

    private int getIntInput(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число:");
            }
        }
    }

    private Long getLongInput(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите корректное число");
            }
        }
    }

    public void close() {
        scanner.close();
    }
}
