package com.example.demo.templates;

import com.example.demo.models.*;
import com.example.demo.services.LoggerService;
import com.example.demo.services.UserService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

@Component
public class SwingGUIView {
    private final UserService userService;
    private final UserRepository userRepository; // ← заменяем fileManegerDao
    private final LoggerService loggerService;
    private User currentUser;

    // GUI компоненты (без изменений)
    private JFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel mainMenuPanel, loginPanel, registerPanel, userPanel, changePassPanel;
    private JTextField loginUsername, regUsername, regEmail;
    private JPasswordField loginPassword, regPassword, regConfirmPassword, currentPass, newPass, confirmNewPass;
    private JLabel userInfoLabel;
    private JButton loginBtn, registerBtn, showUsersBtn, exitBtn,
                    loginSubmitBtn, loginBackBtn,
                    regSubmitBtn, regBackBtn,
                    changePassBtn, showUsersBtn2, showLogsBtn, clearLogsBtn, logoutBtn,
                    changePassSubmitBtn, changePassBackBtn;

    public SwingGUIView(UserService userService, UserRepository userRepository, LoggerService loggerService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.loggerService = loggerService;
        this.currentUser = null;
    }

    public void start() {
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("ERROR: Running in headless mode. GUI cannot be initialized.");
            return;
        }
        SwingUtilities.invokeLater(this::initializeGUI);
    }

    private void initializeGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored2) {}
        }

        mainFrame = new JFrame("Система авторизации");
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.setSize(500, 400);
        mainFrame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        createMainMenuPanel();
        createLoginPanel();
        createRegisterPanel();
        createUserPanel();
        createChangePasswordPanel();

        cardPanel.add(mainMenuPanel, "MAIN");
        cardPanel.add(loginPanel, "LOGIN");
        cardPanel.add(registerPanel, "REGISTER");
        cardPanel.add(userPanel, "USER");
        cardPanel.add(changePassPanel, "CHANGE_PASS");

        mainFrame.add(cardPanel, BorderLayout.CENTER);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        mainFrame.setVisible(true);
    }

    // --- Методы создания панелей (без изменений по логике) ---
    private void createMainMenuPanel() {
        mainMenuPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        mainMenuPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        JLabel title = new JLabel("Добро пожаловать!", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        loginBtn = new JButton("Войти");
        registerBtn = new JButton("Зарегистрироваться");
        showUsersBtn = new JButton("Показать всех пользователей");
        exitBtn = new JButton("Выйти");
        styleButton(loginBtn); styleButton(registerBtn); styleButton(showUsersBtn); styleButton(exitBtn);
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
        JLabel title = new JLabel("ВХОД", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.add(new JLabel("Логин:")); loginUsername = new JTextField(20); formPanel.add(loginUsername);
        formPanel.add(new JLabel("Пароль:")); loginPassword = new JPasswordField(20); formPanel.add(loginPassword);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginSubmitBtn = new JButton("Войти"); loginBackBtn = new JButton("Назад");
        styleButton(loginSubmitBtn); styleButton(loginBackBtn);
        loginSubmitBtn.addActionListener(e -> login());
        loginBackBtn.addActionListener(e -> showMainPanel());
        buttonPanel.add(loginSubmitBtn); buttonPanel.add(loginBackBtn);
        loginPanel.add(title, BorderLayout.NORTH);
        loginPanel.add(formPanel, BorderLayout.CENTER);
        loginPanel.add(buttonPanel, BorderLayout.SOUTH);
        loginPassword.addActionListener(e -> login());
    }

    private void createRegisterPanel() {
        registerPanel = new JPanel(new BorderLayout(10, 10));
        registerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        JLabel title = new JLabel("РЕГИСТРАЦИЯ", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.add(new JLabel("Логин:")); regUsername = new JTextField(20); formPanel.add(regUsername);
        formPanel.add(new JLabel("Email:")); regEmail = new JTextField(20); formPanel.add(regEmail);
        formPanel.add(new JLabel("Пароль:")); regPassword = new JPasswordField(20); formPanel.add(regPassword);
        formPanel.add(new JLabel("Подтвердите пароль:")); regConfirmPassword = new JPasswordField(20); formPanel.add(regConfirmPassword);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        regSubmitBtn = new JButton("Зарегистрироваться"); regBackBtn = new JButton("Назад");
        styleButton(regSubmitBtn); styleButton(regBackBtn);
        regSubmitBtn.addActionListener(e -> register());
        regBackBtn.addActionListener(e -> showMainPanel());
        buttonPanel.add(regSubmitBtn); buttonPanel.add(regBackBtn);
        registerPanel.add(title, BorderLayout.NORTH);
        registerPanel.add(formPanel, BorderLayout.CENTER);
        registerPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void createUserPanel() {
        userPanel = new JPanel(new BorderLayout(10, 10));
        userPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        userInfoLabel = new JLabel("", JLabel.CENTER);
        userInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(new JLabel("Личный кабинет", JLabel.CENTER));
        infoPanel.add(userInfoLabel);
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        changePassBtn = new JButton("Сменить пароль");
        showUsersBtn2 = new JButton("Показать всех пользователей");
        showLogsBtn = new JButton("Показать логи");
        clearLogsBtn = new JButton("Очистить логи");
        logoutBtn = new JButton("Выйти из аккаунта");
        styleButton(changePassBtn); styleButton(showUsersBtn2); styleButton(showLogsBtn); styleButton(clearLogsBtn); styleButton(logoutBtn);
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
        formPanel.add(new JLabel("Текущий пароль:")); currentPass = new JPasswordField(20); formPanel.add(currentPass);
        formPanel.add(new JLabel("Новый пароль:")); newPass = new JPasswordField(20); formPanel.add(newPass);
        formPanel.add(new JLabel("Подтвердите пароль:")); confirmNewPass = new JPasswordField(20); formPanel.add(confirmNewPass);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        changePassSubmitBtn = new JButton("Сменить"); changePassBackBtn = new JButton("Назад");
        styleButton(changePassSubmitBtn); styleButton(changePassBackBtn);
        changePassSubmitBtn.addActionListener(e -> changePassword());
        changePassBackBtn.addActionListener(e -> showUserPanel());
        buttonPanel.add(changePassSubmitBtn); buttonPanel.add(changePassBackBtn);
        changePassPanel.add(title, BorderLayout.NORTH);
        changePassPanel.add(formPanel, BorderLayout.CENTER);
        changePassPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    // --- Навигация ---
    private void showMainPanel() { cardLayout.show(cardPanel, "MAIN"); clearAllFields(); }
    private void showLoginPanel() { cardLayout.show(cardPanel, "LOGIN"); loginUsername.setText(""); loginPassword.setText(""); }
    private void showRegisterPanel() { cardLayout.show(cardPanel, "REGISTER"); regUsername.setText(""); regEmail.setText(""); regPassword.setText(""); regConfirmPassword.setText(""); }
    private void showUserPanel() {
        if (currentUser != null) {
            userInfoLabel.setText("<html>Пользователь: " + currentUser.getUsername() +
                                "<br>Email: " + currentUser.getEmail() + "</html>");
            cardLayout.show(cardPanel, "USER");
        }
    }
    private void showChangePasswordPanel() { cardLayout.show(cardPanel, "CHANGE_PASS"); currentPass.setText(""); newPass.setText(""); confirmNewPass.setText(""); }

    // --- Бизнес-логика (обновлена под JPA) ---
    private void login() {
        String username = loginUsername.getText().trim();
        String password = new String(loginPassword.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Заполните все поля!", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (userService.validateUser(username, password)) {
            currentUser = userRepository.findByUsername(username).orElse(null);
            if (currentUser != null) {
                try {
                    loggerService.logLoginSuccess(username);
                } catch (Exception ignored) {}
                showUserPanel();
                JOptionPane.showMessageDialog(mainFrame, "Вход выполнен успешно!");
            }
        } else {
            try {
                loggerService.logLoginFailure(username, "Invalid credentials");
            } catch (Exception ignored) {}
            JOptionPane.showMessageDialog(mainFrame, "Неверный логин или пароль!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

private void register() {
    String username = regUsername.getText().trim();
    String email = regEmail.getText().trim();
    String password = new String(regPassword.getPassword());
    String confirmPassword = new String(regConfirmPassword.getPassword());

    if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(mainFrame, "Все поля обязательны!", "Ошибка", JOptionPane.WARNING_MESSAGE);
        return;
    }
    if (!password.equals(confirmPassword)) {
        JOptionPane.showMessageDialog(mainFrame, "Пароли не совпадают!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        userService.registerUser(username, email, password);
        loggerService.logRegistration(username);
        showMainPanel();
        JOptionPane.showMessageDialog(mainFrame, "Регистрация прошла успешно!");
    } catch (RuntimeException e) {
        JOptionPane.showMessageDialog(mainFrame, e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(mainFrame, "Ошибка регистрации: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
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
        userRepository.save(currentUser);
        try {
            //loggerService.logPasswordChange(currentUser.getUser_name());
        } catch (Exception ignored) {}
        showUserPanel();
        JOptionPane.showMessageDialog(mainFrame, "Пароль успешно изменён!");
    }

    private void showAllUsersDialog() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Пользователей нет");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("=== ПОЛЬЗОВАТЕЛИ ===\n\n");
        for (User u : users) {
            sb.append("• ").append(u.getUsername()).append(" (").append(u.getEmail()).append(")\n");
        }
        sb.append("\nВсего: ").append(users.size());
        JTextArea textArea = new JTextArea(sb.toString(), 15, 40);
        textArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(mainFrame, scroll, "Список пользователей", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showLogsDialog() {
        try {
            String logs = loggerService.getLogsAsString();
            JTextArea area = new JTextArea(logs, 20, 50);
            area.setEditable(false);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            JScrollPane scroll = new JScrollPane(area);
            scroll.setPreferredSize(new Dimension(600, 400));
            JOptionPane.showMessageDialog(mainFrame, scroll, "Логи", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame, "Ошибка чтения логов: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearLogs() {
        int res = JOptionPane.showConfirmDialog(mainFrame, "Очистить все логи?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            try {
                loggerService.clearLogs();
                JOptionPane.showMessageDialog(mainFrame, "Логи очищены!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(mainFrame, "Ошибка очистки: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        try {
            if (currentUser != null) {
                //loggerService.logLogout(currentUser.getUser_name());
            }
        } catch (Exception ignored) {}
        currentUser = null;
        showMainPanel();
        JOptionPane.showMessageDialog(mainFrame, "Вы вышли из аккаунта");
    }

    private void exit() {
        int res = JOptionPane.showConfirmDialog(mainFrame, "Выйти из программы?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            try {
                if (currentUser != null) {
                    //loggerService.logLogout(currentUser.getUser_name());
                }
            } catch (Exception ignored) {}
            System.exit(0);
        }
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(70, 130, 180));
        btn.setForeground(Color.black);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
    }

    private void clearAllFields() {
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