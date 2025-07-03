package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ReviewDialog extends JDialog {
    private JTextArea commentArea;
    private JRadioButton[] ratingButtons;
    private JButton submitButton, updateButton, deleteButton, cancelButton;
    private int tripId, passengerId, driverId;
    private boolean reviewExists = false;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/carpool";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public ReviewDialog(JFrame parent, int tripId, int passengerId, int driverId) {
        super(parent, "Review Driver", true);
        this.tripId = tripId;
        this.passengerId = passengerId;
        this.driverId = driverId;

        initializeComponents();
        setupLayout();
        checkExistingReview();

        setSize(400, 300);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        commentArea = new JTextArea(5, 20);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);

        ratingButtons = new JRadioButton[5];
        ButtonGroup ratingGroup = new ButtonGroup();
        for (int i = 0; i < 5; i++) {
            ratingButtons[i] = new JRadioButton((i + 1) + " ★");
            ratingGroup.add(ratingButtons[i]);
        }

        submitButton = new JButton("Submit");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        cancelButton = new JButton("Cancel");

        submitButton.addActionListener(e -> submitReview());
        updateButton.addActionListener(e -> updateReview());
        deleteButton.addActionListener(e -> deleteReview());
        cancelButton.addActionListener(e -> dispose());
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));

        JPanel ratingPanel = new JPanel(new FlowLayout());
        ratingPanel.setBorder(BorderFactory.createTitledBorder("Rating"));
        for (JRadioButton button : ratingButtons) {
            ratingPanel.add(button);
        }

        JPanel commentPanel = new JPanel(new BorderLayout());
        commentPanel.setBorder(BorderFactory.createTitledBorder("Comment"));
        commentPanel.add(new JScrollPane(commentArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(submitButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);

        add(ratingPanel, BorderLayout.NORTH);
        add(commentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        submitButton.setEnabled(false);
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void checkExistingReview() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT rating, comment FROM reviews WHERE trip_id = ? AND passenger_id = ? AND driver_id = ?")) {
            pstmt.setInt(1, tripId);
            pstmt.setInt(2, passengerId);
            pstmt.setInt(3, driverId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    reviewExists = true;
                    int rating = rs.getInt("rating");
                    String comment = rs.getString("comment");
                    ratingButtons[rating - 1].setSelected(true);
                    commentArea.setText(comment);
                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                } else {
                    submitButton.setEnabled(true);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error checking existing review: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submitReview() {
        int rating = getSelectedRating();
        if (rating == 0) {
            JOptionPane.showMessageDialog(this, "Please select a rating!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String comment = commentArea.getText().trim();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO reviews (trip_id, passenger_id, driver_id, rating, comment) VALUES (?, ?, ?, ?, ?)")) {
            pstmt.setInt(1, tripId);
            pstmt.setInt(2, passengerId);
            pstmt.setInt(3, driverId);
            pstmt.setInt(4, rating);
            pstmt.setString(5, comment.isEmpty() ? null : comment);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Review submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error submitting review: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateReview() {
        int rating = getSelectedRating();
        if (rating == 0) {
            JOptionPane.showMessageDialog(this, "Please select a rating!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String comment = commentArea.getText().trim();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE reviews SET rating = ?, comment = ? WHERE trip_id = ? AND passenger_id = ? AND driver_id = ?")) {
            pstmt.setInt(1, rating);
            pstmt.setString(2, comment.isEmpty() ? null : comment);
            pstmt.setInt(3, tripId);
            pstmt.setInt(4, passengerId);
            pstmt.setInt(5, driverId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Review updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No review found to update.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating review: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteReview() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete your review?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "DELETE FROM reviews WHERE trip_id = ? AND passenger_id = ? AND driver_id = ?")) {
            pstmt.setInt(1, tripId);
            pstmt.setInt(2, passengerId);
            pstmt.setInt(3, driverId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Review deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No review found to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting review: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getSelectedRating() {
        for (int i = 0; i < ratingButtons.length; i++) {
            if (ratingButtons[i].isSelected()) {
                return i + 1;
            }
        }
        return 0;
    }
}