package ui;

import utils.*;

import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SearchTrips extends JDialog {
    private JTextField departureField, destinationField;
    private DefaultTableModel tableModel;
    private JTable tripsTable;
    private final JFrame parent;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/carpool";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public SearchTrips(JFrame parent) {
        super(parent, "Search Trips", true);
        this.parent = parent;

        UserInfo currentUser = AuthUtils.getInstance().getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "You must be logged in", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        // Role check: Only PASSENGER or BOTH can search and book trips
        if (!"PASSENGER".equals(currentUser.getRole()) && !"BOTH".equals(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this, "You are not authorized to search or book trips", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        initComponents();
        setSize(800, 600); // Set fixed width and height
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel searchPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        searchPanel.add(new JLabel("Departure:"));
        departureField = new JTextField(20);
        searchPanel.add(departureField);

        searchPanel.add(new JLabel("Destination:"));
        destinationField = new JTextField(20);
        searchPanel.add(destinationField);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchTrips());
        searchPanel.add(searchButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        searchPanel.add(cancelButton);

        tableModel = new DefaultTableModel(new String[]{"ID", "Departure", "Destination", "Date", "Time", "Seats", "Price", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Allow editing for the "Action" column
            }
        };
        tripsTable = new JTable(tableModel);
        tripsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tripsTable.setRowHeight(25);
        tripsTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        tripsTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));
        JScrollPane scrollPane = new JScrollPane(tripsTable);

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void searchTrips() {
        String departure = departureField.getText().trim();
        String destination = destinationField.getText().trim();
        List<Trip> trips = TripManager.getInstance().searchTrips(departure, destination);
        tableModel.setRowCount(0);
        for (Trip trip : trips) {
            tableModel.addRow(new Object[]{
                    trip.getId(),
                    trip.getDeparture(),
                    trip.getDestination(),
                    trip.getDate(),
                    trip.getTime(),
                    trip.getSeatsAvailable(),
                    "€" + trip.getPrice(),
                    "Book"
            });
        }
    }

    private void reserveTrip(int tripId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            int passengerId = AuthUtils.getInstance().getCurrentUser().getId();

            // Check if the passenger is the driver of the trip
            String driverSql = "SELECT driver_id FROM trips WHERE id = ?";
            try (PreparedStatement driverStmt = conn.prepareStatement(driverSql)) {
                driverStmt.setInt(1, tripId);
                try (ResultSet rsDriver = driverStmt.executeQuery()) {
                    if (rsDriver.next()) {
                        int driverId = rsDriver.getInt("driver_id");
                        if (driverId == passengerId) {
                            JOptionPane.showMessageDialog(this, "You cannot book your own trip", "Error", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                }
            }

            // Check if the passenger already has a reservation for this trip
            String checkSql = "SELECT COUNT(*), seats_reserved FROM reservations WHERE trip_id = ? AND passenger_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, tripId);
                checkStmt.setInt(2, passengerId);
                try (ResultSet rsCheck = checkStmt.executeQuery()) {
                    rsCheck.next();
                    int existingCount = rsCheck.getInt(1);
                    if (existingCount > 0) {
                        JOptionPane.showMessageDialog(this, "You have already booked this trip! Check 'My Reservations' to edit or delete.",
                                "Already Booked", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
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

            // Check available seats
            String tripSql = "SELECT seats_available FROM trips WHERE id = ?";
            try (PreparedStatement tripStmt = conn.prepareStatement(tripSql)) {
                tripStmt.setInt(1, tripId);
                try (ResultSet rsTrip = tripStmt.executeQuery()) {
                    rsTrip.next();
                    int available = rsTrip.getInt("seats_available");
                    if (seatsToBook > available) {
                        JOptionPane.showMessageDialog(this,
                                "Only " + available + " seats are available!",
                                "Not Enough Seats", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            // Create reservation
            new ReservationDAO().createReservation(passengerId, tripId, seatsToBook);

            // Update available seats
            String updateSql = "UPDATE trips SET seats_available = seats_available - ? WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, seatsToBook);
                updateStmt.setInt(2, tripId);
                updateStmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Trip reserved successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            searchTrips();
            if (parent instanceof PassengerDashboard) {
                ((PassengerDashboard) parent).loadAvailableTrips();
            }
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error reserving trip: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
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
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            clicked = true;
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                int tripId = (int) tableModel.getValueAt(selectedRow, 0);
                if ("Book".equals(label)) {
                    reserveTrip(tripId);
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