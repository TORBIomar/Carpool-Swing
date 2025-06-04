package ui;

<<<<<<< HEAD
=======
import utils.Reservation;
import utils.ReservationDAO;
import utils.Trip;
import utils.TripDAO;

>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
<<<<<<< HEAD
=======
import java.util.List;
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a

public class MyReservations extends JDialog {
    private DefaultTableModel tableModel;
    private JTable reservationsTable;
    private final int passengerId;

<<<<<<< HEAD
    private static final String DB_URL = "jdbc:mysql://localhost:3306/carpool";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

=======
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
    public MyReservations(JFrame parent, int passengerId) {
        super(parent, "My Reservations", true);
        this.passengerId = passengerId;
        setSize(700, 400);
        initComponents();
        loadReservations();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
<<<<<<< HEAD
        tableModel = new DefaultTableModel(new String[]{"Reservation ID", "Trip ID", "Departure", "Destination", "Date", "Time", "Seats Reserved", "Status", "Action", "Edit"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8 || column == 9;
=======
        tableModel = new DefaultTableModel(new String[]{"Reservation ID", "Trip ID", "Departure", "Destination", "Date", "Time", "Reserved At", "Seats Reserved", "Action", "Edit"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Disable editing if table is empty or invalid
                return tableModel.getRowCount() > 0 && (column == 8 || column == 9);
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            }
        };
        reservationsTable = new JTable(tableModel);
        reservationsTable.setRowHeight(25);
        reservationsTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
<<<<<<< HEAD
        reservationsTable.getColumn("Action").setCellEditor(new ReservationButtonEditor(new JCheckBox()));
        reservationsTable.getColumn("Edit").setCellRenderer(new ButtonRenderer());
        reservationsTable.getColumn("Edit").setCellEditor(new EditButtonEditor(new JCheckBox()));
=======
        reservationsTable.getColumn("Action").setCellEditor(new ReservationButtonEditor(new JCheckBox(), reservationsTable));
        reservationsTable.getColumn("Edit").setCellRenderer(new ButtonRenderer());
        reservationsTable.getColumn("Edit").setCellEditor(new EditButtonEditor(new JCheckBox(), reservationsTable));
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        JScrollPane scrollPane = new JScrollPane(reservationsTable);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadReservations() {
<<<<<<< HEAD
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT r.id, r.trip_id, t.departure, t.destination, t.date, t.time, r.seats_reserved, r.status " +
                             "FROM reservations r JOIN trips t ON r.trip_id = t.id " +
                             "WHERE r.passenger_id = ? ORDER BY t.date DESC, t.time DESC")) {
            pstmt.setInt(1, passengerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                tableModel.setRowCount(0);
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getInt("id"), rs.getInt("trip_id"), rs.getString("departure"), rs.getString("destination"),
                            rs.getDate("date"), rs.getTime("time"), rs.getInt("seats_reserved"), rs.getString("status"),
                            "Delete", "Edit Seats"
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading reservations: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
=======
        // Stop any ongoing editing to prevent race conditions
        if (reservationsTable.isEditing()) {
            reservationsTable.getCellEditor().stopCellEditing();
        }

        tableModel.setRowCount(0);
        List<Reservation> reservations = new ReservationDAO().getReservationsByPassengerId(passengerId);
        TripDAO tripDAO = new TripDAO();
        if (reservations.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No reservations found for this passenger.",
                    "No Reservations", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Reservation res : reservations) {
                Trip trip = tripDAO.getTripById(res.getTripId());
                if (trip != null) {
                    tableModel.addRow(new Object[]{
                            res.getId(),
                            res.getTripId(),
                            trip.getDeparture(),
                            trip.getDestination(),
                            trip.getDate(),
                            trip.getTime(),
                            res.getReservedAt(),
                            res.getSeatsReserved(),
                            "Delete",
                            "Edit Seats"
                    });
                } else {
                    System.err.println("Trip not found for reservation ID: " + res.getId());
                }
            }
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        }
    }

    private void deleteReservation(int reservationId, int tripId, int seatsReserved) {
<<<<<<< HEAD
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this reservation?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM reservations WHERE id = ? AND passenger_id = ?")) {
                pstmt.setInt(1, reservationId);
                pstmt.setInt(2, passengerId);
                int deleted = pstmt.executeUpdate();
                if (deleted > 0) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(
                            "UPDATE trips SET seats_available = seats_available + ? WHERE id = ?")) {
                        updateStmt.setInt(1, seatsReserved);
                        updateStmt.setInt(2, tripId);
                        updateStmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Reservation deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadReservations();
                        if (getParent() instanceof PassengerDashboard) {
                            ((PassengerDashboard) getParent()).loadAvailableTrips();
                        }
                    }
                }
            }
        } catch (SQLException ex) {
=======
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this reservation?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            new ReservationDAO().deleteReservation(reservationId, passengerId);
            new TripDAO().increaseSeats(tripId, seatsReserved);
            JOptionPane.showMessageDialog(this, "Reservation deleted successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            loadReservations();
            if (getParent() instanceof PassengerDashboard) {
                ((PassengerDashboard) getParent()).loadAvailableTrips();
            }
        } catch (Exception ex) {
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            JOptionPane.showMessageDialog(this, "Error deleting reservation: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSeats(int reservationId, int tripId, int currentSeatsReserved) {
        String seatsStr = JOptionPane.showInputDialog(this,
                "Update number of seats reserved (current: " + currentSeatsReserved + "):",
                "Edit Seats", JOptionPane.QUESTION_MESSAGE);
        if (seatsStr == null || seatsStr.trim().isEmpty()) return;

        int newSeatsReserved;
        try {
            newSeatsReserved = Integer.parseInt(seatsStr.trim());
            if (newSeatsReserved <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive number.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer!",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

<<<<<<< HEAD
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            int netChange = newSeatsReserved - currentSeatsReserved;

            String tripSql = "SELECT seats_available FROM trips WHERE id = ?";
            try (PreparedStatement tripStmt = conn.prepareStatement(tripSql)) {
                tripStmt.setInt(1, tripId);
                try (ResultSet rsTrip = tripStmt.executeQuery()) {
                    if (!rsTrip.next()) {
                        JOptionPane.showMessageDialog(this, "Trip not found.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    int available = rsTrip.getInt("seats_available");
                    if (netChange > available) {
                        JOptionPane.showMessageDialog(this,
                                "Only " + available + " additional seats are available!",
                                "Not Enough Seats", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE reservations SET seats_reserved = ? WHERE id = ? AND passenger_id = ?")) {
                updateStmt.setInt(1, newSeatsReserved);
                updateStmt.setInt(2, reservationId);
                updateStmt.setInt(3, passengerId);
                int updated = updateStmt.executeUpdate();

                if (updated > 0) {
                    try (PreparedStatement adjustStmt = conn.prepareStatement(
                            "UPDATE trips SET seats_available = seats_available - ? WHERE id = ?")) {
                        adjustStmt.setInt(1, netChange);
                        adjustStmt.setInt(2, tripId);
                        adjustStmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Seats updated successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadReservations();
                        if (getParent() instanceof PassengerDashboard) {
                            ((PassengerDashboard) getParent()).loadAvailableTrips();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update seats.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
=======
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/carpool", "root", "")) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Check available seats for the trip
            String tripSql = "SELECT seats_available FROM trips WHERE id = ?";
            PreparedStatement tripStmt = conn.prepareStatement(tripSql);
            tripStmt.setInt(1, tripId);
            ResultSet rsTrip = tripStmt.executeQuery();
            if (!rsTrip.next()) {
                JOptionPane.showMessageDialog(this, "Trip not found.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int available = rsTrip.getInt("seats_available");

            int netChange = newSeatsReserved - currentSeatsReserved;
            if (available + currentSeatsReserved < newSeatsReserved) {
                JOptionPane.showMessageDialog(this,
                        "Only " + (available + currentSeatsReserved) + " seats are available!",
                        "Not Enough Seats", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Update reservation
            String updateSql = "UPDATE reservations SET seats_reserved = ? WHERE id = ? AND passenger_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setInt(1, newSeatsReserved);
            updateStmt.setInt(2, reservationId);
            updateStmt.setInt(3, passengerId);
            int updated = updateStmt.executeUpdate();

            if (updated > 0) {
                // Update trip seats
                String adjustSql = "UPDATE trips SET seats_available = seats_available + ? WHERE id = ?";
                PreparedStatement adjustStmt = conn.prepareStatement(adjustSql);
                adjustStmt.setInt(1, currentSeatsReserved - newSeatsReserved);
                adjustStmt.setInt(2, tripId);
                adjustStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Seats updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadReservations();
                if (getParent() instanceof PassengerDashboard) {
                    ((PassengerDashboard) getParent()).loadAvailableTrips();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update seats.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ClassNotFoundException | SQLException ex) {
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            JOptionPane.showMessageDialog(this, "Error updating seats: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

<<<<<<< HEAD
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
=======
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            setText(value == null ? "" : value.toString());
            return this;
        }
    }

<<<<<<< HEAD
    private class ReservationButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private int selectedRow;

        public ReservationButtonEditor(JCheckBox checkBox) {
            super(checkBox);
=======
    private abstract class BaseButtonEditor extends DefaultCellEditor {
        protected JButton button;
        protected String label;
        protected boolean clicked;
        protected int selectedRow;
        protected JTable table;

        public BaseButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            this.table = table;
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
<<<<<<< HEAD
            label = value == null ? "" : value.toString();
=======
            label = (value == null) ? "" : value.toString();
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
            button.setText(label);
            clicked = true;
            selectedRow = row;
            return button;
        }

<<<<<<< HEAD
        @Override
        public Object getCellEditorValue() {
            if (clicked && selectedRow < tableModel.getRowCount()) {
                int reservationId = (int) tableModel.getValueAt(selectedRow, 0);
                int tripId = (int) tableModel.getValueAt(selectedRow, 1);
                int seatsReserved = (int) tableModel.getValueAt(selectedRow, 6);
=======
        protected boolean isValidRow() {
            if (table.getRowCount() == 0 || selectedRow < 0 || selectedRow >= table.getRowCount()) {
                System.err.println("Invalid row access: selectedRow=" + selectedRow + ", rowCount=" + table.getRowCount());
                JOptionPane.showMessageDialog(MyReservations.this, "No valid reservations to process or invalid selection.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
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

    private class ReservationButtonEditor extends BaseButtonEditor {
        public ReservationButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox, table);
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked && isValidRow()) {
                int reservationId = (int) table.getModel().getValueAt(selectedRow, 0);
                int tripId = (int) table.getModel().getValueAt(selectedRow, 1);
                int seatsReserved = (int) table.getModel().getValueAt(selectedRow, 7);
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
                if ("Delete".equals(label)) {
                    deleteReservation(reservationId, tripId, seatsReserved);
                }
            }
            clicked = false;
            return label;
        }
<<<<<<< HEAD

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }

    private class EditButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private int selectedRow;

        public EditButtonEditor(JCheckBox checkBox) {
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
=======
    }

    private class EditButtonEditor extends BaseButtonEditor {
        public EditButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox, table);
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        }

        @Override
        public Object getCellEditorValue() {
<<<<<<< HEAD
            if (clicked && selectedRow < tableModel.getRowCount()) {
                int reservationId = (int) tableModel.getValueAt(selectedRow, 0);
                int tripId = (int) tableModel.getValueAt(selectedRow, 1);
                int seatsReserved = (int) tableModel.getValueAt(selectedRow, 6);
=======
            if (clicked && isValidRow()) {
                int reservationId = (int) table.getModel().getValueAt(selectedRow, 0);
                int tripId = (int) table.getModel().getValueAt(selectedRow, 1);
                int seatsReserved = (int) table.getModel().getValueAt(selectedRow, 7);
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
                if ("Edit Seats".equals(label)) {
                    editSeats(reservationId, tripId, seatsReserved);
                }
            }
            clicked = false;
            return label;
        }
<<<<<<< HEAD

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
=======
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
    }
}