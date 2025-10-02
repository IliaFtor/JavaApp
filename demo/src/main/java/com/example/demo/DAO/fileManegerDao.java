package com.example.demo.DAO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.models.User;

public class fileManegerDao {
    private static final String USERS_FILE = "users.json";

    // Сохранить пользователя в файл
    public void saveUser(User user) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE, true))) {
            writer.println(user.toJson());
        }
    }

    // Получить всех пользователей
    public List<User> getAllUsers() throws IOException {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        
        if (!file.exists()) {
            return users;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                users.add(User.fromJson(line));
            }
        }
        return users;
    }
    public boolean isAdmin(User user) {
        return user != null && "ADMIN".equals(user.getRole());
    }
    // Найти пользователя по username
    public User findByUsername(String username) throws IOException {
        List<User> users = getAllUsers();
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}
