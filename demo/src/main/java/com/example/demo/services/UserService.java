package com.example.demo.services;

import com.example.demo.models.User;
import com.example.demo.DAO.fileManegerDao;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private fileManegerDao iDao = new fileManegerDao();
    // Проверить логин и пароль
    public boolean validateUser(String username, String password) throws IOException {
        User user = iDao.findByUsername(username);
        return user != null && user.getPassword().equals(password) && user.isEnabled();
    }
}