package com.example.demo.services;

import com.example.demo.models.User;
import com.example.demo.models.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final LoggerService logger;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // ← ЖЁСТКО ЗАДАН!

    public UserService(UserRepository userRepository, LoggerService logger) {
        this.userRepository = userRepository;
        this.logger = logger;
    }

    public void registerUser(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Пользователь с таким логином уже существует");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email уже используется");
        }

        String hashedPassword = passwordEncoder.encode(password);
        logger.logDebug("Хеширование", "Пароль для " + username + " захеширован: " + hashedPassword);

        User user = new User(username, email, hashedPassword);
        userRepository.save(user);
        logger.logRegistration(username);
    }

    public boolean validateUser(String username, String rawPassword) {
        var userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            logger.logDebug("Вход", "Пользователь не найден: " + username);
            return false;
        }
        boolean matches = passwordEncoder.matches(rawPassword, userOpt.get().getPassword());
        logger.logDebug("Вход", "Пароль верный для " + username + "? " + matches);
        return matches;
    }
}