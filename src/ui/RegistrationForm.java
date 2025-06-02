package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.security.MessageDigest;
import java.util.Base64;
import ui.LoginForm;
import javax.swing.UIManager;

public class RegistrationForm extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField phoneField;
    private JRadioButton driverRadio;
    private JRadioButton passengerRadio;
    private JRadioButton bothRadio;
    private ButtonGroup userTypeGroup;
    private JButton registerButton;
    private JButton backButton;

    // Database connection details - update these according to your setup
    private static final String DB_URL = "jdbc:mysql://localhost:3306/carpool";
    private static final String DB_USER = "root"; // Your DB username
    private static final String DB_PASSWORD = ""; // Your DB password

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
        
        // User type selection
        driverRadio = new JRadioButton("Driver");
        passengerRadio = new JRadioButton("Passenger", true);
        bothRadio = new JRadioButton("Both Driver & Passenger");
        userTypeGroup = new ButtonGroup();
        userTypeGroup.add(driverRadio);
        userTypeGroup.add(passengerRadio);
        userTypeGroup.add(bothRadio);
        
        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Name
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        add(nameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        add(emailField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        add(phoneField, gbc);

        // User Type
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("I am a:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JPanel userTypePanel = new JPanel();
        userTypePanel.setLayout(new BoxLayout(userTypePanel, BoxLayout.Y_AXIS));
        userTypePanel.add(passengerRadio);
        userTypePanel.add(driverRadio);
        userTypePanel.add(bothRadio);
        add(userTypePanel, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        add(buttonPanel, gbc);
    }

    private void setupEventHandlers() {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Navigate back to login
                new LoginForm().setVisible(true);
                dispose();
            }
        });
    }

    private void handleRegistration() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String phone = phoneField.getText().trim();
        
        String userType = "PASSENGER"; // default
        if (driverRadio.isSelected()) {
            userType = "DRIVER";
        } else if (bothRadio.isSelected()) {
            userType = "BOTH";
        }

        // Validation
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
            if (registerUser(name, email, password, phone, userType)) {
                JOptionPane.showMessageDialog(this, 
                    "Registration successful!\nName: " + name + "\nUser Type: " + userType, 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Navigate to login
                new LoginForm().setVisible(true);
                dispose();
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Registration failed: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean registerUser(String name, String email, String password, String phone, String userType) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish connection
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // Check if email already exists
            String checkSql = "SELECT COUNT(*) FROM users WHERE email = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            
            if (rs.getInt(1) > 0) {
                throw new Exception("Email already exists!");
            }
            
            // Hash the password (using the same method as your existing data)
            String hashedPassword = hashPassword(password);
            
            // Insert new user
            String insertSql = "INSERT INTO users (name, email, password, phone, user_type) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, phone.isEmpty() ? null : phone);
            pstmt.setString(5, userType);
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    // Hash password using SHA-256 and Base64 encoding (matches your existing data format)
    private String hashPassword(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(hash);
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
           try {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
} catch (Exception e) {
    e.printStackTrace();
            }
            new RegistrationForm().setVisible(true);
        });
    }
}