package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.security.MessageDigest;
import java.util.Base64;
import com.formdev.flatlaf.FlatDarkLaf;
import utils.AuthUtils;
import utils.UserInfo;

public class LoginForm extends JFrame {
    private JLabel emailLabel, passwordLabel;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton, exitButton;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/carpool";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public LoginForm() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();

        setTitle("Carpool App - Login");
        setSize(400, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    private void initializeComponents() {
        emailLabel = new JLabel("Email:");
        passwordLabel = new JLabel("Password:");

        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        exitButton = new JButton("Exit");
    }

    private void setupLayout() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(exitButton);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both email and password.",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                UserInfo user = authenticateUser(email, password);
                if (user != null) {
                    AuthUtils.getInstance().setCurrentUser(user);
                    SwingUtilities.invokeLater(() -> {
                        String role = user.getRole();
                        if ("ADMIN".equalsIgnoreCase(role)) {
                            new AdminDashboard(user).setVisible(true);
                        } else if ("DRIVER".equalsIgnoreCase(role) || "BOTH".equalsIgnoreCase(role)) {
                            new DriverDashboard(user).setVisible(true);
                        } else {
                            new PassengerDashboard(user).setVisible(true);
                        }
                        dispose();
                    });
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid email or password.",
                            "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Login error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> {
            new RegistrationForm().setVisible(true);
            dispose();
        });

        exitButton.addActionListener(e -> System.exit(0));

        passwordField.addActionListener(e -> loginButton.doClick());
    }

    private UserInfo authenticateUser(String email, String password) throws Exception {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT id, name, user_type AS role, password FROM users WHERE email = ?")) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String dbPassword = rs.getString("password");
                    String enteredHashed = hashPassword(password);
                    if (enteredHashed.equals(dbPassword)) {
                        return new UserInfo(rs.getInt("id"), rs.getString("name"), rs.getString("role"));
                    }
                }
                return null;
            }
        }
    }

    private String hashPassword(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(hash);
    }

    public static void main(String[] args) {
        try {
            FlatDarkLaf.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}