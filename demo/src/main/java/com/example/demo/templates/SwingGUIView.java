package com.example.demo.templates;

import com.example.demo.DAO.fileManegerDao;
import com.example.demo.models.User;
import com.example.demo.services.LoggerService;
import com.example.demo.services.UserService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

@Component
public class SwingGUIView {
    private final UserService userService;
    private fileManegerDao iDao = new fileManegerDao();
    private final LoggerService loggerService;
    private User currentUser;
    
    private JFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    // Компоненты для главного меню
    private JPanel mainMenuPanel;
    private JButton loginBtn, registerBtn, showUsersBtn, exitBtn;
    
    // Компоненты для логина
    private JPanel loginPanel;
    private JTextField loginUsername;
    private JPasswordField loginPassword;
    private JButton loginSubmitBtn, loginBackBtn;
    
    // Компоненты для регистрации
    private JPanel registerPanel;
    private JTextField regUsername, regEmail;
    private JPasswordField regPassword, regConfirmPassword;
    private JButton regSubmitBtn, regBackBtn;
    
    // Компоненты личного кабинета
    private JPanel userPanel;
    private JLabel userInfoLabel;
    private JButton changePassBtn, showUsersBtn2, showLogsBtn, clearLogsBtn, logoutBtn;
    
    // Компоненты для смены пароля
    private JPanel changePassPanel;
    private JPasswordField currentPass, newPass, confirmNewPass;
    private JButton changePassSubmitBtn, changePassBackBtn;

    public SwingGUIView(UserService userService, LoggerService loggerService) {
        this.userService = userService;
        this.loggerService = loggerService;
        this.currentUser = null;
        System.out.println("SwingGUIView constructor called");
    }

    public void start() {
        System.out.println("SwingGUIView.start() called");
        System.out.println("Headless mode: " + GraphicsEnvironment.isHeadless());
        System.out.println("Is Event Dispatch Thread: " + SwingUtilities.isEventDispatchThread());
        
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("ERROR: Running in headless mode. GUI cannot be initialized.");
            System.err.println("Add '-Djava.awt.headless=false' to VM options");
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            System.out.println("Now in Event Dispatch Thread");
            initializeGUI();
        });
    }

    private void initializeGUI() {
        System.out.println("Initializing GUI...");
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("Look and feel set successfully");
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                System.out.println("Using cross-platform look and feel");
            } catch (Exception ex) {
                System.err.println("Failed to set cross-platform look and feel: " + ex.getMessage());
            }
        }
        
        try {
            // Создание главного окна
            mainFrame = new JFrame("Система авторизации");
            mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            mainFrame.setSize(500, 400);
            mainFrame.setLocationRelativeTo(null); // Центрирование окна
            
            // Настройка CardLayout для переключения между экранами
            cardLayout = new CardLayout();
            cardPanel = new JPanel(cardLayout);
            
            // Создание панелей
            createMainMenuPanel();
            createLoginPanel();
            createRegisterPanel();
            createUserPanel();
            createChangePasswordPanel();
            
            // Добавление панелей в CardLayout
            cardPanel.add(mainMenuPanel, "MAIN");
            cardPanel.add(loginPanel, "LOGIN");
            cardPanel.add(registerPanel, "REGISTER");
            cardPanel.add(userPanel, "USER");
            cardPanel.add(changePassPanel, "CHANGE_PASS");
            
            mainFrame.add(cardPanel, BorderLayout.CENTER);
            
            // Обработчик закрытия окна
            mainFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent windowEvent) {
                    exit();
                }
            });
            
            mainFrame.setVisible(true);
            System.out.println("GUI initialized successfully - window should be visible");
            
        } catch (Exception e) {
            System.err.println("Failed to initialize GUI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createMainMenuPanel() {
        mainMenuPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        mainMenuPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        JLabel title = new JLabel("Добро пожаловать в систему авторизации!", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        
        loginBtn = new JButton("Войти");
        registerBtn = new JButton("Зарегистрироваться");
        showUsersBtn = new JButton("Показать всех пользователей");
        exitBtn = new JButton("Выйти из программы");
        
        // Стилизация кнопок
        styleButton(loginBtn);
        styleButton(registerBtn);
        styleButton(showUsersBtn);
        styleButton(exitBtn);
        
        // Обработчики событий
        loginBtn.addActionListener(e -> showLoginPanel());
        registerBtn.addActionListener(e -> showRegisterPanel());
        showUsersBtn.addActionListener(e -> showAllUsersDialog());
        exitBtn.addActionListener(e -> exit());
        
        mainMenuPanel.add(title);
        mainMenuPanel.add(loginBtn);
        mainMenuPanel.add(registerBtn);
        mainMenuPanel.add(showUsersBtn);
        mainMenuPanel.add(exitBtn);
    }

    private void createLoginPanel() {
        loginPanel = new JPanel(new BorderLayout(10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        JLabel title = new JLabel("ВХОД В СИСТЕМУ", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        formPanel.add(new JLabel("Логин:"));
        loginUsername = new JTextField(20);
        formPanel.add(loginUsername);
        
        formPanel.add(new JLabel("Пароль:"));
        loginPassword = new JPasswordField(20);
        formPanel.add(loginPassword);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginSubmitBtn = new JButton("Войти");
        loginBackBtn = new JButton("Назад");
        
        styleButton(loginSubmitBtn);
        styleButton(loginBackBtn);
        
        loginSubmitBtn.addActionListener(e -> login());
        loginBackBtn.addActionListener(e -> showMainPanel());
        
        buttonPanel.add(loginSubmitBtn);
        buttonPanel.add(loginBackBtn);
        
        loginPanel.add(title, BorderLayout.NORTH);
        loginPanel.add(formPanel, BorderLayout.CENTER);
        loginPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Добавляем обработчик нажатия Enter
        loginPassword.addActionListener(e -> login());
    }

    private void createRegisterPanel() {
        registerPanel = new JPanel(new BorderLayout(10, 10));
        registerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        JLabel title = new JLabel("РЕГИСТРАЦИЯ", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        
        formPanel.add(new JLabel("Логин:"));
        regUsername = new JTextField(20);
        formPanel.add(regUsername);
        
        formPanel.add(new JLabel("Email:"));
        regEmail = new JTextField(20);
        formPanel.add(regEmail);
        
        formPanel.add(new JLabel("Пароль:"));
        regPassword = new JPasswordField(20);
        formPanel.add(regPassword);
        
        formPanel.add(new JLabel("Подтвердите пароль:"));
        regConfirmPassword = new JPasswordField(20);
        formPanel.add(regConfirmPassword);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        regSubmitBtn = new JButton("Зарегистрироваться");
        regBackBtn = new JButton("Назад");
        
        styleButton(regSubmitBtn);
        styleButton(regBackBtn);
        
        regSubmitBtn.addActionListener(e -> register());
        regBackBtn.addActionListener(e -> showMainPanel());
        
        buttonPanel.add(regSubmitBtn);
        buttonPanel.add(regBackBtn);
        
        registerPanel.add(title, BorderLayout.NORTH);
        registerPanel.add(formPanel, BorderLayout.CENTER);
        registerPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void createUserPanel() {
        userPanel = new JPanel(new BorderLayout(10, 10));
        userPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        // Панель информации о пользователе
        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        userInfoLabel = new JLabel("", JLabel.CENTER);
        userInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(userInfoLabel);
        
        // Панель кнопок
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        
        changePassBtn = new JButton("Сменить пароль");
        showUsersBtn2 = new JButton("Показать всех пользователей");
        showLogsBtn = new JButton("Показать логи");
        clearLogsBtn = new JButton("Очистить логи");
        logoutBtn = new JButton("Выйти из аккаунта");
        
        // Стилизация кнопок
        styleButton(changePassBtn);
        styleButton(showUsersBtn2);
        styleButton(showLogsBtn);
        styleButton(clearLogsBtn);
        styleButton(logoutBtn);
        
        changePassBtn.addActionListener(e -> showChangePasswordPanel());
        showUsersBtn2.addActionListener(e -> showAllUsersDialog());
        showLogsBtn.addActionListener(e -> showLogsDialog());
        clearLogsBtn.addActionListener(e -> clearLogs());
        logoutBtn.addActionListener(e -> logout());
        
        buttonPanel.add(changePassBtn);
        buttonPanel.add(showUsersBtn2);
        buttonPanel.add(showLogsBtn);
        buttonPanel.add(clearLogsBtn);
        buttonPanel.add(logoutBtn);
        
        userPanel.add(infoPanel, BorderLayout.NORTH);
        userPanel.add(buttonPanel, BorderLayout.CENTER);
    }

    private void createChangePasswordPanel() {
        changePassPanel = new JPanel(new BorderLayout(10, 10));
        changePassPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        JLabel title = new JLabel("СМЕНА ПАРОЛЯ", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        
        formPanel.add(new JLabel("Текущий пароль:"));
        currentPass = new JPasswordField(20);
        formPanel.add(currentPass);
        
        formPanel.add(new JLabel("Новый пароль:"));
        newPass = new JPasswordField(20);
        formPanel.add(newPass);
        
        formPanel.add(new JLabel("Подтвердите пароль:"));
        confirmNewPass = new JPasswordField(20);
        formPanel.add(confirmNewPass);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        changePassSubmitBtn = new JButton("Сменить пароль");
        changePassBackBtn = new JButton("Назад");
        
        styleButton(changePassSubmitBtn);
        styleButton(changePassBackBtn);
        
        changePassSubmitBtn.addActionListener(e -> changePassword());
        changePassBackBtn.addActionListener(e -> showUserPanel());
        
        buttonPanel.add(changePassSubmitBtn);
        buttonPanel.add(changePassBackBtn);
        
        changePassPanel.add(title, BorderLayout.NORTH);
        changePassPanel.add(formPanel, BorderLayout.CENTER);
        changePassPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    // Методы для навигации
    private void showMainPanel() {
        cardLayout.show(cardPanel, "MAIN");
        clearAllFields();
    }

    private void showLoginPanel() {
        cardLayout.show(cardPanel, "LOGIN");
        loginUsername.setText("");
        loginPassword.setText("");
    }

    private void showRegisterPanel() {
        cardLayout.show(cardPanel, "REGISTER");
        regUsername.setText("");
        regEmail.setText("");
        regPassword.setText("");
        regConfirmPassword.setText("");
    }

    private void showUserPanel() {
        if (currentUser != null) {
            userInfoLabel.setText("<html>Пользователь: " + currentUser.getUsername() + 
                                "<br>Email: " + currentUser.getEmail() + 
                                "<br>Роль: " + currentUser.getRole() + "</html>");
            
            // Показываем/скрываем кнопки админа
            boolean isAdmin = iDao.isAdmin(currentUser);
            showLogsBtn.setVisible(isAdmin);
            clearLogsBtn.setVisible(isAdmin);
            
            cardLayout.show(cardPanel, "USER");
        }
    }

    private void showChangePasswordPanel() {
        cardLayout.show(cardPanel, "CHANGE_PASS");
        currentPass.setText("");
        newPass.setText("");
        confirmNewPass.setText("");
    }

    // Бизнес-логика
    private void login() {
        String username = loginUsername.getText();
        String password = new String(loginPassword.getPassword());

        try {
            if (userService.validateUser(username, password)) {
                currentUser = iDao.findByUsername(username);
                loggerService.logLoginSuccess(username);
                showUserPanel();
                JOptionPane.showMessageDialog(mainFrame, "Вход выполнен успешно!");
            } else {
                loggerService.logLoginFailure(username, "Invalid credentials");
                JOptionPane.showMessageDialog(mainFrame, "Неверный логин или пароль!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Ошибка при чтении данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void register() {
        String username = regUsername.getText();
        String email = regEmail.getText();
        String password = new String(regPassword.getPassword());
        String confirmPassword = new String(regConfirmPassword.getPassword());

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(mainFrame, "Пароли не совпадают!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (iDao.findByUsername(username) != null) {
                JOptionPane.showMessageDialog(mainFrame, "Пользователь с таким логином уже существует!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            long newId = iDao.getAllUsers().size() + 1L;
            User newUser = new User(newId, username, email, password, "USER");

            iDao.saveUser(newUser);
            loggerService.logRegistration(username);
            showMainPanel();
            JOptionPane.showMessageDialog(mainFrame, "Регистрация прошла успешно!");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Ошибка при сохранении данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changePassword() {
        String currentPassword = new String(currentPass.getPassword());
        String newPassword = new String(newPass.getPassword());
        String confirmPassword = new String(confirmNewPass.getPassword());

        if (!currentUser.getPassword().equals(currentPassword)) {
            JOptionPane.showMessageDialog(mainFrame, "Неверный текущий пароль!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(mainFrame, "Пароли не совпадают!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentUser.setPassword(newPassword);
        try {
            loggerService.logPasswordChange(currentUser.getUsername());
            showUserPanel();
            JOptionPane.showMessageDialog(mainFrame, "Пароль успешно изменен!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Ошибка при записи лога: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAllUsersDialog() {
        try {
            List<User> users = iDao.getAllUsers();
            
            if (users.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Пользователей нет");
                return;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("=== ПОЛЬЗОВАТЕЛИ ===\n\n");
            
            for (User user : users) {
                String status = user.isEnabled() ? "Активен" : "Неактивен";
                sb.append(user.getUsername())
                  .append(" (").append(user.getEmail()).append(") - ")
                  .append(user.getRole()).append(" - ").append(status)
                  .append("\n");
            }
            sb.append("\nВсего: ").append(users.size());
            
            JTextArea textArea = new JTextArea(sb.toString(), 15, 40);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            
            JOptionPane.showMessageDialog(mainFrame, scrollPane, "Список пользователей", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Ошибка при чтении данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showLogsDialog() {
        try {
            String logs = loggerService.getLogsAsString();
            JTextArea textArea = new JTextArea(logs, 20, 50);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));
            
            JOptionPane.showMessageDialog(mainFrame, scrollPane, "Логи системы", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Ошибка при чтении логов: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearLogs() {
        int result = JOptionPane.showConfirmDialog(mainFrame, 
            "Вы уверены, что хотите очистить все логи?", 
            "Подтверждение", 
            JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            try {
                loggerService.clearLogs();
                JOptionPane.showMessageDialog(mainFrame, "Логи успешно очищены!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(mainFrame, "Ошибка при очистке логов: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        try {
            if (currentUser != null) {
                loggerService.logLogout(currentUser.getUsername());
            }
        } catch (IOException e) {
            // Игнорируем ошибки при выходе
        }
        currentUser = null;
        showMainPanel();
        JOptionPane.showMessageDialog(mainFrame, "Вы вышли из аккаунта");
    }

    private void exit() {
        int result = JOptionPane.showConfirmDialog(mainFrame, 
            "Вы уверены, что хотите выйти?", 
            "Подтверждение выхода", 
            JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            try {
                if (currentUser != null) {
                    loggerService.logLogout(currentUser.getUsername());
                }
            } catch (IOException e) {
                // Игнорируем ошибки при выходе
            }
            System.exit(0);
        }
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
    }

    private void clearAllFields() {
        // Очистка всех полей ввода
        if (loginUsername != null) loginUsername.setText("");
        if (loginPassword != null) loginPassword.setText("");
        if (regUsername != null) regUsername.setText("");
        if (regEmail != null) regEmail.setText("");
        if (regPassword != null) regPassword.setText("");
        if (regConfirmPassword != null) regConfirmPassword.setText("");
        if (currentPass != null) currentPass.setText("");
        if (newPass != null) newPass.setText("");
        if (confirmNewPass != null) confirmNewPass.setText("");
    }
}