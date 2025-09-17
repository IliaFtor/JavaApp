package com.example.demo.templates;

import com.example.demo.models.User;
import com.example.demo.services.LoggerService;
import com.example.demo.services.UserService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

@Component
public class ConsoleView {
    private final UserService userService;
    private final LoggerService loggerService;
    private final Scanner scanner;
    private User currentUser;

    public ConsoleView(UserService userService, LoggerService loggerService) {
        this.userService = userService;
        this.loggerService = loggerService;
        this.scanner = new Scanner(System.in);
        this.currentUser = null;
    }

    public void start() {
        System.out.println("Добро пожаловать в систему авторизации!");

        while (true) {
            if (currentUser == null) {
                showMainMenu();
            } else {
                showUserMenu();
            }
        }
    }

    private void showMainMenu() {
        clearConsole();
        System.out.println("\n=== ГЛАВНОЕ МЕНЮ ===");
        System.out.println("1. Войти");
        System.out.println("2. Зарегистрироваться");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Выйти из программы");
        System.out.print("Выберите действие: ");

        int choice = getIntInput();

        switch (choice) {
            case 1 -> login();
            case 2 -> register();
            case 3 -> showAllUsers();
            case 4 -> exit();
            default -> System.out.println("Неверный выбор!");
        }
    }

    private void showUserMenu() {
        clearConsole();
        System.out.println("\n=== ЛИЧНЫЙ КАБИНЕТ ===");
        System.out.println("Пользователь: " + currentUser.getUsername());
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println("Роль: " + currentUser.getRole());

        System.out.println("1. Сменить пароль");
        System.out.println("2. Показать всех пользователей");

        // Только для администраторов
        if (userService.isAdmin(currentUser)) {
            System.out.println("3. Показать логи");
            System.out.println("4. Очистить логи");
        }

        System.out.println("5. Выйти из аккаунта");
        System.out.print("Выберите действие: ");

        int choice = getIntInput();

        switch (choice) {
            case 1 -> changePassword();
            case 2 -> showAllUsers();
            case 3 -> {
                if (userService.isAdmin(currentUser)) {
                    showLogs();
                } else {
                    System.out.println("Доступ запрещен. Только для администраторов.");
                }
            }
            case 4 -> {
                if (userService.isAdmin(currentUser)) {
                    clearLogs();
                } else {
                    System.out.println("Доступ запрещен. Только для администраторов.");
                }
            }
            case 5 -> logout();
            default -> System.out.println("Неверный выбор!");
        }
    }

    private void login() {
        System.out.println("\n=== ВХОД ===");
        System.out.print("Логин: ");
        String username = scanner.nextLine();

        System.out.print("Пароль: ");
        String password = scanner.nextLine();

        try {
            if (userService.validateUser(username, password)) {
                currentUser = userService.findByUsername(username);
                loggerService.logLoginSuccess(username);
                clearConsole();
                System.out.println("Вход выполнен успешно!");
            } else {
                loggerService.logLoginFailure(username, "Invalid credentials");
                System.out.println("Неверный логин или пароль!");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении данных: " + e.getMessage());
        }
    }

    private void register() {
        System.out.println("\n=== РЕГИСТРАЦИЯ ===");
        System.out.print("Логин: ");
        String username = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Пароль: ");
        String password = scanner.nextLine();

        System.out.print("Подтвердите пароль: ");
        String confirmPassword = scanner.nextLine();

        if (!password.equals(confirmPassword)) {
            clearConsole();
            System.out.println("Пароли не совпадают!");
            return;
        }

        try {
            if (userService.findByUsername(username) != null) {
                System.out.println("Пользователь с таким логином уже существует!");
                return;
            }

            long newId = userService.getAllUsers().size() + 1L;
            User newUser = new User(newId, username, email, password, "USER");

            userService.saveUser(newUser);
            loggerService.logRegistration(username);
            clearConsole();
            System.out.println("Регистрация прошла успешно!");

        } catch (IOException e) {
            System.out.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }

    private void changePassword() {
        System.out.println("\n=== СМЕНА ПАРОЛЯ ===");
        System.out.print("Текущий пароль: ");
        String currentPassword = scanner.nextLine();

        if (!currentUser.getPassword().equals(currentPassword)) {
            System.out.println("Неверный текущий пароль!");
            return;
        }

        System.out.print("Новый пароль: ");
        String newPassword = scanner.nextLine();

        System.out.print("Подтвердите новый пароль: ");
        String confirmPassword = scanner.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Пароли не совпадают!");
            return;
        }

        currentUser.setPassword(newPassword);
        try {
            loggerService.logPasswordChange(currentUser.getUsername());
            clearConsole();
            System.out.println("Пароль успешно изменен!");
        } catch (IOException e) {
            System.out.println("Ошибка при записи лога: " + e.getMessage());
        }
    }

    private void showAllUsers() {
        try {
            List<User> users = userService.getAllUsers();

            System.out.println("\n=== ПОЛЬЗОВАТЕЛИ ===");
            if (users.isEmpty()) {
                clearConsole();
                System.out.println("Пользователей нет");
            } else {
                for (User user : users) {
                    String status = user.isEnabled() ? "Активен" : "Неактивен";
                    System.out.println(
                            user.getUsername() + " (" + user.getEmail() + ") - " + user.getRole() + " - " + status);
                }
                System.out.println("Всего: " + users.size());
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении данных: " + e.getMessage());
        }
    }

    private void showLogs() {
        try {
            loggerService.showLogs();
        } catch (IOException e) {
            System.out.println("Ошибка при чтении логов: " + e.getMessage());
        }
    }

    private void clearLogs() {
        try {
            loggerService.clearLogs();
        } catch (IOException e) {
            System.out.println("Ошибка при очистке логов: " + e.getMessage());
        }
    }

    private void logout() {
        try {
            if (currentUser != null) {
                loggerService.logLogout(currentUser.getUsername());
            }
        } catch (IOException e) {
            System.out.println("Ошибка при записи лога: " + e.getMessage());
        }
        currentUser = null;
        clearConsole();
        System.out.println("Вы вышли из аккаунта");

    }

    private void exit() {
        try {
            if (currentUser != null) {
                loggerService.logLogout(currentUser.getUsername());
            }
        } catch (IOException e) {
            System.out.println("Ошибка при записи лога: " + e.getMessage());
        }
        System.out.println("До свидания!");
        System.exit(0);
    }

    @SuppressWarnings("unused")
    private void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Если не получилось очистить консоль, просто выводим много пустых строк
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

}