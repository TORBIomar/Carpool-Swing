package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import utils.UserInfo;

public class PassengerDashboard extends JFrame {
    private JLabel welcomeLabel, driverRatingLabel;
    private JButton myReservationsButton, logoutButton;
    private JTable tripsTable;
    private DefaultTableModel tripsTableModel;
    private JScrollPane tableScrollPane;
    private JTextField searchFromField, searchToField, searchDateField;
    private JButton searchTripsButton;
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
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initializeComponents() {
        welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + " (Passenger)");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        myReservationsButton = new JButton("My Reservations");
        logoutButton = new JButton("Logout");
        searchTripsButton = new JButton("Search Trips");

        searchFromField = new JTextField(12);
        searchToField = new JTextField(12);
        searchDateField = new JTextField(10);
        searchDateField.setToolTipText("Format: YYYY-MM-DD (leave empty for all dates)");

        searchTripsButton.setBackground(new Color(33, 150, 243));
        searchTripsButton.setForeground(Color.WHITE);
        searchTripsButton.setFont(new Font("Arial", Font.BOLD, 12));

        String[] columnNames = {"Trip ID", "Driver", "From", "To", "Date", "Time", "Available Seats", "Price (€)", "Status", "Action", "DriverID"};
        tripsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9;
            }
        };
        tripsTable = new JTable(tripsTableModel);
        tripsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tripsTable.getTableHeader().setBackground(new Color(63, 81, 181));
        tripsTable.getTableHeader().setForeground(Color.WHITE);
        tripsTable.setRowHeight(30);
        tripsTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        tripsTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));
        tripsTable.getColumnModel().getColumn(10).setMinWidth(0);
        tripsTable.getColumnModel().getColumn(10).setMaxWidth(0);

        // Set column widths for better fit within 900x600
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

        // Add topPanel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomePanel.add(welcomeLabel);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(myReservationsButton);
        buttonPanel.add(logoutButton);
        if ("BOTH".equals(currentUser.getRole())) {
            JButton switchButton = new JButton("Switch to Driver Mode");
            switchButton.addActionListener(e -> switchToDriverMode());
            buttonPanel.add(switchButton);
        }
        topPanel.add(welcomePanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        gbc.gridy = 0;
        add(topPanel, gbc);

        // Add searchPanel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Trips"));
        searchPanel.add(new JLabel("From:"));
        searchPanel.add(searchFromField);
        searchPanel.add(new JLabel("To:"));
        searchPanel.add(searchToField);
        searchPanel.add(new JLabel("Date:"));
        searchPanel.add(searchDateField);
        searchPanel.add(searchTripsButton);
        JButton clearButton = new JButton("Show All");
        clearButton.addActionListener(e -> {
            searchFromField.setText("");
            searchToField.setText("");
            searchDateField.setText("");
            loadAvailableTrips();
        });
        searchPanel.add(clearButton);
        gbc.gridy = 1;
        add(searchPanel, gbc);

        // Add centerPanel
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
                int driverId = (int) tripsTableModel.getValueAt(selectedRow, 10);
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
                            "         WHEN r.status = 'done' THEN 'Review' " +
                            "         ELSE 'Book' END " +
                            "ELSE 'Book' END AS action " +
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
                        String driverName = rs.getString("driver_name");
                        String departure = rs.getString("departure");
                        String destination = rs.getString("destination");
                        Date d = rs.getDate("date");
                        Time t = rs.getTime("time");
                        int seats = rs.getInt("seats_available");
                        double price = rs.getDouble("price");
                        String status = rs.getString("status");
                        String action = rs.getString("action");

                        String tripDateTimeStr = sdfDate.format(d) + " " + sdfTime.format(t);
                        Date tripDateTime = sdfDateTime.parse(tripDateTimeStr);

                        if ("booked".equals(status) && now.after(tripDateTime)) {
                            updateTripStatusToDone(tripId, currentUser.getId());
                            status = "done";
                            action = "Review";
                        }

                        status = status != null ? status.substring(0, 1).toUpperCase() + status.substring(1) : "-";

                        tripsTableModel.addRow(new Object[]{
                                tripId, driverName, departure, destination, sdfDate.format(d),
                                sdfTime.format(t), seats, "€" + price, status, action, driverId
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
            try (PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE reservations SET status = 'canceled' WHERE trip_id = ? AND passenger_id = ?")) {
                updateStmt.setInt(1, tripId);
                updateStmt.setInt(2, currentUser.getId());
                int res = updateStmt.executeUpdate();

                if (res > 0) {
                    try (PreparedStatement restoreStmt = conn.prepareStatement(
                            "UPDATE trips SET seats_available = seats_available + ? WHERE id = ?")) {
                        restoreStmt.setInt(1, seatsReserved);
                        restoreStmt.setInt(2, tripId);
                        restoreStmt.executeUpdate();
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
        new ReviewDialog(this, tripId, currentUser.getId(), driverId).setVisible(true);
        loadAvailableTrips();
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
                     "SELECT rating, comment FROM reviews WHERE driver_id = ? ORDER BY created_at DESC")) {
            pstmt.setInt(1, driverId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reviewsArea.append(String.format("%d ★: %s\n", rs.getInt("rating"),
                            rs.getString("comment") != null ? rs.getString("comment") : "No comment"));
                }
            }
        } catch (SQLException ex) {
            reviewsArea.setText("Error loading reviews: " + ex.getMessage());
        }
    }

    private static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            return this;
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
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = value == null ? "" : value.toString();
            button.setText(label);
            clicked = true;
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                int tripId = (int) tripsTableModel.getValueAt(selectedRow, 0);
                int driverId = (int) tripsTableModel.getValueAt(selectedRow, 10);
                if ("Book".equals(label)) {
                    bookTrip(tripId);
                } else if ("Cancel".equals(label)) {
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
                } else if ("Review".equals(label)) {
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