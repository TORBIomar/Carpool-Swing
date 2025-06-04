package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.security.MessageDigest;
import java.util.Base64;
import com.formdev.flatlaf.FlatDarkLaf;
import utils.AuthUtils;
import utils.UserInfo;

public class RegistrationForm extends JFrame {
    private JTextField nameField, emailField, phoneField;
    private JPasswordField passwordField;
    private JRadioButton driverRadio, passengerRadio, bothRadio;
    private ButtonGroup userTypeGroup;
    private JButton registerButton, backButton;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/carpool";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public RegistrationForm() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();

        setTitle("Carpooling - Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initializeComponents() {
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        phoneField = new JTextField(20);

        driverRadio = new JRadioButton("Driver");
        passengerRadio = new JRadioButton("Passenger", true);
        bothRadio = new JRadioButton("Both");
        userTypeGroup = new ButtonGroup();
        userTypeGroup.add(passengerRadio);
        userTypeGroup.add(driverRadio);
        userTypeGroup.add(bothRadio);

        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, gbc);

        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("I am a:"), gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JPanel userTypePanel = new JPanel();
        userTypePanel.setLayout(new BoxLayout(userTypePanel, BoxLayout.Y_AXIS));
        userTypePanel.add(passengerRadio);
        userTypePanel.add(driverRadio);
        userTypePanel.add(bothRadio);
        add(userTypePanel, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        add(buttonPanel, gbc);
    }

    private void setupEventHandlers() {
        registerButton.addActionListener(e -> handleRegistration());
        backButton.addActionListener(e -> {
            new LoginForm().setVisible(true);
            dispose();
        });
    }

    private void handleRegistration() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String phone = phoneField.getText().trim();

        String userType = passengerRadio.isSelected() ? "PASSENGER" :
                driverRadio.isSelected() ? "DRIVER" : "BOTH";

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in required fields (Name, Email, Password)!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            UserInfo user = registerUser(name, email, password, phone, userType);
            if (user != null) {
                AuthUtils.getInstance().setCurrentUser(user);
                JOptionPane.showMessageDialog(this,
                        "Registration successful!\nName: " + name + "\nUser Type: " + userType,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.invokeLater(() -> {
                    if ("DRIVER".equalsIgnoreCase(userType) || "BOTH".equalsIgnoreCase(userType)) {
                        new DriverDashboard(user).setVisible(true);
                    } else {
                        new PassengerDashboard(user).setVisible(true);
                    }
                    dispose();
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private UserInfo registerUser(String name, String email, String password, String phone, String userType) throws Exception {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String checkSql = "SELECT COUNT(*) FROM users WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                pstmt.setString(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        throw new Exception("Email already exists!");
                    }
                }
            }

            String hashedPassword = hashPassword(password);
            String insertSql = "INSERT INTO users (name, email, password, phone, user_type, application_status) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, hashedPassword);
                pstmt.setString(4, phone.isEmpty() ? null : phone);
                pstmt.setString(5, userType);
                pstmt.setString(6, "DRIVER".equals(userType) || "BOTH".equals(userType) ? "NOT_APPLIED" : "APPROVED");
                int result = pstmt.executeUpdate();
                if (result > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            return new UserInfo(rs.getInt(1), name, userType);
                        }
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

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    public static void main(String[] args) {
        try {
            FlatDarkLaf.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new RegistrationForm().setVisible(true));
    }
}