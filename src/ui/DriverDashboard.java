package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
<<<<<<< HEAD
import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import utils.UserInfo;

public class DriverDashboard extends JFrame {
    private JLabel welcomeLabel;
    private JButton createTripButton, updateTripButton, deleteTripButton, logoutButton, uploadDocumentsButton;
    private JTable tripsTable;
    private DefaultTableModel tripsTableModel;
    private JScrollPane tableScrollPane;
    private UserInfo currentUser;
=======
import java.sql.*;
import java.text.SimpleDateFormat;
import java.io.File;

public class DriverDashboard extends JFrame {
    private JLabel welcomeLabel;
    private JButton createTripButton;
    private JButton updateTripButton;
    private JButton deleteTripButton;
    private JButton logoutButton;
    private JButton uploadDocumentsButton;
    private JTable tripsTable;
    private DefaultTableModel tripsTableModel;
    private JScrollPane tableScrollPane;
    private LoginForm.UserInfo currentUser;
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a

    private static final String DB_URL = "jdbc:mysql://localhost:3306/carpool";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

<<<<<<< HEAD
    public DriverDashboard(UserInfo userInfo) {
=======
    public DriverDashboard(LoginForm.UserInfo userInfo) {
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        this.currentUser = userInfo;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadUserTrips();

        setTitle("Driver Dashboard - " + userInfo.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
<<<<<<< HEAD
        setResizable(false);
=======
        setResizable(true);
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
    }

    private void initializeComponents() {
        welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + " (Driver)");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        createTripButton = new JButton("Create New Trip");
        updateTripButton = new JButton("Update Trip");
        deleteTripButton = new JButton("Delete Trip");
        logoutButton = new JButton("Logout");
        uploadDocumentsButton = new JButton("Upload Documents");

<<<<<<< HEAD
        String[] columnNames = {"Trip ID", "Departure", "Destination", "Date", "Time", "Seats Available", "Price (€)", "Reservations"};
=======
        String[] columnNames = {
            "Trip ID", "Departure", "Destination", "Date", "Time",
            "Seats Available", "Price (€)", "Reservations"
        };
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        tripsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };
        tripsTable = new JTable(tripsTableModel);
        tripsTable.setRowHeight(30);
        tripsTable.getTableHeader().setBackground(new Color(63, 81, 181));
        tripsTable.getTableHeader().setForeground(Color.WHITE);
        tripsTable.getColumn("Reservations").setCellRenderer(new ButtonRenderer());
        tripsTable.getColumn("Reservations").setCellEditor(new ButtonEditor(new JCheckBox()));
        tripsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

<<<<<<< HEAD
        // Set column widths for better fit within 900x600
        tripsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        tripsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        tripsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        tripsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        tripsTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        tripsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        tripsTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        tripsTable.getColumnModel().getColumn(7).setPreferredWidth(80);

=======
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        tableScrollPane = new JScrollPane(tripsTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("My Trips"));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
<<<<<<< HEAD
=======

>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomePanel.add(welcomeLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(createTripButton);
        buttonPanel.add(updateTripButton);
        buttonPanel.add(deleteTripButton);
        buttonPanel.add(uploadDocumentsButton);
        buttonPanel.add(logoutButton);
        if ("BOTH".equals(currentUser.getRole())) {
            JButton switchButton = new JButton("Switch to Passenger Mode");
            switchButton.addActionListener(e -> switchToPassengerMode());
            buttonPanel.add(switchButton);
        }

        topPanel.add(welcomePanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        createTripButton.addActionListener(e -> openCreateTripDialog());
        updateTripButton.addActionListener(e -> openEditTripForSelectedRow());
        deleteTripButton.addActionListener(e -> deleteTripForSelectedRow());
        uploadDocumentsButton.addActionListener(e -> openUploadDocumentsDialog());
        logoutButton.addActionListener(e -> logout());

        tripsTable.addMouseListener(new MouseAdapter() {
<<<<<<< HEAD
            @Override
=======
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    viewTripDetails();
                }
            }
        });
    }

    void loadUserTrips() {
<<<<<<< HEAD
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT id, departure, destination, date, time, seats_available, price " +
                             "FROM trips WHERE driver_id = ? ORDER BY date ASC, time ASC")) {
            pstmt.setInt(1, currentUser.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                tripsTableModel.setRowCount(0);
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

                while (rs.next()) {
                    Object[] row = {
                            rs.getInt("id"),
                            rs.getString("departure"),
                            rs.getString("destination"),
                            sdfDate.format(rs.getDate("date")),
                            sdfTime.format(rs.getTime("time")),
                            rs.getInt("seats_available"),
                            "€" + rs.getDouble("price"),
                            "View"
                    };
                    tripsTableModel.addRow(row);
                }
            }
        } catch (SQLException ex) {
=======
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String sql = "SELECT id, departure, destination, date, time, seats_available, price " +
                         "FROM trips WHERE driver_id = ? ORDER BY date ASC, time ASC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();

            tripsTableModel.setRowCount(0);
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

            while (rs.next()) {
                int tripId = rs.getInt("id");
                String departure = rs.getString("departure");
                String destination = rs.getString("destination");
                Date date = rs.getDate("date");
                Time time = rs.getTime("time");
                int seats = rs.getInt("seats_available");
                double price = rs.getDouble("price");

                Object[] row = {
                    tripId,
                    departure,
                    destination,
                    sdfDate.format(date),
                    sdfTime.format(time),
                    seats,
                    "€" + price,
                    "View"
                };
                tripsTableModel.addRow(row);
            }
        } catch (ClassNotFoundException | SQLException ex) {
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            JOptionPane.showMessageDialog(this, "Error loading your trips: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openCreateTripDialog() {
        String status = getApplicationStatus();
<<<<<<< HEAD
        if ("NOT_APPLIED".equals(status)) {
=======
        if (status.equals("NOT_APPLIED")) {
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            JOptionPane.showMessageDialog(this, "Please upload your documents first.",
                    "Documents Required", JOptionPane.WARNING_MESSAGE);
            openUploadDocumentsDialog();
            return;
<<<<<<< HEAD
        } else if ("PENDING".equals(status)) {
            JOptionPane.showMessageDialog(this, "Your application is pending approval.",
                    "Approval Pending", JOptionPane.WARNING_MESSAGE);
            return;
        } else if ("DECLINED".equals(status)) {
=======
        } else if (status.equals("PENDING")) {
            JOptionPane.showMessageDialog(this, "Your application is pending approval.",
                    "Approval Pending", JOptionPane.WARNING_MESSAGE);
            return;
        } else if (status.equals("DECLINED")) {
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            JOptionPane.showMessageDialog(this, "Your application was declined. Please re-upload your documents.",
                    "Application Declined", JOptionPane.WARNING_MESSAGE);
            openUploadDocumentsDialog();
            return;
<<<<<<< HEAD
        } else if (!"APPROVED".equals(status)) {
=======
        } else if (!status.equals("APPROVED")) {
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            JOptionPane.showMessageDialog(this, "Invalid application status.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Create New Trip", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
<<<<<<< HEAD
=======

>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

<<<<<<< HEAD
        JTextField depField = new JTextField(20);
        JTextField destField = new JTextField(20);
        JTextField dateField = new JTextField(20);
        dateField.setToolTipText("Format: YYYY-MM-DD");
        JTextField timeField = new JTextField(20);
        timeField.setToolTipText("Format: HH:MM (24h)");
        JTextField seatsField = new JTextField(20);
=======
        JLabel depLabel = new JLabel("Departure:");
        JTextField depField = new JTextField(20);

        JLabel destLabel = new JLabel("Destination:");
        JTextField destField = new JTextField(20);

        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        JTextField dateField = new JTextField(20);
        dateField.setToolTipText("Format: YYYY-MM-DD");

        JLabel timeLabel = new JLabel("Time (HH:MM):");
        JTextField timeField = new JTextField(20);
        timeField.setToolTipText("Format: HH:MM (24h)");

        JLabel seatsLabel = new JLabel("Seats Available:");
        JTextField seatsField = new JTextField(20);

        JLabel priceLabel = new JLabel("Price (€):");
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        JTextField priceField = new JTextField(20);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

<<<<<<< HEAD
        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Departure:"), gbc);
        gbc.gridx = 1; dialog.add(depField, gbc);
        gbc.gridx = 0; gbc.gridy++; dialog.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 1; dialog.add(destField, gbc);
        gbc.gridx = 0; gbc.gridy++; dialog.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; dialog.add(dateField, gbc);
        gbc.gridx = 0; gbc.gridy++; dialog.add(new JLabel("Time (HH:MM):"), gbc);
        gbc.gridx = 1; dialog.add(timeField, gbc);
        gbc.gridx = 0; gbc.gridy++; dialog.add(new JLabel("Seats Available:"), gbc);
        gbc.gridx = 1; dialog.add(seatsField, gbc);
        gbc.gridx = 0; gbc.gridy++; dialog.add(new JLabel("Price (€):"), gbc);
=======
        gbc.gridx = 0; gbc.gridy = 0; dialog.add(depLabel, gbc);
        gbc.gridx = 1; dialog.add(depField, gbc);

        gbc.gridx = 0; gbc.gridy++; dialog.add(destLabel, gbc);
        gbc.gridx = 1; dialog.add(destField, gbc);

        gbc.gridx = 0; gbc.gridy++; dialog.add(dateLabel, gbc);
        gbc.gridx = 1; dialog.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy++; dialog.add(timeLabel, gbc);
        gbc.gridx = 1; dialog.add(timeField, gbc);

        gbc.gridx = 0; gbc.gridy++; dialog.add(seatsLabel, gbc);
        gbc.gridx = 1; dialog.add(seatsField, gbc);

        gbc.gridx = 0; gbc.gridy++; dialog.add(priceLabel, gbc);
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        gbc.gridx = 1; dialog.add(priceField, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(saveButton);
        btnPanel.add(cancelButton);
<<<<<<< HEAD
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; dialog.add(btnPanel, gbc);

        cancelButton.addActionListener(e -> dialog.dispose());
=======

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; dialog.add(btnPanel, gbc);

        cancelButton.addActionListener(e -> dialog.dispose());

>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        saveButton.addActionListener(e -> {
            String dep = depField.getText().trim();
            String dest = destField.getText().trim();
            String dateStr = dateField.getText().trim();
            String timeStr = timeField.getText().trim();
            String seatsStr = seatsField.getText().trim();
            String priceStr = priceField.getText().trim();

<<<<<<< HEAD
            if (dep.isEmpty() || dest.isEmpty() || dateStr.isEmpty() || timeStr.isEmpty() || seatsStr.isEmpty() || priceStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!dateStr.matches("\\d{4}-\\d{2}-\\d{2}") || !timeStr.matches("\\d{2}:\\d{2}")) {
                JOptionPane.showMessageDialog(dialog, "Invalid date (YYYY-MM-DD) or time (HH:MM) format.",
=======
            if (dep.isEmpty() || dest.isEmpty() || dateStr.isEmpty() ||
                timeStr.isEmpty() || seatsStr.isEmpty() || priceStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required.",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use YYYY-MM-DD.",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!timeStr.matches("\\d{2}:\\d{2}")) {
                JOptionPane.showMessageDialog(dialog, "Invalid time format. Use HH:MM (24h).",
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int seats = Integer.parseInt(seatsStr);
                double price = Double.parseDouble(priceStr);
<<<<<<< HEAD
                String formattedTimeStr = timeStr + ":00";

                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement(
                             "INSERT INTO trips (driver_id, departure, destination, date, time, seats_available, price) " +
                                     "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    pstmt.setInt(1, currentUser.getId());
                    pstmt.setString(2, dep);
                    pstmt.setString(3, dest);
                    pstmt.setDate(4, java.sql.Date.valueOf(dateStr));
                    pstmt.setTime(5, java.sql.Time.valueOf(formattedTimeStr));
                    pstmt.setInt(6, seats);
                    pstmt.setDouble(7, price);

                    if (pstmt.executeUpdate() > 0) {
                        JOptionPane.showMessageDialog(dialog, "Trip created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        loadUserTrips();
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Seats and Price must be numeric!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error saving trip: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
=======

                String formattedTimeStr = timeStr + ":00";

                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                String insertSql = "INSERT INTO trips " +
                                   "(driver_id, departure, destination, date, time, seats_available, price) " +
                                   "VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(insertSql);
                pstmt.setInt(1, currentUser.getId());
                pstmt.setString(2, dep);
                pstmt.setString(3, dest);
                pstmt.setDate(4, java.sql.Date.valueOf(dateStr));
                pstmt.setTime(5, java.sql.Time.valueOf(formattedTimeStr));
                pstmt.setInt(6, seats);
                pstmt.setDouble(7, price);

                int result = pstmt.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(dialog, "Trip created successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadUserTrips();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to create trip.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

                pstmt.close();
                conn.close();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Seats and Price must be numeric!",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date or time format: " + ex.getMessage(),
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error saving trip: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            }
        });

        dialog.setVisible(true);
    }

    private void openEditTripDialog(int tripId) {
        Object[] tripData = getTripById(tripId);
        if (tripData == null) {
            JOptionPane.showMessageDialog(this, "Trip not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Edit Trip", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
<<<<<<< HEAD
=======

>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

<<<<<<< HEAD
        JTextField depField = new JTextField((String) tripData[1], 20);
        JTextField destField = new JTextField((String) tripData[2], 20);
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        JTextField dateField = new JTextField(sdfDate.format((Date) tripData[3]), 20);
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        JTextField timeField = new JTextField(sdfTime.format((Time) tripData[4]), 20);
        JTextField seatsField = new JTextField(String.valueOf((int) tripData[5]), 20);
=======
        JLabel depLabel = new JLabel("Departure:");
        JTextField depField = new JTextField((String) tripData[1], 20);

        JLabel destLabel = new JLabel("Destination:");
        JTextField destField = new JTextField((String) tripData[2], 20);

        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdfDate.format((Date) tripData[3]);
        JTextField dateField = new JTextField(dateStr, 20);
        dateField.setToolTipText("Format: YYYY-MM-DD");

        JLabel timeLabel = new JLabel("Time (HH:MM):");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        String timeStr = sdfTime.format((Time) tripData[4]);
        JTextField timeField = new JTextField(timeStr, 20);
        timeField.setToolTipText("Format: HH:MM (24h)");

        JLabel seatsLabel = new JLabel("Seats Available:");
        JTextField seatsField = new JTextField(String.valueOf((int) tripData[5]), 20);

        JLabel priceLabel = new JLabel("Price (€):");
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        JTextField priceField = new JTextField(String.valueOf((double) tripData[6]), 20);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

<<<<<<< HEAD
        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Departure:"), gbc);
        gbc.gridx = 1; dialog.add(depField, gbc);
        gbc.gridx = 0; gbc.gridy++; dialog.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 1; dialog.add(destField, gbc);
        gbc.gridx = 0; gbc.gridy++; dialog.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; dialog.add(dateField, gbc);
        gbc.gridx = 0; gbc.gridy++; dialog.add(new JLabel("Time (HH:MM):"), gbc);
        gbc.gridx = 1; dialog.add(timeField, gbc);
        gbc.gridx = 0; gbc.gridy++; dialog.add(new JLabel("Seats Available:"), gbc);
        gbc.gridx = 1; dialog.add(seatsField, gbc);
        gbc.gridx = 0; gbc.gridy++; dialog.add(new JLabel("Price (€):"), gbc);
=======
        gbc.gridx = 0; gbc.gridy = 0; dialog.add(depLabel, gbc);
        gbc.gridx = 1; dialog.add(depField, gbc);

        gbc.gridx = 0; gbc.gridy++; dialog.add(destLabel, gbc);
        gbc.gridx = 1; dialog.add(destField, gbc);

        gbc.gridx = 0; gbc.gridy++; dialog.add(dateLabel, gbc);
        gbc.gridx = 1; dialog.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy++; dialog.add(timeLabel, gbc);
        gbc.gridx = 1; dialog.add(timeField, gbc);

        gbc.gridx = 0; gbc.gridy++; dialog.add(seatsLabel, gbc);
        gbc.gridx = 1; dialog.add(seatsField, gbc);

        gbc.gridx = 0; gbc.gridy++; dialog.add(priceLabel, gbc);
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        gbc.gridx = 1; dialog.add(priceField, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(saveButton);
        btnPanel.add(cancelButton);
<<<<<<< HEAD
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; dialog.add(btnPanel, gbc);

        cancelButton.addActionListener(e -> dialog.dispose());
        saveButton.addActionListener(e -> {
            String dep = depField.getText().trim();
            String dest = destField.getText().trim();
            String newDateStr = dateField.getText().trim();
            String newTimeStr = timeField.getText().trim();

            if (dep.isEmpty() || dest.isEmpty() || newDateStr.isEmpty() || newTimeStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!newDateStr.matches("\\d{4}-\\d{2}-\\d{2}") || !newTimeStr.matches("\\d{2}:\\d{2}")) {
                JOptionPane.showMessageDialog(dialog, "Invalid date (YYYY-MM-DD) or time (HH:MM) format.",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int newSeats = Integer.parseInt(seatsField.getText().trim());
                double newPrice = Double.parseDouble(priceField.getText().trim());
                String formattedTimeStr = newTimeStr + ":00";

                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement(
                             "UPDATE trips SET departure = ?, destination = ?, date = ?, time = ?, seats_available = ?, price = ? WHERE id = ?")) {
                    pstmt.setString(1, dep);
                    pstmt.setString(2, dest);
                    pstmt.setDate(3, java.sql.Date.valueOf(newDateStr));
                    pstmt.setTime(4, java.sql.Time.valueOf(formattedTimeStr));
                    pstmt.setInt(5, newSeats);
                    pstmt.setDouble(6, newPrice);
                    pstmt.setInt(7, tripId);

                    if (pstmt.executeUpdate() > 0) {
                        JOptionPane.showMessageDialog(dialog, "Trip updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        loadUserTrips();
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Seats and Price must be numeric!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating trip: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
=======

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; dialog.add(btnPanel, gbc);

        cancelButton.addActionListener(e -> dialog.dispose());

        saveButton.addActionListener(e -> {
            try {
                String dep = depField.getText().trim();
                String dest = destField.getText().trim();
                String newDateStr = dateField.getText().trim();
                String newTimeStr = timeField.getText().trim();
                int newSeats = Integer.parseInt(seatsField.getText().trim());
                double newPrice = Double.parseDouble(priceField.getText().trim());

                if (dep.isEmpty() || dest.isEmpty() || newDateStr.isEmpty() ||
                    newTimeStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "All fields are required.",
                            "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!newDateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    throw new IllegalArgumentException("Invalid date format.");
                }

                if (!newTimeStr.matches("\\d{2}:\\d{2}")) {
                    throw new IllegalArgumentException("Invalid time format.");
                }

                String formattedTimeStr = newTimeStr + ":00";

                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                String updateSql = "UPDATE trips SET departure = ?, destination = ?, date = ?, time = ?, seats_available = ?, price = ? WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, dep);
                pstmt.setString(2, dest);
                pstmt.setDate(3, java.sql.Date.valueOf(newDateStr));
                pstmt.setTime(4, java.sql.Time.valueOf(formattedTimeStr));
                pstmt.setInt(5, newSeats);
                pstmt.setDouble(6, newPrice);
                pstmt.setInt(7, tripId);

                int result = pstmt.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(dialog, "Trip updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadUserTrips();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update trip.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

                pstmt.close();
                conn.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating trip: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            }
        });

        dialog.setVisible(true);
    }

    private void openEditTripForSelectedRow() {
        int row = tripsTable.getSelectedRow();
        if (row == -1) {
<<<<<<< HEAD
            JOptionPane.showMessageDialog(this, "Please select a trip to update.", "No Trip Selected", JOptionPane.WARNING_MESSAGE);
=======
            JOptionPane.showMessageDialog(this, "Please select a trip to update.",
                    "No Trip Selected", JOptionPane.WARNING_MESSAGE);
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            return;
        }
        int tripId = (int) tripsTableModel.getValueAt(row, 0);
        openEditTripDialog(tripId);
    }

    private void deleteTripForSelectedRow() {
        int row = tripsTable.getSelectedRow();
        if (row == -1) {
<<<<<<< HEAD
            JOptionPane.showMessageDialog(this, "Please select a trip to delete.", "No Trip Selected", JOptionPane.WARNING_MESSAGE);
=======
            JOptionPane.showMessageDialog(this, "Please select a trip to delete.",
                    "No Trip Selected", JOptionPane.WARNING_MESSAGE);
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            return;
        }
        int tripId = (int) tripsTableModel.getValueAt(row, 0);
        deleteTrip(tripId);
    }

    private Object[] getTripById(int tripId) {
<<<<<<< HEAD
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT id, departure, destination, date, time, seats_available, price " +
                             "FROM trips WHERE id = ? AND driver_id = ?")) {
            pstmt.setInt(1, tripId);
            pstmt.setInt(2, currentUser.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                            rs.getInt("id"),
                            rs.getString("departure"),
                            rs.getString("destination"),
                            rs.getDate("date"),
                            rs.getTime("time"),
                            rs.getInt("seats_available"),
                            rs.getDouble("price")
                    };
                }
                return null;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching trip: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
=======
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id, departure, destination, date, time, seats_available, price " +
                         "FROM trips WHERE id = ? AND driver_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, tripId);
            pstmt.setInt(2, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Object[]{
                    rs.getInt("id"),
                    rs.getString("departure"),
                    rs.getString("destination"),
                    rs.getDate("date"),
                    rs.getTime("time"),
                    rs.getInt("seats_available"),
                    rs.getDouble("price")
                };
            }
            return null;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching trip: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            return null;
        }
    }

    private void deleteTrip(int tripId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String checkSql = "SELECT COUNT(*) FROM reservations WHERE trip_id = ?";
<<<<<<< HEAD
            try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
                checkPstmt.setInt(1, tripId);
                try (ResultSet rs = checkPstmt.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "Cannot delete trip with existing reservations.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
=======
            PreparedStatement checkPstmt = conn.prepareStatement(checkSql);
            checkPstmt.setInt(1, tripId);
            ResultSet rs = checkPstmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Cannot delete trip with existing reservations.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this trip?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
<<<<<<< HEAD
            if (confirm != JOptionPane.YES_OPTION) return;

            try (PreparedStatement deletePstmt = conn.prepareStatement("DELETE FROM trips WHERE id = ? AND driver_id = ?")) {
                deletePstmt.setInt(1, tripId);
                deletePstmt.setInt(2, currentUser.getId());
                if (deletePstmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Trip deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadUserTrips();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting trip: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
=======
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            String deleteSql = "DELETE FROM trips WHERE id = ? AND driver_id = ?";
            PreparedStatement deletePstmt = conn.prepareStatement(deleteSql);
            deletePstmt.setInt(1, tripId);
            deletePstmt.setInt(2, currentUser.getId());

            int result = deletePstmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Trip deleted successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUserTrips();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete trip.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting trip: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        }
    }

    private void showTripDetailsDialog(int tripId) {
        Object[] tripData = getTripById(tripId);
        if (tripData == null) {
            JOptionPane.showMessageDialog(this, "Trip not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Trip Details", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
<<<<<<< HEAD
=======

>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

<<<<<<< HEAD
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Trip ID: " + tripData[0]), gbc);
        gbc.gridy++; dialog.add(new JLabel("Departure: " + tripData[1]), gbc);
        gbc.gridy++; dialog.add(new JLabel("Destination: " + tripData[2]), gbc);
        gbc.gridy++; dialog.add(new JLabel("Date: " + sdfDate.format((Date) tripData[3])), gbc);
        gbc.gridy++; dialog.add(new JLabel("Time: " + sdfTime.format((Time) tripData[4])), gbc);
        gbc.gridy++; dialog.add(new JLabel("Seats Available: " + tripData[5]), gbc);
        gbc.gridy++; dialog.add(new JLabel("Price: €" + tripData[6]), gbc);
=======
        JLabel idLabel = new JLabel("Trip ID: " + tripData[0]);
        JLabel depLabel = new JLabel("Departure: " + tripData[1]);
        JLabel destLabel = new JLabel("Destination: " + tripData[2]);
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdfDate.format((Date) tripData[3]);
        JLabel dateLabel = new JLabel("Date: " + dateStr);
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        String timeStr = sdfTime.format((Time) tripData[4]);
        JLabel timeLabel = new JLabel("Time: " + timeStr);
        JLabel seatsLabel = new JLabel("Seats Available: " + tripData[5]);
        JLabel priceLabel = new JLabel("Price: €" + tripData[6]);

        gbc.gridx = 0; gbc.gridy = 0; dialog.add(idLabel, gbc);
        gbc.gridy++; dialog.add(depLabel, gbc);
        gbc.gridy++; dialog.add(destLabel, gbc);
        gbc.gridy++; dialog.add(dateLabel, gbc);
        gbc.gridy++; dialog.add(timeLabel, gbc);
        gbc.gridy++; dialog.add(seatsLabel, gbc);
        gbc.gridy++; dialog.add(priceLabel, gbc);
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> {
            dialog.dispose();
            openEditTripDialog(tripId);
        });
<<<<<<< HEAD
=======

>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            dialog.dispose();
            deleteTrip(tripId);
        });
<<<<<<< HEAD
=======

>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);
<<<<<<< HEAD
=======

>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        gbc.gridy++; gbc.gridwidth = 2; dialog.add(buttonPanel, gbc);

        dialog.setVisible(true);
    }

    private void viewTripDetails() {
        int row = tripsTable.getSelectedRow();
        if (row == -1) return;
<<<<<<< HEAD
=======

>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        int tripId = (int) tripsTableModel.getValueAt(row, 0);
        showTripDetailsDialog(tripId);
    }

    private void viewReservations(int tripId) {
        JDialog dialog = new JDialog(this, "Reservations for Trip #" + tripId, true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        String[] cols = {"Reservation ID", "Passenger Name", "Seats Reserved"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);
        JScrollPane scroll = new JScrollPane(table);
        dialog.add(scroll, BorderLayout.CENTER);

<<<<<<< HEAD
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT r.id, u.name AS passenger_name, r.seats_reserved " +
                             "FROM reservations r JOIN users u ON r.passenger_id = u.id " +
                             "WHERE r.trip_id = ?")) {
            pstmt.setInt(1, tripId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{rs.getInt("id"), rs.getString("passenger_name"), rs.getInt("seats_reserved")});
                }
=======
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT r.id, u.name AS passenger_name, r.seats_reserved " +
                         "FROM reservations r " +
                         "JOIN users u ON r.passenger_id = u.id " +
                         "WHERE r.trip_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, tripId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int resId = rs.getInt("id");
                String passengerName = rs.getString("passenger_name");
                int seats = rs.getInt("seats_reserved");
                model.addRow(new Object[]{resId, passengerName, seats});
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading reservations: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        dialog.setVisible(true);
    }

    private void logout() {
<<<<<<< HEAD
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
=======
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
        }
    }

    private void switchToPassengerMode() {
<<<<<<< HEAD
        dispose();
        SwingUtilities.invokeLater(() -> new PassengerDashboard(currentUser).setVisible(true));
    }

    private String getApplicationStatus() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT application_status FROM users WHERE id = ?")) {
            pstmt.setInt(1, currentUser.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getString("application_status") : "NOT_APPLIED";
            }
=======
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            PassengerDashboard passengerDash = new PassengerDashboard(currentUser);
            passengerDash.setVisible(true);
        });
    }

    private boolean isDocumentsUploaded() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id_card_path, vehicle_registration_path FROM users WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String idCard = rs.getString("id_card_path");
                String vehicleReg = rs.getString("vehicle_registration_path");
                return idCard != null && !idCard.isEmpty() && vehicleReg != null && !vehicleReg.isEmpty();
            }
            return false;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error checking documents: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private String getApplicationStatus() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT application_status FROM users WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("application_status");
            }
            return "NOT_APPLIED";
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error checking application status: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return "NOT_APPLIED";
        }
    }

    private void openUploadDocumentsDialog() {
        JDialog dialog = new JDialog(this, "Upload Documents", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(3, 2, 10, 10));

        JTextField idCardField = new JTextField(20);
        idCardField.setEditable(false);
        JButton uploadIdCardButton = new JButton("Upload ID Card");
        uploadIdCardButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
<<<<<<< HEAD
                idCardField.setText(fileChooser.getSelectedFile().getAbsolutePath());
=======
                File file = fileChooser.getSelectedFile();
                idCardField.setText(file.getAbsolutePath());
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            }
        });

        JTextField vehicleRegField = new JTextField(20);
        vehicleRegField.setEditable(false);
        JButton uploadVehicleRegButton = new JButton("Upload Vehicle Registration");
        uploadVehicleRegButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
<<<<<<< HEAD
                vehicleRegField.setText(fileChooser.getSelectedFile().getAbsolutePath());
=======
                File file = fileChooser.getSelectedFile();
                vehicleRegField.setText(file.getAbsolutePath());
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            }
        });

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String idCardPath = idCardField.getText();
            String vehicleRegPath = vehicleRegField.getText();
            if (idCardPath.isEmpty() || vehicleRegPath.isEmpty()) {
<<<<<<< HEAD
                JOptionPane.showMessageDialog(dialog, "Please upload both documents.", "Error", JOptionPane.ERROR_MESSAGE);
=======
                JOptionPane.showMessageDialog(dialog, "Please upload both documents.",
                        "Error", JOptionPane.ERROR_MESSAGE);
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
                return;
            }
            saveDocumentsToDatabase(idCardPath, vehicleRegPath);
            dialog.dispose();
        });

        dialog.add(new JLabel("ID Card:"));
        dialog.add(idCardField);
        dialog.add(uploadIdCardButton);
        dialog.add(new JLabel("Vehicle Registration:"));
        dialog.add(vehicleRegField);
        dialog.add(uploadVehicleRegButton);
        dialog.add(new JLabel(""));
        dialog.add(saveButton);

        dialog.setVisible(true);
    }

    private void saveDocumentsToDatabase(String idCardPath, String vehicleRegPath) {
<<<<<<< HEAD
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE users SET id_card_path = ?, vehicle_registration_path = ?, application_status = 'PENDING' WHERE id = ?")) {
            pstmt.setString(1, idCardPath);
            pstmt.setString(2, vehicleRegPath);
            pstmt.setInt(3, currentUser.getId());
            if (pstmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Documents uploaded successfully. Awaiting admin approval.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
=======
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "UPDATE users SET id_card_path = ?, vehicle_registration_path = ?, application_status = 'PENDING' WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, idCardPath);
            pstmt.setString(2, vehicleRegPath);
            pstmt.setInt(3, currentUser.getId());
            int result = pstmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Documents uploaded successfully. Awaiting admin approval.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to upload documents.",
                        "Error", JOptionPane.ERROR_MESSAGE);
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving documents: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
<<<<<<< HEAD
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
=======
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
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
<<<<<<< HEAD
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = value == null ? "" : value.toString();
=======
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            button.setText(label);
            clicked = true;
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                int tripId = (int) tripsTableModel.getValueAt(selectedRow, 0);
                viewReservations(tripId);
            }
            clicked = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
<<<<<<< HEAD
=======

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
    }
}