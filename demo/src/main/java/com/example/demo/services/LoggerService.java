package com.example.demo.services;

import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class LoggerService {
    private static final String LOGS_FILE = "auth_logs.json";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Логирование успешного входа
    public void logLoginSuccess(String username) throws IOException {
        String logEntry = String.format(
            "{\"timestamp\":\"%s\",\"event\":\"LOGIN_SUCCESS\",\"username\":\"%s\",\"message\":\"User logged in successfully\"}",
            LocalDateTime.now().format(formatter), username
        );
        writeLog(logEntry);
    }

    // Логирование неудачного входа
    public void logLoginFailure(String username, String reason) throws IOException {
        String logEntry = String.format(
            "{\"timestamp\":\"%s\",\"event\":\"LOGIN_FAILURE\",\"username\":\"%s\",\"reason\":\"%s\",\"message\":\"Login attempt failed\"}",
            LocalDateTime.now().format(formatter), username, reason
        );
        writeLog(logEntry);
    }

    // Логирование регистрации
    public void logRegistration(String username) throws IOException {
        String logEntry = String.format(
            "{\"timestamp\":\"%s\",\"event\":\"REGISTRATION\",\"username\":\"%s\",\"message\":\"New user registered\"}",
            LocalDateTime.now().format(formatter), username
        );
        writeLog(logEntry);
    }

    // Логирование смены пароля
    public void logPasswordChange(String username) throws IOException {
        String logEntry = String.format(
            "{\"timestamp\":\"%s\",\"event\":\"PASSWORD_CHANGE\",\"username\":\"%s\",\"message\":\"User changed password\"}",
            LocalDateTime.now().format(formatter), username
        );
        writeLog(logEntry);
    }

    // Логирование выхода
    public void logLogout(String username) throws IOException {
        String logEntry = String.format(
            "{\"timestamp\":\"%s\",\"event\":\"LOGOUT\",\"username\":\"%s\",\"message\":\"User logged out\"}",
            LocalDateTime.now().format(formatter), username
        );
        writeLog(logEntry);
    }

    // Логирование ошибок
    public void logError(String event, String username, String errorMessage) throws IOException {
        String logEntry = String.format(
            "{\"timestamp\":\"%s\",\"event\":\"%s\",\"username\":\"%s\",\"error\":\"%s\",\"message\":\"System error occurred\"}",
            LocalDateTime.now().format(formatter), event, username, errorMessage
        );
        writeLog(logEntry);
    }

    // Запись лога в файл
    private void writeLog(String logEntry) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOGS_FILE, true))) {
            writer.println(logEntry);
        }
    }

    // Просмотр логов
    public void showLogs() throws IOException {
        File file = new File(LOGS_FILE);
        
        if (!file.exists()) {
            System.out.println("📭 Логов еще нет");
            return;
        }

        System.out.println("\n=== ПОСЛЕДНИЕ ЛОГИ ===");
        try (BufferedReader reader = new BufferedReader(new FileReader(LOGS_FILE))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null && count < 10) {
                System.out.println(line);
                count++;
            }
        }
    }

    // Очистка логов
    public void clearLogs() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOGS_FILE))) {
            writer.print("");
        }
        System.out.println("✅ Логи очищены");
    }
}