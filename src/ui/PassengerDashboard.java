package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import utils.UserInfo;

public class PassengerDashboard extends JFrame {
    private JLabel welcomeLabel, driverRatingLabel;
    private JButton myReservationsButton, logoutButton, searchTripsButton, submitReclamationButton;
    private JTable tripsTable;
    private DefaultTableModel tripsTableModel;
    private JScrollPane tableScrollPane;
    private JTextField searchFromField, searchToField, searchDateField;
    private JTextArea reviewsArea;
    private UserInfo currentUser;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/carpool";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String[] RATING_LABELS = {"Poor", "Fair", "Good", "Very Good", "Excellent"};

    public PassengerDashboard(UserInfo userInfo) {
        this.currentUser = userInfo;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadAvailableTrips();

        setTitle("Passenger Dashboard - " + userInfo.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + " (Passenger)");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        myReservationsButton = new JButton("My Reservations");
        logoutButton = new JButton("Logout");
        searchTripsButton = new JButton("Search Trips");
        submitReclamationButton = new JButton("Submit Reclamation");

        searchFromField = new JTextField(12);
        searchToField = new JTextField(12);
        searchDateField = new JTextField(10);
        searchDateField.setToolTipText("Format: YYYY-MM-DD (leave empty for all dates)");

        JButton[] buttons = {myReservationsButton, logoutButton, searchTripsButton, submitReclamationButton};
        for (JButton button : buttons) {
            button.setFont(new Font("Arial", Font.PLAIN, 12));
            button.setPreferredSize(new Dimension(100, 25));
            button.setFocusPainted(false);
            button.setMargin(new Insets(2, 5, 2, 5));
        }

        String[] columnNames = {"Trip ID", "Driver", "From", "To", "Date", "Time", "Available Seats", "Price (€)", "Status", "Action", "Review", "DriverID"};
        tripsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9 || column == 10;
            }
        };
        tripsTable = new JTable(tripsTableModel);
        tripsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tripsTable.getTableHeader().setBackground(new Color(63, 81, 181));
        tripsTable.getTableHeader().setForeground(Color.WHITE);
        tripsTable.setRowHeight(30);
        tripsTable.getColumn("Action").setCellRenderer(new ActionRenderer());
        tripsTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));
        tripsTable.getColumn("Review").setCellRenderer(new ReviewRenderer());
        tripsTable.getColumn("Review").setCellEditor(new ReviewButtonEditor(new JCheckBox()));
        tripsTable.getColumnModel().getColumn(11).setMinWidth(0);
        tripsTable.getColumnModel().getColumn(11).setMaxWidth(0);

        tripsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        tripsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        tripsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        tripsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        tripsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        tripsTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        tripsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        tripsTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        tripsTable.getColumnModel().getColumn(8).setPreferredWidth(80);
        tripsTable.getColumnModel().getColumn(9).setPreferredWidth(80);
        tripsTable.getColumnModel().getColumn(10).setPreferredWidth(80);

        tableScrollPane = new JScrollPane(tripsTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Available Trips"));

        driverRatingLabel = new JLabel("Select a trip to see driver rating");
        reviewsArea = new JTextArea(5, 20);
        reviewsArea.setEditable(false);
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Corrected method name
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomePanel.add(welcomeLabel);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        buttonPanel.add(myReservationsButton);
        buttonPanel.add(submitReclamationButton);
        buttonPanel.add(logoutButton);
        if ("BOTH".equals(currentUser.getRole())) {
            JButton switchButton = new JButton("Switch to Driver Mode");
            switchButton.setFont(new Font("Arial", Font.PLAIN, 12));
            switchButton.setPreferredSize(new Dimension(100, 25));
            switchButton.setFocusPainted(false);
            switchButton.setMargin(new Insets(2, 5, 2, 5));
            buttonPanel.add(switchButton);
            switchButton.addActionListener(e -> switchToDriverMode());
        }
        topPanel.add(welcomePanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        gbc.gridy = 0;
        add(topPanel, gbc);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Trips"));
        searchPanel.add(new JLabel("From:"));
        searchPanel.add(searchFromField);
        searchPanel.add(new JLabel("To:"));
        searchPanel.add(searchToField);
        searchPanel.add(new JLabel("Date:"));
        searchPanel.add(searchDateField);
        searchPanel.add(searchTripsButton);
        JButton clearButton = new JButton("Show All");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 12));
        clearButton.setPreferredSize(new Dimension(100, 25));
        clearButton.setFocusPainted(false);
        clearButton.setMargin(new Insets(2, 5, 2, 5));
        clearButton.addActionListener(e -> {
            searchFromField.setText("");
            searchToField.setText("");
            searchDateField.setText("");
            loadAvailableTrips();
        });
        searchPanel.add(clearButton);
        gbc.gridy = 1;
        add(searchPanel, gbc);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        JPanel reviewsPanel = new JPanel(new BorderLayout());
        reviewsPanel.setBorder(BorderFactory.createTitledBorder("Driver Reviews"));
        reviewsPanel.add(driverRatingLabel, BorderLayout.NORTH);
        reviewsPanel.add(new JScrollPane(reviewsArea), BorderLayout.CENTER);
        centerPanel.add(reviewsPanel, BorderLayout.SOUTH);
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(centerPanel, gbc);
    }

    private void setupEventHandlers() {
        searchTripsButton.addActionListener(e -> searchTrips());
        myReservationsButton.addActionListener(e -> new MyReservations(this, currentUser.getId()).setVisible(true));
        logoutButton.addActionListener(e -> logout());
        submitReclamationButton.addActionListener(e -> openSubmitReclamationDialog());

        tripsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    viewTripDetails();
                }
            }
        });

        tripsTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = tripsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int driverId = (int) tripsTableModel.getValueAt(selectedRow, 11);
                updateReviewsDisplay(driverId);
            }
        });
    }

    void loadAvailableTrips() {
        searchTrips("", "", "");
    }

    private void searchTrips() {
        searchTrips(searchFromField.getText().trim(), searchToField.getText().trim(), searchDateField.getText().trim());
    }

    private void searchTrips(String from, String to, String date) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            StringBuilder sql = new StringBuilder(
                    "SELECT t.id, u.id AS driver_id, u.name AS driver_name, t.departure, t.destination, " +
                            "t.date, t.time, t.seats_available, t.price, r.status, " +
                            "CASE WHEN r.passenger_id = ? THEN " +
                            "    CASE WHEN r.status = 'booked' THEN 'Cancel' " +
                            "         WHEN r.status = 'done' THEN 'Completed' " +
                            "         ELSE 'Book' END " +
                            "ELSE 'Book' END AS action, " +
                            "CASE WHEN r.status = 'done' AND r.passenger_id = ? THEN 'Review' ELSE '' END AS review_action, " +
                            "EXISTS(SELECT 1 FROM reviews WHERE trip_id = t.id AND passenger_id = ? AND driver_id = u.id) AS has_review " +
                            "FROM trips t " +
                            "JOIN users u ON t.driver_id = u.id " +
                            "LEFT JOIN reservations r ON t.id = r.trip_id AND r.passenger_id = ? " +
                            "WHERE t.seats_available > 0 AND t.driver_id != ?");
            if (!from.isEmpty()) sql.append(" AND LOWER(t.departure) LIKE LOWER(?)");
            if (!to.isEmpty()) sql.append(" AND LOWER(t.destination) LIKE LOWER(?)");
            if (!date.isEmpty()) sql.append(" AND t.date = ?");
            sql.append(" ORDER BY t.date ASC, t.time ASC");

            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                int idx = 1;
                pstmt.setInt(idx++, currentUser.getId());
                pstmt.setInt(idx++, currentUser.getId());
                pstmt.setInt(idx++, currentUser.getId());
                pstmt.setInt(idx++, currentUser.getId());
                pstmt.setInt(idx++, currentUser.getId());
                if (!from.isEmpty()) pstmt.setString(idx++, "%" + from + "%");
                if (!to.isEmpty()) pstmt.setString(idx++, "%" + to + "%");
                if (!date.isEmpty()) {
                    if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.",
                                "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    pstmt.setDate(idx++, java.sql.Date.valueOf(date));
                }

                try (ResultSet rs = pstmt.executeQuery()) {
                    tripsTableModel.setRowCount(0);
                    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
                    SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date now = new Date();

                    while (rs.next()) {
                        int tripId = rs.getInt("id");
                        int driverId = rs.getInt("driver_id");
                        String driverName = rs.getString("driver_name") != null ? rs.getString("driver_name") : "";
                        String departure = rs.getString("departure") != null ? rs.getString("departure") : "";
                        String destination = rs.getString("destination") != null ? rs.getString("destination") : "";
                        Date d = rs.getDate("date");
                        Time t = rs.getTime("time");
                        int seats = rs.getInt("seats_available");
                        double price = rs.getDouble("price");
                        String status = rs.getString("status");
                        String action = rs.getString("action");
                        String reviewAction = rs.getString("review_action");
                        boolean hasReview = rs.getBoolean("has_review");

                        // Combine date and time for accurate comparison
                        String tripDateTimeStr = (d != null && t != null) ? sdfDate.format(d) + " " + sdfTime.format(t) : "";
                        Date tripDateTime = tripDateTimeStr.isEmpty() ? null : sdfDateTime.parse(tripDateTimeStr);

                        // Auto-update status to 'done' if trip is past and user has booking
                        if ("booked".equals(status) && tripDateTime != null && now.after(tripDateTime)) {
                            updateTripStatusToDone(tripId, currentUser.getId());
                            status = "done";
                            action = "Completed";
                            reviewAction = "Review"; // Enable review for completed trips
                        }

                        // Ensure review button is set correctly for 'done' status
                        if ("done".equals(status)) {
                            reviewAction = hasReview ? "Edit Review" : "Review";
                        } else {
                            reviewAction = ""; // Disable review for non-done trips
                        }

                        status = status != null ? status.substring(0, 1).toUpperCase() + status.substring(1) : "-";

                        tripsTableModel.addRow(new Object[]{
                                tripId, driverName, departure, destination, d != null ? sdfDate.format(d) : "N/A",
                                t != null ? sdfTime.format(t) : "N/A", seats, "€" + price, status, action, reviewAction, driverId
                        });
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error searching trips: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bookTrip(int tripId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String checkSql = "SELECT COUNT(*) FROM reservations WHERE trip_id = ? AND passenger_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, tripId);
                checkStmt.setInt(2, currentUser.getId());
                try (ResultSet rsCheck = checkStmt.executeQuery()) {
                    rsCheck.next();
                    if (rsCheck.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "You have already booked this trip!",
                                "Already Booked", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            String seatsStr = JOptionPane.showInputDialog(this, "How many seats would you like to book?");
            if (seatsStr == null || seatsStr.trim().isEmpty()) return;
            int seatsToBook = Integer.parseInt(seatsStr.trim());
            if (seatsToBook <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String tripSql = "SELECT seats_available FROM trips WHERE id = ?";
            try (PreparedStatement tripStmt = conn.prepareStatement(tripSql)) {
                tripStmt.setInt(1, tripId);
                try (ResultSet rsTrip = tripStmt.executeQuery()) {
                    rsTrip.next();
                    int available = rsTrip.getInt("seats_available");
                    if (seatsToBook > available) {
                        JOptionPane.showMessageDialog(this, "Only " + available + " seats are available!",
                                "Not Enough Seats", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            try (PreparedStatement bookStmt = conn.prepareStatement(
                    "INSERT INTO reservations (trip_id, passenger_id, seats_reserved, status) VALUES (?, ?, ?, 'booked')")) {
                bookStmt.setInt(1, tripId);
                bookStmt.setInt(2, currentUser.getId());
                bookStmt.setInt(3, seatsToBook);
                bookStmt.executeUpdate();
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE trips SET seats_available = seats_available - ? WHERE id = ?")) {
                updateStmt.setInt(1, seatsToBook);
                updateStmt.setInt(2, tripId);
                updateStmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Trip successfully booked!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAvailableTrips();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error booking trip: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelTrip(int tripId, int seatsReserved) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel your reservation?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String deleteSql = "DELETE FROM reservations WHERE trip_id = ? AND passenger_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, tripId);
                deleteStmt.setInt(2, currentUser.getId());
                int res = deleteStmt.executeUpdate();
                if (res > 0) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(
                            "UPDATE trips SET seats_available = seats_available + ? WHERE id = ?")) {
                        updateStmt.setInt(1, seatsReserved);
                        updateStmt.setInt(2, tripId);
                        updateStmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Reservation canceled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadAvailableTrips();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No reservation found to cancel.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error canceling reservation: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTripStatusToDone(int tripId, int passengerId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement updateStmt = conn.prepareStatement(
                     "UPDATE reservations SET status = 'done' WHERE trip_id = ? AND passenger_id = ? AND status = 'booked'")) {
            updateStmt.setInt(1, tripId);
            updateStmt.setInt(2, passengerId);
            updateStmt.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating trip status: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showReviewDialog(int tripId, int driverId) {
        ReviewDialog dialog = new ReviewDialog(this, tripId, currentUser.getId(), driverId);
        dialog.setVisible(true);
        // Refresh the trips table after the dialog is closed
        loadAvailableTrips();
        // Update reviews display if a row is still selected
        int selectedRow = tripsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int currentDriverId = (int) tripsTableModel.getValueAt(selectedRow, 11);
            updateReviewsDisplay(currentDriverId);
        }
    }

    private void openSubmitReclamationDialog() {
        JDialog dialog = new JDialog(this, "Submit Reclamation", true);
        dialog.setSize(450, 300); // Increased size to better accommodate components
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Increased insets for better spacing
        gbc.anchor = GridBagConstraints.WEST;

        // Label for Reclamation Description
        JLabel label = new JLabel("Reclamation Description:");
        label.setFont(new Font("Arial", Font.BOLD, 16)); // Increased font size to 16 and made bold for emphasis
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.0; // Label doesn't expand
        gbc.fill = GridBagConstraints.NONE;
        dialog.add(label, gbc);

        // Text Area for Description
        JTextArea descriptionArea = new JTextArea(8, 30); // 8 rows, 30 columns
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14)); // Consistent font size
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding inside text area
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2; // Span across both columns
        gbc.weightx = 1.0; // Allow horizontal expansion
        gbc.weighty = 1.0; // Allow vertical expansion
        gbc.fill = GridBagConstraints.BOTH; // Fill available space
        dialog.add(descriptionScroll, gbc);

        // Buttons
        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 14)); // Increased button font size
        submitButton.setPreferredSize(new Dimension(100, 30)); // Slightly larger button
        submitButton.setMargin(new Insets(5, 10, 5, 10));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14)); // Increased button font size
        cancelButton.setPreferredSize(new Dimension(100, 30)); // Slightly larger button
        cancelButton.setMargin(new Insets(5, 10, 5, 10));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Increased spacing
        btnPanel.add(submitButton);
        btnPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST; // Align buttons to the right
        dialog.add(btnPanel, gbc);

        // Action Listeners
        cancelButton.addActionListener(e -> dialog.dispose());
        submitButton.addActionListener(e -> {
            String description = descriptionArea.getText().trim();
            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Description is required.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(
                         "INSERT INTO claims (user_id, description, status) VALUES (?, ?, ?)")) {
                pstmt.setInt(1, currentUser.getId());
                pstmt.setString(2, description);
                pstmt.setString(3, "PENDING");
                if (pstmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(dialog, "Reclamation submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error submitting reclamation: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    private void viewTripDetails() {
        int row = tripsTable.getSelectedRow();
        if (row == -1) return;

        String msg = String.format(
                "Trip ID: %s%nDriver: %s%nFrom: %s%nTo: %s%nDate: %s%nTime: %s%nAvailable Seats: %s%nPrice: %s%nStatus: %s",
                tripsTableModel.getValueAt(row, 0), tripsTableModel.getValueAt(row, 1),
                tripsTableModel.getValueAt(row, 2), tripsTableModel.getValueAt(row, 3),
                tripsTableModel.getValueAt(row, 4), tripsTableModel.getValueAt(row, 5),
                tripsTableModel.getValueAt(row, 6), tripsTableModel.getValueAt(row, 7),
                tripsTableModel.getValueAt(row, 8)
        );
        JOptionPane.showMessageDialog(this, msg, "Trip Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
        }
    }

    private void switchToDriverMode() {
        dispose();
        SwingUtilities.invokeLater(() -> new DriverDashboard(currentUser).setVisible(true));
    }

    private String getRatingDisplay(int driverId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT AVG(rating) as avg_rating, COUNT(*) as count FROM reviews WHERE driver_id = ?")) {
            pstmt.setInt(1, driverId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double avgRating = rs.getDouble("avg_rating");
                    int count = rs.getInt("count");
                    if (count == 0) return "No reviews yet";
                    int starRating = (int) Math.round(avgRating);
                    String label = RATING_LABELS[starRating - 1];
                    return String.format("%.1f ★ (%s, %d reviews)", avgRating, label, count);
                }
                return "No reviews yet";
            }
        } catch (SQLException ex) {
            return "Error fetching rating";
        }
    }

    private void updateReviewsDisplay(int driverId) {
        driverRatingLabel.setText("Driver Rating: " + getRatingDisplay(driverId));
        reviewsArea.setText("");
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT r.rating, r.comment, u.name as passenger_name FROM reviews r " +
                             "JOIN users u ON r.passenger_id = u.id " +
                             "WHERE r.driver_id = ? ORDER BY r.created_at DESC")) {
            pstmt.setInt(1, driverId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    reviewsArea.setText("No reviews available for this driver.");
                } else {
                    do {
                        String passengerName = rs.getString("passenger_name") != null ? rs.getString("passenger_name") : "Anonymous";
                        int rating = rs.getInt("rating");
                        String comment = rs.getString("comment") != null ? rs.getString("comment") : "No comment";
                        reviewsArea.append(String.format("%s - %d ★: %s\n", passengerName, rating, comment));
                    } while (rs.next());
                }
            }
        } catch (SQLException ex) {
            reviewsArea.setText("Error loading reviews: " + ex.getMessage());
        }
    }

    private static class ActionRenderer extends DefaultTableCellRenderer {
        private final JButton button = new JButton();

        public ActionRenderer() {
            button.setOpaque(true);
            button.setFont(new Font("Arial", Font.PLAIN, 12));
            button.setPreferredSize(new Dimension(100, 25));
            button.setMargin(new Insets(2, 5, 2, 5));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            String text = value == null ? "" : value.toString();
            if ("Book".equals(text) || "Cancel".equals(text)) {
                button.setText(text);
                button.setEnabled(true);
                return button;
            } else {
                setText(text); // Shows "Completed" or "" as text
                setHorizontalAlignment(JLabel.CENTER);
                return this;
            }
        }
    }

    private static class ReviewRenderer extends DefaultTableCellRenderer {
        private final JButton button = new JButton();

        public ReviewRenderer() {
            button.setOpaque(true);
            button.setFont(new Font("Arial", Font.PLAIN, 12));
            button.setPreferredSize(new Dimension(100, 25));
            button.setMargin(new Insets(2, 5, 2, 5));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            String text = value == null ? "" : value.toString();
            if ("Review".equals(text) || "Edit Review".equals(text)) {
                button.setText(text);
                button.setEnabled(true);
                return button;
            } else {
                setText(""); // No text when review is not available
                setHorizontalAlignment(JLabel.CENTER);
                return this;
            }
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setFont(new Font("Arial", Font.PLAIN, 12));
            button.setPreferredSize(new Dimension(100, 25));
            button.setMargin(new Insets(2, 5, 2, 5));
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = value == null ? "" : value.toString();
            button.setText(label);
            button.setEnabled("Book".equals(label) || "Cancel".equals(label));
            clicked = true;
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                if ("Book".equals(label)) {
                    int tripId = (int) tripsTableModel.getValueAt(selectedRow, 0);
                    bookTrip(tripId);
                } else if ("Cancel".equals(label)) {
                    int tripId = (int) tripsTableModel.getValueAt(selectedRow, 0);
                    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                         PreparedStatement pstmt = conn.prepareStatement(
                                 "SELECT seats_reserved FROM reservations WHERE trip_id = ? AND passenger_id = ?")) {
                        pstmt.setInt(1, tripId);
                        pstmt.setInt(2, currentUser.getId());
                        try (ResultSet rs = pstmt.executeQuery()) {
                            if (rs.next()) {
                                cancelTrip(tripId, rs.getInt("seats_reserved"));
                            }
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(PassengerDashboard.this, "Error checking reservation: " + ex.getMessage(),
                                "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            clicked = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }

    private class ReviewButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private int selectedRow;

        public ReviewButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setFont(new Font("Arial", Font.PLAIN, 12));
            button.setPreferredSize(new Dimension(100, 25));
            button.setMargin(new Insets(2, 5, 2, 5));
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = value == null ? "" : value.toString();
            button.setText(label);
            button.setEnabled("Review".equals(label) || "Edit Review".equals(label));
            clicked = true;
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                if ("Review".equals(label) || "Edit Review".equals(label)) {
                    int tripId = (int) tripsTableModel.getValueAt(selectedRow, 0);
                    int driverId = (int) tripsTableModel.getValueAt(selectedRow, 11);
                    showReviewDialog(tripId, driverId);
                }
            }
            clicked = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }
}