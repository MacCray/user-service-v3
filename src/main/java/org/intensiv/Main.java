package org.intensiv;

import org.intensiv.entity.User;
import org.intensiv.service.UserService;
import org.intensiv.util.HibernateUtil;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserService();

    public static void main(String[] args) {
        try {
            runApplication();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
            HibernateUtil.shutdown();
        }
    }

    private static void runApplication() {
        while (true) {
            printMenu();
            int choice = getIntInput("Выберите действие: ");

            switch (choice) {
                case 1 -> addUser();
                case 2 -> getUser();
                case 3 -> listUsers();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("1 - Добавить User");
        System.out.println("2 - Найти User");
        System.out.println("3 - Показать всех User");
        System.out.println("4 - Обновить User");
        System.out.println("5 - Удалить User");
        System.out.println("0 - Выход");
    }

    private static void addUser() {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getUser() {
        try {
            userService.getUser(getLongInput("User ID:"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void listUsers() {
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
            e.printStackTrace();
        }
    }

    private static void updateUser() {
        try {
            Long id = getLongInput("User ID для обновления: ");
            User user = userService.getUser(id);

            if (user == null) {
                System.out.println("User с ID " + id + " не найден");
                return;
            }

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
            System.out.println("User обновлен!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteUser() {
        try {
            Long id = getLongInput("User ID для удаления: ");
            User user = userService.getUser(id);

            if (user == null) {
                System.out.println("User с ID " + id + " не найден");
                return;
            }

            System.out.println("Удалить User: " + user + " ? (Y/N)");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if ("y".equals(confirmation)) {
                userService.deleteUser(user);
                System.out.println("User удален!");
            } else {
                System.out.println("Отменено");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число:");
            }
        }
    }

    private static Long getLongInput(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите корректное число");
            }
        }
    }
}
