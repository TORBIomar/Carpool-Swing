package ui;

import javax.swing.*;
import java.awt.*;
<<<<<<< HEAD
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
=======
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
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a

    public RegistrationForm() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
<<<<<<< HEAD

=======
        
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
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
<<<<<<< HEAD

        driverRadio = new JRadioButton("Driver");
        passengerRadio = new JRadioButton("Passenger", true);
        bothRadio = new JRadioButton("Both");
        userTypeGroup = new ButtonGroup();
        userTypeGroup.add(passengerRadio);
        userTypeGroup.add(driverRadio);
        userTypeGroup.add(bothRadio);

=======
        
        // User type selection
        driverRadio = new JRadioButton("Driver");
        passengerRadio = new JRadioButton("Passenger", true);
        bothRadio = new JRadioButton("Both Driver & Passenger");
        userTypeGroup = new ButtonGroup();
        userTypeGroup.add(driverRadio);
        userTypeGroup.add(passengerRadio);
        userTypeGroup.add(bothRadio);
        
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

<<<<<<< HEAD
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
=======
        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, gbc);

<<<<<<< HEAD
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;

=======
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Name
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        add(nameField, gbc);

<<<<<<< HEAD
=======
        // Email
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        add(emailField, gbc);

<<<<<<< HEAD
=======
        // Password
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        add(passwordField, gbc);

<<<<<<< HEAD
=======
        // Phone
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        add(phoneField, gbc);

<<<<<<< HEAD
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("I am a:"), gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
=======
        // User Type
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("I am a:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        JPanel userTypePanel = new JPanel();
        userTypePanel.setLayout(new BoxLayout(userTypePanel, BoxLayout.Y_AXIS));
        userTypePanel.add(passengerRadio);
        userTypePanel.add(driverRadio);
        userTypePanel.add(bothRadio);
        add(userTypePanel, gbc);

<<<<<<< HEAD
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
=======
        // Buttons
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        add(buttonPanel, gbc);
    }

    private void setupEventHandlers() {
<<<<<<< HEAD
        registerButton.addActionListener(e -> handleRegistration());
        backButton.addActionListener(e -> {
            new LoginForm().setVisible(true);
            dispose();
=======
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
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        });
    }

    private void handleRegistration() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String phone = phoneField.getText().trim();
<<<<<<< HEAD

        String userType = passengerRadio.isSelected() ? "PASSENGER" :
                driverRadio.isSelected() ? "DRIVER" : "BOTH";

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in required fields (Name, Email, Password)!",
                    "Error", JOptionPane.ERROR_MESSAGE);
=======
        
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
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            return;
        }

        if (!isValidEmail(email)) {
<<<<<<< HEAD
            JOptionPane.showMessageDialog(this, "Please enter a valid email address!",
                    "Error", JOptionPane.ERROR_MESSAGE);
=======
            JOptionPane.showMessageDialog(this, "Please enter a valid email address!", 
                "Error", JOptionPane.ERROR_MESSAGE);
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            return;
        }

        try {
<<<<<<< HEAD
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

=======
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
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
    private String hashPassword(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(hash);
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    public static void main(String[] args) {
<<<<<<< HEAD
        try {
            FlatDarkLaf.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new RegistrationForm().setVisible(true));
=======
        SwingUtilities.invokeLater(() -> {
           try {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
} catch (Exception e) {
    e.printStackTrace();
            }
            new RegistrationForm().setVisible(true);
        });
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
    }
}