package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.table.TableCellRenderer;

public class PassengerDashboard extends JFrame {
    private JLabel welcomeLabel;
    private JButton searchTripsButton;
    private JButton myReservationsButton;
    private JButton logoutButton;
    private JTable tripsTable;
    private DefaultTableModel tripsTableModel;
    private JScrollPane tableScrollPane;
    private JTextField searchFromField;
    private JTextField searchToField;
    private JTextField searchDateField;
    private LoginForm.UserInfo currentUser;
    private JLabel driverRatingLabel;
    private JTextArea reviewsArea;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/carpool";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String[] RATING_LABELS = {
        "Poor", "Fair", "Good", "Very Good", "Excellent"
    };
    private static final SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final Date CURRENT_DATE;

    static {
        try {
            // Current date and time: June 02, 2025, 05:33 PM +01
            CURRENT_DATE = sdfDateTime.parse("2025-06-02 17:33");
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse current date", e);
        }
    }

    public PassengerDashboard(LoginForm.UserInfo userInfo) {
        this.currentUser = userInfo;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadAvailableTrips();

        setTitle("Passenger Dashboard - " + userInfo.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void initializeComponents() {
        welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + " (Passenger)");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        searchTripsButton = new JButton("Search Trips");
        myReservationsButton = new JButton("My Reservations");
        logoutButton = new JButton("Logout");

        searchFromField = new JTextField(12);
        searchToField = new JTextField(12);
        searchDateField = new JTextField(10);
        searchDateField.setToolTipText("Format: YYYY-MM-DD (leave empty for all dates)");

        searchTripsButton.setBackground(new Color(33, 150, 243));
        searchTripsButton.setForeground(Color.WHITE);
        searchTripsButton.setFont(new Font("Arial", Font.BOLD, 12));

        String[] columnNames = {
            "Trip ID", "Driver", "From", "To", "Date", "Time",
            "Available Seats", "Price (€)", "Status", "Action", "DriverID" // Hidden column
        };
        tripsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9; // "Action" column is editable
            }
        };
        tripsTable = new JTable(tripsTableModel);
        tripsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tripsTable.getTableHeader().setBackground(new Color(63, 81, 181));
        tripsTable.getTableHeader().setForeground(Color.WHITE);
        tripsTable.setRowHeight(30);
        tripsTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        tripsTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));

        // Hide the DriverID column
        tripsTable.getColumnModel().getColumn(10).setMinWidth(0);
        tripsTable.getColumnModel().getColumn(10).setMaxWidth(0);
        tripsTable.getColumnModel().getColumn(10).setWidth(0);

        tableScrollPane = new JScrollPane(tripsTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Available Trips"));

        driverRatingLabel = new JLabel("Select a trip to see driver rating");
        reviewsArea = new JTextArea(5, 20);
        reviewsArea.setEditable(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

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

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel reviewsPanel = new JPanel(new BorderLayout());
        reviewsPanel.setBorder(BorderFactory.createTitledBorder("Driver Reviews"));
        reviewsPanel.add(driverRatingLabel, BorderLayout.NORTH);
        reviewsPanel.add(new JScrollPane(reviewsArea), BorderLayout.CENTER);
        centerPanel.add(reviewsPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        searchTripsButton.addActionListener(e -> new SearchTrips(this).setVisible(true));
        myReservationsButton.addActionListener(e -> new MyReservations(this, currentUser.getId()).setVisible(true));
        logoutButton.addActionListener(e -> logout());

        tripsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    viewTripDetails();
                }
            }
        });

        tripsTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = tripsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int driverId = getDriverIdFromRow(selectedRow);
                updateReviewsDisplay(driverId);
            }
        });
    }

    void loadAvailableTrips() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String sql = "SELECT t.id, u.id AS driver_id, u.name AS driver_name, t.departure, t.destination, " +
                         "t.date, t.time, t.seats_available, t.price, r.status, " +
                         "CASE WHEN r.passenger_id = ? THEN " +
                         "    CASE WHEN r.status = 'booked' THEN 'Cancel' " +
                         "         WHEN r.status = 'done' THEN 'Review' " +
                         "         ELSE 'Book' END " +
                         "ELSE 'Book' END AS action " +
                         "FROM trips t " +
                         "JOIN users u ON t.driver_id = u.id " +
                         "LEFT JOIN reservations r ON t.id = r.trip_id AND r.passenger_id = ? " +
                         "WHERE t.seats_available > 0 AND t.driver_id != ? " +
                         "ORDER BY t.date ASC, t.time ASC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, currentUser.getId());
            pstmt.setInt(2, currentUser.getId());
            pstmt.setInt(3, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();

            tripsTableModel.setRowCount(0);
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

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

                // Combine date and time to compare with current date
                String tripDateTimeStr = sdfDate.format(d) + " " + sdfTime.format(t);
                Date tripDateTime = sdfDateTime.parse(tripDateTimeStr);

                // Automate status update if trip is booked and date/time has passed
                if (status != null && status.equals("booked") && CURRENT_DATE.after(tripDateTime)) {
                    updateTripStatusToDone(tripId, currentUser.getId());
                    status = "done";
                    action = "Review";
                }

                // Capitalize the status for display
                if (status != null) {
                    status = status.substring(0, 1).toUpperCase() + status.substring(1);
                } else {
                    status = "-";
                }

                Object[] row = {
                    tripId,
                    driverName,
                    departure,
                    destination,
                    sdfDate.format(d),
                    sdfTime.format(t),
                    seats,
                    "€" + price,
                    status,
                    action,
                    driverId
                };
                tripsTableModel.addRow(row);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading trips: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchTrips() {
        String from = searchFromField.getText().trim();
        String to = searchToField.getText().trim();
        String date = searchDateField.getText().trim();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

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
                "WHERE t.seats_available > 0 AND t.driver_id != ?"
            );

            if (!from.isEmpty()) sql.append(" AND LOWER(t.departure) LIKE LOWER(?)");
            if (!to.isEmpty()) sql.append(" AND LOWER(t.destination) LIKE LOWER(?)");
            if (!date.isEmpty()) sql.append(" AND t.date = ?");

            sql.append(" ORDER BY t.date ASC, t.time ASC");

            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            int idx = 1;
            pstmt.setInt(idx++, currentUser.getId());
            pstmt.setInt(idx++, currentUser.getId());
            pstmt.setInt(idx++, currentUser.getId());
            if (!from.isEmpty()) pstmt.setString(idx++, "%" + from + "%");
            if (!to.isEmpty()) pstmt.setString(idx++, "%" + to + "%");
            if (!date.isEmpty()) {
                try {
                    // Validate date format (YYYY-MM-DD)
                    if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        throw new IllegalArgumentException("Date must be in YYYY-MM-DD format");
                    }
                    pstmt.setDate(idx++, java.sql.Date.valueOf(date)); // Explicitly use java.sql.Date
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid date format: " + ex.getMessage() + ". Please use YYYY-MM-DD.",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            ResultSet rs = pstmt.executeQuery();
            tripsTableModel.setRowCount(0);
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

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

                // Combine date and time to compare with current date
                String tripDateTimeStr = sdfDate.format(d) + " " + sdfTime.format(t);
                Date tripDateTime = sdfDateTime.parse(tripDateTimeStr);

                // Automate status update if trip is booked and date/time has passed
                if (status != null && status.equals("booked") && CURRENT_DATE.after(tripDateTime)) {
                    updateTripStatusToDone(tripId, currentUser.getId());
                    status = "done";
                    action = "Review";
                }

                // Capitalize the status for display
                if (status != null) {
                    status = status.substring(0, 1).toUpperCase() + status.substring(1);
                } else {
                    status = "-";
                }

                Object[] row = {
                    tripId,
                    driverName,
                    departure,
                    destination,
                    sdfDate.format(d),
                    sdfTime.format(t),
                    seats,
                    "€" + price,
                    status,
                    action,
                    driverId
                };
                tripsTableModel.addRow(row);
            }

            if (tripsTableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No trips found matching your criteria.",
                        "Search Results", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error searching trips: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bookTrip(int tripId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String checkSql = "SELECT COUNT(*) FROM reservations WHERE trip_id = ? AND passenger_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, tripId);
            checkStmt.setInt(2, currentUser.getId());
            ResultSet rsCheck = checkStmt.executeQuery();
            rsCheck.next();
            if (rsCheck.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "You have already booked this trip!",
                        "Already Booked", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String seatsStr = JOptionPane.showInputDialog(this,
                    "How many seats would you like to book?", "Book Seats",
                    JOptionPane.QUESTION_MESSAGE);
            if (seatsStr == null || seatsStr.trim().isEmpty()) return;
            int seatsToBook;
            try {
                seatsToBook = Integer.parseInt(seatsStr.trim());
                if (seatsToBook <= 0) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid positive number.",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid integer!",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String tripSql = "SELECT seats_available, driver_id FROM trips WHERE id = ?";
            PreparedStatement tripStmt = conn.prepareStatement(tripSql);
            tripStmt.setInt(1, tripId);
            ResultSet rsTrip = tripStmt.executeQuery();
            rsTrip.next();
            int available = rsTrip.getInt("seats_available");
            if (seatsToBook > available) {
                JOptionPane.showMessageDialog(this,
                        "Only " + available + " seats are available!",
                        "Not Enough Seats", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String bookSql = "INSERT INTO reservations (trip_id, passenger_id, seats_reserved, status) VALUES (?, ?, ?, 'booked')";
            PreparedStatement bookStmt = conn.prepareStatement(bookSql);
            bookStmt.setInt(1, tripId);
            bookStmt.setInt(2, currentUser.getId());
            bookStmt.setInt(3, seatsToBook);
            int res = bookStmt.executeUpdate();

            if (res > 0) {
                String updateSql = "UPDATE trips SET seats_available = seats_available - ? WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, seatsToBook);
                updateStmt.setInt(2, tripId);
                updateStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Trip successfully booked! It will be marked as done after the trip date and time.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                loadAvailableTrips();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to book the trip.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error booking trip: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelTrip(int tripId, int seatsReserved) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel your reservation for this trip?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String updateSql = "UPDATE reservations SET status = 'canceled' WHERE trip_id = ? AND passenger_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setInt(1, tripId);
            updateStmt.setInt(2, currentUser.getId());
            int res = updateStmt.executeUpdate();

            if (res > 0) {
                String restoreSeatsSql = "UPDATE trips SET seats_available = seats_available + ? WHERE id = ?";
                PreparedStatement restoreStmt = conn.prepareStatement(restoreSeatsSql);
                restoreStmt.setInt(1, seatsReserved);
                restoreStmt.setInt(2, tripId);
                restoreStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Reservation canceled successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAvailableTrips();
            } else {
                JOptionPane.showMessageDialog(this, "No reservation found to cancel or failed to cancel.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error canceling reservation: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTripStatusToDone(int tripId, int passengerId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String updateSql = "UPDATE reservations SET status = 'done' WHERE trip_id = ? AND passenger_id = ? AND status = 'booked'";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setInt(1, tripId);
            updateStmt.setInt(2, passengerId);
            updateStmt.executeUpdate();
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating trip status: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showReviewDialog(int tripId, int driverId) {
        ReviewDialog reviewDialog = new ReviewDialog(this, tripId, currentUser.getId(), driverId);
        reviewDialog.setVisible(true);
        loadAvailableTrips();
    }

    private void viewTripDetails() {
        int row = tripsTable.getSelectedRow();
        if (row == -1) return;

        String tripId = tripsTableModel.getValueAt(row, 0).toString();
        String driverName = tripsTableModel.getValueAt(row, 1).toString();
        String from = tripsTableModel.getValueAt(row, 2).toString();
        String to = tripsTableModel.getValueAt(row, 3).toString();
        String date = tripsTableModel.getValueAt(row, 4).toString();
        String time = tripsTableModel.getValueAt(row, 5).toString();
        String seats = tripsTableModel.getValueAt(row, 6).toString();
        String price = tripsTableModel.getValueAt(row, 7).toString();
        String status = tripsTableModel.getValueAt(row, 8).toString();

        String msg = String.format(
            "Trip ID: %s%nDriver: %s%nFrom: %s%nTo: %s%nDate: %s%nTime: %s%nAvailable Seats: %s%nPrice: %s%nStatus: %s",
            tripId, driverName, from, to, date, time, seats, price, status
        );
        JOptionPane.showMessageDialog(this, msg, "Trip Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewMyReservations() {
        new MyReservations(this, currentUser.getId()).setVisible(true);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
        }
    }

    private void switchToDriverMode() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            DriverDashboard driverDash = new DriverDashboard(currentUser);
            driverDash.setVisible(true);
        });
    }

    private int getDriverIdFromRow(int row) {
        return (int) tripsTableModel.getValueAt(row, 10);
    }

    private String getRatingDisplay(int driverId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT AVG(rating) as avg_rating, COUNT(*) as count FROM reviews WHERE driver_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, driverId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                double avgRating = rs.getDouble("avg_rating");
                int count = rs.getInt("count");
                if (count == 0) return "No reviews yet";
                int starRating = (int) Math.round(avgRating);
                String label = RATING_LABELS[starRating - 1];
                return String.format("%.1f ★ (%s, %d reviews)", avgRating, label, count);
            }
        } catch (SQLException ex) {
            System.err.println("Error fetching rating: " + ex.getMessage());
        }
        return "Error fetching rating";
    }

    private void updateReviewsDisplay(int driverId) {
        driverRatingLabel.setText("Driver Rating: " + getRatingDisplay(driverId));
        reviewsArea.setText("");
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT rating, comment FROM reviews WHERE driver_id = ? ORDER BY created_at DESC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, driverId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int rating = rs.getInt("rating");
                String comment = rs.getString("comment");
                reviewsArea.append(String.format("%d ★: %s\n", rating, comment != null ? comment : "No comment"));
            }
        } catch (SQLException ex) {
            reviewsArea.setText("Error loading reviews: " + ex.getMessage());
        }
    }

    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
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
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            clicked = true;
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                int tripId = (int) tripsTableModel.getValueAt(selectedRow, 0);
                int driverId = getDriverIdFromRow(selectedRow);
                if ("Book".equals(label)) {
                    bookTrip(tripId);
                } else if ("Cancel".equals(label)) {
                    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        String sql = "SELECT seats_reserved FROM reservations WHERE trip_id = ? AND passenger_id = ?";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setInt(1, tripId);
                        pstmt.setInt(2, currentUser.getId());
                        ResultSet rs = pstmt.executeQuery();
                        int seatsReserved = rs.next() ? rs.getInt("seats_reserved") : 0;
                        if (seatsReserved > 0) {
                            cancelTrip(tripId, seatsReserved);
                        } else {
                            JOptionPane.showMessageDialog(PassengerDashboard.this, "No reservation found to cancel.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (ClassNotFoundException | SQLException ex) {
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

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}