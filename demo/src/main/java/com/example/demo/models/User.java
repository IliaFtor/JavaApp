package com.example.demo.models;
import java.util.Objects;

public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String role; // "USER", "ADMIN"
    private boolean enabled;

    // Конструкторы
    public User() {}

    public User(Long id, String username, String email, String password, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = true;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    // equals и hashCode для сравнения пользователей
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && 
               Objects.equals(username, user.username) &&
               Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", enabled=" + enabled +
                '}';
    }

    // Метод для преобразования в JSON строку (ручная сериализация)
    public String toJson() {
        return String.format(
            "{\"id\":%d,\"username\":\"%s\",\"email\":\"%s\",\"password\":\"%s\",\"role\":\"%s\",\"enabled\":%b}",
            id, username, email, password, role, enabled
        );
    }

    // Статический метод для создания User из JSON строки
    public static User fromJson(String json) {
        // Простая парсинговая логика (для демо)
        json = json.replace("{", "").replace("}", "").replace("\"", "");
        String[] pairs = json.split(",");
        
        User user = new User();
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                
                switch (key) {
                    case "id": user.setId(Long.parseLong(value)); break;
                    case "username": user.setUsername(value); break;
                    case "email": user.setEmail(value); break;
                    case "password": user.setPassword(value); break;
                    case "role": user.setRole(value); break;
                    case "enabled": user.setEnabled(Boolean.parseBoolean(value)); break;
                }
            }
        }
        return user;
    }
}