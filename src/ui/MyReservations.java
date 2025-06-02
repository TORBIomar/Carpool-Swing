package ui;

import utils.Reservation;
import utils.ReservationDAO;
import utils.Trip;
import utils.TripDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class MyReservations extends JDialog {
    private DefaultTableModel tableModel;
    private JTable reservationsTable;
    private final int passengerId;

    public MyReservations(JFrame parent, int passengerId) {
        super(parent, "My Reservations", true);
        this.passengerId = passengerId;
        setSize(700, 400);
        initComponents();
        loadReservations();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        tableModel = new DefaultTableModel(new String[]{"Reservation ID", "Trip ID", "Departure", "Destination", "Date", "Time", "Reserved At", "Seats Reserved", "Action", "Edit"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Disable editing if table is empty or invalid
                return tableModel.getRowCount() > 0 && (column == 8 || column == 9);
            }
        };
        reservationsTable = new JTable(tableModel);
        reservationsTable.setRowHeight(25);
        reservationsTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        reservationsTable.getColumn("Action").setCellEditor(new ReservationButtonEditor(new JCheckBox(), reservationsTable));
        reservationsTable.getColumn("Edit").setCellRenderer(new ButtonRenderer());
        reservationsTable.getColumn("Edit").setCellEditor(new EditButtonEditor(new JCheckBox(), reservationsTable));
        JScrollPane scrollPane = new JScrollPane(reservationsTable);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadReservations() {
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
        }
    }

    private void deleteReservation(int reservationId, int tripId, int seatsReserved) {
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
            JOptionPane.showMessageDialog(this, "Error updating seats: " + ex.getMessage(),
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

    private abstract class BaseButtonEditor extends DefaultCellEditor {
        protected JButton button;
        protected String label;
        protected boolean clicked;
        protected int selectedRow;
        protected JTable table;

        public BaseButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            this.table = table;
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
                if ("Delete".equals(label)) {
                    deleteReservation(reservationId, tripId, seatsReserved);
                }
            }
            clicked = false;
            return label;
        }
    }

    private class EditButtonEditor extends BaseButtonEditor {
        public EditButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox, table);
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked && isValidRow()) {
                int reservationId = (int) table.getModel().getValueAt(selectedRow, 0);
                int tripId = (int) table.getModel().getValueAt(selectedRow, 1);
                int seatsReserved = (int) table.getModel().getValueAt(selectedRow, 7);
                if ("Edit Seats".equals(label)) {
                    editSeats(reservationId, tripId, seatsReserved);
                }
            }
            clicked = false;
            return label;
        }
    }
}