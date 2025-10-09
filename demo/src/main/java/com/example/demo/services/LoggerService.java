package com.example.demo.services;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

@Service
public class LoggerService {

    private static final String LOGS_FILE = "auth_logs.json";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public void info(String message, String username, String email) {
        try {
            String logEntry = String.format(
                "{\"timestamp\":\"%s\",\"level\":\"INFO\",\"username\":\"%s\",\"email\":\"%s\",\"message\":\"%s\"}",
                LocalDateTime.now().format(formatter),
                username != null ? username : "N/A",
                email != null ? email : "N/A",
                message != null ? message : ""
            );
            writeLog(logEntry);
        } catch (IOException e) {
            System.err.println("Не удалось записать INFO-лог: " + e.getMessage());
        }
    }

    public void debug(String message, String detail) {
        try {
            String logEntry = String.format(
                "{\"timestamp\":\"%s\",\"level\":\"DEBUG\",\"detail\":\"%s\",\"message\":\"%s\"}",
                LocalDateTime.now().format(formatter),
                detail != null ? detail : "N/A",
                message != null ? message : ""
            );
            writeLog(logEntry);
        } catch (IOException e) {
            System.err.println("Не удалось записать DEBUG-лог: " + e.getMessage());
        }
    }

    public void warn(String message, String username) {
        try {
            String logEntry = String.format(
                "{\"timestamp\":\"%s\",\"level\":\"WARN\",\"username\":\"%s\",\"message\":\"%s\"}",
                LocalDateTime.now().format(formatter),
                username != null ? username : "N/A",
                message != null ? message : ""
            );
            writeLog(logEntry);
        } catch (IOException e) {
            System.err.println("Не удалось записать WARN-лог: " + e.getMessage());
        }
    }

    // --- Специфичные методы аудита ---

    public void logLoginSuccess(String username) {
        try {
            logEvent("LOGIN_SUCCESS", username, null, "User logged in successfully");
        } catch (IOException e) {
            System.err.println("Ошибка логирования: " + e.getMessage());
        }
    }

    public void logLoginFailure(String username, String reason) {
        try {
            logEvent("LOGIN_FAILURE", username, reason, "Login attempt failed");
        } catch (IOException e) {
            System.err.println("Ошибка логирования: " + e.getMessage());
        }
    }

    public void logRegistration(String username) {
        try {
            logEvent("REGISTRATION", username, null, "New user registered");
        } catch (IOException e) {
            System.err.println("Ошибка логирования: " + e.getMessage());
        }
    }

    public void logPasswordChange(String username) {
        try {
            logEvent("PASSWORD_CHANGE", username, null, "User changed password");
        } catch (IOException e) {
            System.err.println("Ошибка логирования: " + e.getMessage());
        }
    }

    public void logLogout(String username) {
        try {
            logEvent("LOGOUT", username, null, "User logged out");
        } catch (IOException e) {
            System.err.println("Ошибка логирования: " + e.getMessage());
        }
    }

    public void logError(String event, String username, String errorMessage) {
        try {
            logEvent(event, username, errorMessage, "System error occurred");
        } catch (IOException e) {
            System.err.println("Ошибка логирования: " + e.getMessage());
        }
    }


    private void logEvent(String event, String username, String extra, String message) throws IOException {
        StringBuilder entry = new StringBuilder();
        entry.append("{");
        entry.append("\"timestamp\":\"").append(LocalDateTime.now().format(formatter)).append("\",");
        entry.append("\"event\":\"").append(event).append("\",");
        if (username != null) {
            entry.append("\"username\":\"").append(username).append("\",");
        }
        if (extra != null) {
            String key = event.equals("LOGIN_FAILURE") ? "reason" : 
                         event.equals("ERROR") ? "error" : "detail";
            entry.append("\"").append(key).append("\":\"").append(extra).append("\",");
        }
        entry.append("\"message\":\"").append(message).append("\"");
        entry.append("}");
        writeLog(entry.toString());
    }

    private void writeLog(String logEntry) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOGS_FILE, true))) {
            writer.println(logEntry);
        }
    }

    public String getLogsAsString() throws IOException {
        File file = new File(LOGS_FILE);
        if (!file.exists()) {
            return "📭 Логов ещё нет";
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(LOGS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString().trim();
    }

    public void clearLogs() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOGS_FILE))) {
        }
    }

    public void logDebug(String category, String message) {
    try {
        String logEntry = String.format(
            "{\"timestamp\":\"%s\",\"level\":\"DEBUG\",\"category\":\"%s\",\"message\":\"%s\"}",
            LocalDateTime.now().format(formatter), category, message);
        writeLog(logEntry);
    } catch (IOException e) {
        e.printStackTrace();
    }

}
}