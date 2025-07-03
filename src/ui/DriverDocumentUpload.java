package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DriverDocumentUpload extends JFrame {
    private JTextField idCardField, vehicleRegField;
    private JButton uploadIdCardButton, uploadVehicleRegButton, createTripButton;
    private String idCardPath, vehicleRegPath;
    private int userId;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/carpool";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public DriverDocumentUpload(int userId) {
        this.userId = userId;
        setTitle("Driver Document Upload");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));
        setSize(400, 200);

        idCardField = new JTextField(20);
        idCardField.setEditable(false);
        uploadIdCardButton = new JButton("Upload ID Card");
        uploadIdCardButton.addActionListener(new UploadListener("ID Card"));

        vehicleRegField = new JTextField(20);
        vehicleRegField.setEditable(false);
        uploadVehicleRegButton = new JButton("Upload Vehicle Registration");
        uploadVehicleRegButton.addActionListener(new UploadListener("Vehicle Registration"));

        createTripButton = new JButton("Create Trip");
        createTripButton.addActionListener(e -> openCreateTripDialog());

        add(new JLabel("ID Card:"));
        add(idCardField);
        add(uploadIdCardButton);
        add(new JLabel("Vehicle Registration:"));
        add(vehicleRegField);
        add(uploadVehicleRegButton);
        add(new JLabel(""));
        add(createTripButton);

        setLocationRelativeTo(null);
    }

    private class UploadListener implements ActionListener {
        private String documentType;

        public UploadListener(String documentType) {
            this.documentType = documentType;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (documentType.equals("ID Card")) {
                    idCardPath = selectedFile.getAbsolutePath();
                    idCardField.setText(idCardPath);
                    saveDocumentToDatabase("id_card_path", idCardPath);
                } else {
                    vehicleRegPath = selectedFile.getAbsolutePath();
                    vehicleRegField.setText(vehicleRegPath);
                    saveDocumentToDatabase("vehicle_registration_path", vehicleRegPath);
                }
                JOptionPane.showMessageDialog(null, documentType + " uploaded successfully!");
            }
        }
    }

    private void saveDocumentToDatabase(String column, String path) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "UPDATE users SET " + column + " = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, path);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error saving document: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openCreateTripDialog() {
        if (!areDocumentsUploaded()) {
            JOptionPane.showMessageDialog(this, "Please upload your ID card and vehicle registration first.",
                    "Documents Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Proceeding to trip creation...", "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean areDocumentsUploaded() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id_card_path, vehicle_registration_path FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String idCard = rs.getString("id_card_path");
                String vehicleReg = rs.getString("vehicle_registration_path");
                return idCard != null && !idCard.isEmpty() && vehicleReg != null && !vehicleReg.isEmpty();
            }
            return false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error checking documents: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DriverDocumentUpload(1).setVisible(true));
    }
}