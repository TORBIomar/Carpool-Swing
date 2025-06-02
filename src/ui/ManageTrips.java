package ui;

import utils.AuthUtils;
import utils.Trip;
import utils.TripManager;
import utils.User;

import javax.swing.*;
import java.awt.*;
import java.sql.Time;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;

public class ManageTrips extends JDialog {
    private JTextField departureField, destinationField;
    private JSpinner dateTimeSpinner, seatsSpinner, priceSpinner;
    private JButton saveButton, cancelButton;
    private final User currentUser;
    private final DriverDashboard parent;
    private Trip trip;

    public ManageTrips(DriverDashboard parent, Integer tripId) {
        super(parent, tripId == null ? "Create Trip" : "Edit Trip", true);
        this.parent = parent;

        currentUser = AuthUtils.getInstance().getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "You must be logged in", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        if (tripId != null) {
            trip = TripManager.getInstance().getTripById(tripId);
            if (trip == null) {
                JOptionPane.showMessageDialog(this, "Trip not found", "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }
        }

        initComponents();
        if (trip != null) loadTrip();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Departure:"));
        departureField = new JTextField();
        panel.add(departureField);

        panel.add(new JLabel("Destination:"));
        destinationField = new JTextField();
        panel.add(destinationField);

        panel.add(new JLabel("Date & Time:"));
        dateTimeSpinner = new JSpinner(new SpinnerDateModel(new java.util.Date(), null, null, Calendar.MINUTE));
        dateTimeSpinner.setEditor(new JSpinner.DateEditor(dateTimeSpinner, "yyyy-MM-dd HH:mm:ss"));
        panel.add(dateTimeSpinner);

        panel.add(new JLabel("Seats Available:"));
        seatsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        panel.add(seatsSpinner);

        panel.add(new JLabel("Price (€):"));
        priceSpinner = new JSpinner(new SpinnerNumberModel(10.0, 0.0, 1000.0, 0.5));
        panel.add(priceSpinner);

        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> dispose());
        panel.add(saveButton);
        panel.add(cancelButton);

        setContentPane(panel);
    }

    private void loadTrip() {
        if (trip == null) return;
        departureField.setText(trip.getDeparture());
        destinationField.setText(trip.getDestination());

        // Convert java.sql.Date and java.sql.Time to java.util.Date for JSpinner
        java.sql.Date sqlDate = trip.getDate();
        java.sql.Time sqlTime = trip.getTime();
        LocalDateTime localDateTime = sqlDate.toLocalDate().atTime(
            sqlTime.toLocalTime().getHour(),
            sqlTime.toLocalTime().getMinute(),
            sqlTime.toLocalTime().getSecond()
        );
        java.util.Date utilDate = java.util.Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        dateTimeSpinner.setValue(utilDate);

        seatsSpinner.setValue(trip.getSeatsAvailable());
        priceSpinner.setValue(trip.getPrice());
    }

    private void onSave() {
        try {
            String dep = departureField.getText().trim();
            String dest = destinationField.getText().trim();
            java.util.Date dt = (java.util.Date) dateTimeSpinner.getValue();
            int seats = (Integer) seatsSpinner.getValue();
            double price = (Double) priceSpinner.getValue();

            if (dep.isEmpty() || dest.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Departure and destination are required", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (dt == null) {
                JOptionPane.showMessageDialog(this, "Invalid date and time", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Convert java.util.Date to java.sql.Date and java.sql.Time
            LocalDateTime localDateTime = dt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            java.sql.Date sqlDate = java.sql.Date.valueOf(localDateTime.toLocalDate());
            java.sql.Time sqlTime = java.sql.Time.valueOf(localDateTime.toLocalTime());

            TripManager mgr = TripManager.getInstance();
            if (trip == null) {
                mgr.createTrip(currentUser.getId(), dep, dest, sqlDate, sqlTime, seats, price);
                JOptionPane.showMessageDialog(this, "Trip created!");
            } else {
                trip.setDeparture(dep);
                trip.setDestination(dest);
                trip.setDate(sqlDate);
                trip.setTime(sqlTime);
                trip.setSeatsAvailable(seats);
                trip.setPrice(price);
                mgr.updateTrip(trip);
                JOptionPane.showMessageDialog(this, "Trip updated!");
            }
            parent.loadUserTrips(); // Updated to match DriverDashboard method
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date or time format: " + ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving trip: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}