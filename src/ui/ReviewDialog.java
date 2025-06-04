package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ReviewDialog extends JDialog {
    private JComboBox<Integer> ratingCombo;
    private JTextArea commentArea;
    private JButton submitButton;
    private final int tripId, passengerId, driverId;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/carpool";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public ReviewDialog(JFrame parent, int tripId, int passengerId, int driverId) {
        super(parent, "Review Trip", true);
        this.tripId = tripId;
        this.passengerId = passengerId;
        this.driverId = driverId;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setSize(400, 300);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        ratingCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        commentArea = new JTextArea(5, 20);
        submitButton = new JButton("Submit Review");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(3, 1));
        formPanel.add(new JLabel("Rating (1-5):"));
        formPanel.add(ratingCombo);
        formPanel.add(new JLabel("Comment:"));
        formPanel.add(new JScrollPane(commentArea));
        add(formPanel, BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        submitButton.addActionListener(e -> submitReview());
    }

    private void submitReview() {
        int rating = (int) ratingCombo.getSelectedItem();
        String comment = commentArea.getText().trim();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO reviews (driver_id, passenger_id, trip_id, rating, comment) VALUES (?, ?, ?, ?, ?)")) {
            pstmt.setInt(1, driverId);
            pstmt.setInt(2, passengerId);
            pstmt.setInt(3, tripId);
            pstmt.setInt(4, rating);
            pstmt.setString(5, comment.isEmpty() ? null : comment);
            if (pstmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Review submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error submitting review: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}