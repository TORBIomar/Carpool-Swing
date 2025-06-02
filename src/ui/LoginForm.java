package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;
import java.util.Base64;

public class LoginForm extends JFrame {
    private JLabel emailLabel, passwordLabel;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, exitButton;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/carpool";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public LoginForm() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();

        setTitle("Carpool App - Login");
        setSize(400, 250);
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
        exitButton = new JButton("Exit");
    }

    private void setupLayout() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
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
                    SwingUtilities.invokeLater(() -> {
                        String role = user.getRole();
                        if ("ADMIN".equalsIgnoreCase(role)) {
                            AdminDashboard adminDash = new AdminDashboard(user);
                            adminDash.setVisible(true);
                        } else if ("DRIVER".equalsIgnoreCase(role) || "BOTH".equalsIgnoreCase(role)) {
                            DriverDashboard driverDash = new DriverDashboard(user);
                            driverDash.setVisible(true);
                        } else {
                            PassengerDashboard passengerDash = new PassengerDashboard(user);
                            passengerDash.setVisible(true);
                        }
                    });
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid email or password.", 
                            "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Login error: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        exitButton.addActionListener(e -> System.exit(0));

        passwordField.addActionListener(e -> loginButton.doClick());
    }

    private UserInfo authenticateUser(String email, String password) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "SELECT id, name, user_type AS role, password FROM users WHERE email = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                String dbPassword = rs.getString("password");
                String enteredHashed = hashPassword(password);
                if (enteredHashed.equals(dbPassword)) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String role = rs.getString("role");
                    return new UserInfo(id, name, role);
                }
            }
            return null;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException ex) {}
            if (conn != null) try { conn.close(); } catch (SQLException ex) {}
        }
    }

    private String hashPassword(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(hash);
    }

    public static class UserInfo {
        private final int id;
        private final String name;
        private final String role;

        public UserInfo(int id, String name, String role) {
            this.id = id;
            this.name = name;
            this.role = role;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getRole() {
            return role;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginForm login = new LoginForm();
            login.setVisible(true);
        });
    }
}