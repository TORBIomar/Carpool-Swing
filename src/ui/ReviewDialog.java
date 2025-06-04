package ui;

import javax.swing.*;
import java.awt.*;
<<<<<<< HEAD
import java.sql.*;

public class ReviewDialog extends JDialog {
    private JComboBox<Integer> ratingCombo;
    private JTextArea commentArea;
    private JButton submitButton;
    private final int tripId, passengerId, driverId;
=======
import java.awt.event.*;
import java.sql.*;

public class ReviewDialog extends JDialog {
    private JLabel[] starLabels;
    private JTextArea commentArea;
    private JButton submitButton;
    private int tripId;
    private int passengerId;
    private int driverId;
    private int selectedRating = 5;
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a

    private static final String DB_URL = "jdbc:mysql://localhost:3306/carpool";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public ReviewDialog(JFrame parent, int tripId, int passengerId, int driverId) {
<<<<<<< HEAD
        super(parent, "Review Trip", true);
=======
        super(parent, "Rate Your Trip", true);
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        this.tripId = tripId;
        this.passengerId = passengerId;
        this.driverId = driverId;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setSize(400, 300);
        setLocationRelativeTo(parent);
<<<<<<< HEAD
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
=======
        setUndecorated(true); // Cool: Remove default window decorations
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 2));
    }

    private void initializeComponents() {
        starLabels = new JLabel[5];
        for (int i = 0; i < 5; i++) {
            starLabels[i] = new JLabel("★");
            starLabels[i].setFont(new Font("Arial", Font.PLAIN, 30));
            starLabels[i].setForeground(Color.YELLOW);
        }
        commentArea = new JTextArea(3, 25);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(0, 150, 136));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
    }

    private void setupLayout() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(0, 50, 100);
                Color color2 = new Color(0, 120, 215);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel starPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        for (int i = 0; i < 5; i++) {
            starPanel.add(starLabels[i]);
        }
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Rate this trip:"), gbc);
        gbc.gridy = 1;
        panel.add(starPanel, gbc);

        gbc.gridy = 2;
        panel.add(new JLabel("Comments (optional):"), gbc);
        gbc.gridy = 3;
        panel.add(new JScrollPane(commentArea), gbc);

        gbc.gridy = 4;
        panel.add(submitButton, gbc);

        add(panel);
    }

    private void setupEventHandlers() {
        for (int i = 0; i < 5; i++) {
            final int index = i;
            starLabels[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    for (int j = 0; j <= index; j++) {
                        starLabels[j].setForeground(new Color(255, 215, 0)); // Gold on hover
                    }
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    for (int j = 0; j < 5; j++) {
                        starLabels[j].setForeground(Color.YELLOW);
                    }
                    for (int j = 0; j < selectedRating; j++) {
                        starLabels[j].setForeground(new Color(255, 215, 0)); // Keep selected gold
                    }
                }
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedRating = index + 1;
                    for (int j = 0; j < 5; j++) {
                        starLabels[j].setForeground(j < selectedRating ? new Color(255, 215, 0) : Color.YELLOW);
                    }
                }
            });
        }

        submitButton.addActionListener(e -> {
            submitReview();
            // Cool animation: Fade out
            new Thread(() -> {
                for (float i = 1.0f; i >= 0; i -= 0.1f) {
                    try {
                        Thread.sleep(50);
                        setOpacity(i);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
                dispose();
            }).start();
        });
    }

    private void submitReview() {
        String comment = commentArea.getText().trim();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String checkSql = "SELECT COUNT(*) FROM reviews WHERE trip_id = ? AND passenger_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, tripId);
            checkStmt.setInt(2, passengerId);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "You have already reviewed this trip!",
                        "Duplicate Review", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sql = "INSERT INTO reviews (trip_id, passenger_id, driver_id, rating, comment) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, tripId);
            pstmt.setInt(2, passengerId);
            pstmt.setInt(3, driverId);
            pstmt.setInt(4, selectedRating);
            pstmt.setString(5, comment.isEmpty() ? null : comment);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Review submitted! Thanks for your feedback!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error submitting review: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static class java {

        public java() {
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        }
    }
}