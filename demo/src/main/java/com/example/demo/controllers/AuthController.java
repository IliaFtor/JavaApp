package com.example.demo.controllers;

import com.example.demo.models.User;
import com.example.demo.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Простая форма регистрации
    @PostMapping("/register")
    public String register(@RequestParam String username,
                          @RequestParam String email,
                          @RequestParam String password) throws IOException {
        
        // Генерируем ID (простая логика)
        long newId = userService.getAllUsers().size() + 1L;
        
        User user = new User(newId, username, email, password, "USER");
        userService.saveUser(user);
        
        return "redirect:/login?registered=true";
    }

    // Простая форма логина
    @PostMapping("/login")
    public String login(@RequestParam String username,
                       @RequestParam String password) throws IOException {
        
        if (userService.validateUser(username, password)) {
            return "redirect:/dashboard?username=" + username;
        }
        
        return "redirect:/login?error=true";
    }
}