package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;
import javax.imageio.ImageIO;

import ui.LoginForm;
import utils.UserInfo;

public class AdminDashboard extends JFrame {
    private JTable applicationsTable, usersTable, reclamationsTable;
    private DefaultTableModel applicationsTableModel, usersTableModel, reclamationsTableModel;
    private UserInfo currentUser;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/carpool";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public AdminDashboard(UserInfo userInfo) {
        this.currentUser = userInfo;
        initializeComponents();
        initializeUserManagementComponents();
        initializeReclamationsComponents();
        setupLayout();
        loadPendingApplications();
        loadUsers();
        loadReclamations();

        setTitle("Admin Dashboard - " + userInfo.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        pack();
        setLocationFlat();
    }

    private void setLocationFlat() {
    }

    public static String getDbUrl() {
        return DB_URL;
    }

    public static String getDbUser() {
        return DB_USER;
    }

    public static String getDbPassword() {
        return DB_PASSWORD;
    }

    private void initializeComponents() {
        String[] columnNames = {"User ID", "Name", "Email", "View Documents", "Approve", "Decline"};
        applicationsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 3;
            }
        };
        applicationsTable = new JTable(applicationsTableModel);
        applicationsTable.setRowHeight(30);
        applicationsTable.getColumn("View Documents").setCellRenderer(new ButtonRenderer());
        applicationsTable.getColumn("View Documents").setCellEditor(new ViewButtonEditor(new JCheckBox()));
        applicationsTable.getColumn("Approve").setCellRenderer(new ButtonRenderer());
        applicationsTable.getColumn("Approve").setCellEditor(new ApproveButtonEditor(new JCheckBox()));
        applicationsTable.getColumn("Decline").setCellRenderer(new ButtonRenderer());
        applicationsTable.getColumn("Decline").setCellEditor(new DeclineButtonEditor(new JCheckBox()));

        applicationsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        applicationsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        applicationsTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        applicationsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        applicationsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        applicationsTable.getColumnModel().getColumn(5).setPreferredWidth(80);
    }

    private void initializeUserManagementComponents() {
        String[] userColumnNames = {"User ID", "Name", "Email", "Role", "Activate/Suspend", "Delete"};
        usersTableModel = new DefaultTableModel(userColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 4;
            }
        };
        usersTable = new JTable(usersTableModel);
        usersTable.setRowHeight(30);
        usersTable.getColumn("Activate/Suspend").setCellRenderer(new ButtonRenderer());
        usersTable.getColumn("Activate/Suspend").setCellEditor(new ActivateSuspendButtonEditor(new JCheckBox()));
        usersTable.getColumn("Delete").setCellRenderer(new ButtonRenderer());
        usersTable.getColumn("Delete").setCellEditor(new DeleteButtonEditor(new JCheckBox()));

        usersTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        usersTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        usersTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        usersTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        usersTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        usersTable.getColumnModel().getColumn(5).setPreferredWidth(80);
    }

    private void initializeReclamationsComponents() {
        String[] reclamationColumnNames = {"Reclamation ID", "User", "Description", "Status", "Resolve"};
        reclamationsTableModel = new DefaultTableModel(reclamationColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        reclamationsTable = new JTable(reclamationsTableModel);
        reclamationsTable.setRowHeight(30);
        reclamationsTable.getColumn("Resolve").setCellRenderer(new ButtonRenderer());
        reclamationsTable.getColumn("Resolve").setCellEditor(new ResolveReclamationButtonEditor(new JCheckBox()));

        reclamationsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        reclamationsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        reclamationsTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        reclamationsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        reclamationsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Welcome, " + currentUser.getName() + " (Admin)"));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        topPanel.add(logoutButton);
        JButton generateReportButton = new JButton("Generate Report");
        generateReportButton.addActionListener(e -> generateReport());
        topPanel.add(generateReportButton);
        JButton analyzeRevenueButton = new JButton("Analyze Revenue");
        analyzeRevenueButton.addActionListener(e -> analyzeRevenue());
        topPanel.add(analyzeRevenueButton);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel applicationsPanel = new JPanel(new BorderLayout());
        JScrollPane applicationsScrollPane = new JScrollPane(applicationsTable);
        applicationsScrollPane.setBorder(BorderFactory.createTitledBorder("Pending Driver Applications"));
        applicationsPanel.add(applicationsScrollPane, BorderLayout.CENTER);
        tabbedPane.addTab("Driver Applications", applicationsPanel);

        JPanel userManagementPanel = new JPanel(new BorderLayout());
        JScrollPane usersScrollPane = new JScrollPane(usersTable);
        usersScrollPane.setBorder(BorderFactory.createTitledBorder("All Users"));
        userManagementPanel.add(usersScrollPane, BorderLayout.CENTER);

        JPanel userButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addUserButton = new JButton("Add User");
        addUserButton.addActionListener(e -> showAddUserDialog());
        userButtonsPanel.add(addUserButton);
        JButton refreshUsersButton = new JButton("Refresh");
        refreshUsersButton.addActionListener(e -> loadUsers());
        userButtonsPanel.add(refreshUsersButton);
        userManagementPanel.add(userButtonsPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("User Management", userManagementPanel);

        JPanel reclamationsPanel = new JPanel(new BorderLayout());
        JScrollPane reclamationsScrollPane = new JScrollPane(reclamationsTable);
        reclamationsScrollPane.setBorder(BorderFactory.createTitledBorder("Reclamations"));
        reclamationsPanel.add(reclamationsScrollPane, BorderLayout.CENTER);
        tabbedPane.addTab("Reclamations", reclamationsPanel);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void loadPendingApplications() {
        try (Connection conn = DriverManager.getConnection(getDbUrl(), getDbUser(), getDbPassword());
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT id, name, email FROM users WHERE application_status = 'PENDING' AND (user_type = 'DRIVER' OR user_type = 'BOTH')")) {
            try (ResultSet rs = pstmt.executeQuery()) {
                applicationsTableModel.setRowCount(0);
                while (rs.next()) {
                    applicationsTableModel.addRow(new Object[]{
                            rs.getInt("id"), rs.getString("name"), rs.getString("email"), "View", "Approve", "Decline"
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading pending applications: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadUsers() {
        try (Connection conn = DriverManager.getConnection(getDbUrl(), getDbUser(), getDbPassword());
             PreparedStatement pstmt = conn.prepareStatement("SELECT id, name, email, user_type FROM users")) {
            try (ResultSet rs = pstmt.executeQuery()) {
                usersTableModel.setRowCount(0);
                while (rs.next()) {
                    usersTableModel.addRow(new Object[]{
                            rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("user_type"), "Activate/Suspend", "Delete"
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadReclamations() {
        try (Connection conn = DriverManager.getConnection(getDbUrl(), getDbUser(), getDbPassword());
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT id, user_id, description, status FROM claims")) {
            try (ResultSet rs = pstmt.executeQuery()) {
                reclamationsTableModel.setRowCount(0);
                while (rs.next()) {
                    String userName = getUserName(rs.getInt("user_id"));
                    reclamationsTableModel.addRow(new Object[]{
                            rs.getInt("id"),
                            userName,
                            rs.getString("description"),
                            rs.getString("status") != null ? rs.getString("status") : "Pending",
                            "Resolve"
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading reclamations: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getUserName(int userId) {
        try (Connection conn = DriverManager.getConnection(getDbUrl(), getDbUser(), getDbPassword());
             PreparedStatement pstmt = conn.prepareStatement("SELECT name FROM users WHERE id = ?")) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getString("name") : "Unknown";
            }
        } catch (SQLException ex) {
            return "Unknown";
        }
    }

    private void showAddUserDialog() {
        AddUserDialog dialog = new AddUserDialog(this);
        dialog.setVisible(true);
        if (dialog.isUserAdded()) {
            loadUsers();
        }
    }

    private void deleteUser(int userId) {
        try (Connection conn = DriverManager.getConnection(getDbUrl(), getDbUser(), getDbPassword());
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
            pstmt.setInt(1, userId);
            if (pstmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "User deleted successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "User not found.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDocuments(int userId) {
        try (Connection conn = DriverManager.getConnection(getDbUrl(), getDbUser(), getDbPassword());
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT id_card_path, vehicle_registration_path FROM users WHERE id = ?")) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String idCardPath = rs.getString("id_card_path");
                    String vehicleRegPath = rs.getString("vehicle_registration_path");

                    JDialog dialog = new JDialog(this, "Driver Documents", true);
                    dialog.setLayout(new BorderLayout());
                    dialog.setSize(800, 600);
                    dialog.setLocationRelativeTo(this);

                    JPanel imagePanel = new JPanel(new GridLayout(2, 1, 10, 10));
                    JScrollPane scrollPane = new JScrollPane(imagePanel);
                    dialog.add(scrollPane, BorderLayout.CENTER);

                    // Display ID Card
                    JLabel idCardLabel = new JLabel("ID Card: Not uploaded");
                    if (idCardPath != null) {
                        try {
                            BufferedImage idCardImage = ImageIO.read(new File(idCardPath));
                            Image scaledImage = idCardImage.getScaledInstance(700, -1, Image.SCALE_SMOOTH);
                            idCardLabel.setIcon(new ImageIcon(scaledImage));
                            idCardLabel.setText("");
                        } catch (Exception e) {
                            idCardLabel.setText("Error loading ID Card image");
                        }
                    }
                    imagePanel.add(idCardLabel);

                    // Display Vehicle Registration
                    JLabel vehicleRegLabel = new JLabel("Vehicle Registration: Not uploaded");
                    if (vehicleRegPath != null) {
                        try {
                            BufferedImage vehicleRegImage = ImageIO.read(new File(vehicleRegPath));
                            Image scaledImage = vehicleRegImage.getScaledInstance(700, -1, Image.SCALE_SMOOTH);
                            vehicleRegLabel.setIcon(new ImageIcon(scaledImage));
                            vehicleRegLabel.setText("");
                        } catch (Exception e) {
                            vehicleRegLabel.setText("Error loading Vehicle Registration image");
                        }
                    }
                    imagePanel.add(vehicleRegLabel);

                    JButton closeButton = new JButton("Close");
                    closeButton.addActionListener(e -> dialog.dispose());
                    dialog.add(closeButton, BorderLayout.SOUTH);

                    dialog.setVisible(true);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching documents: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setApplicationStatus(int userId, String status) {
        try (Connection conn = DriverManager.getConnection(getDbUrl(), getDbUser(), getDbPassword());
             PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET application_status = ? WHERE id = ?")) {
            pstmt.setString(1, status);
            pstmt.setInt(2, userId);
            if (pstmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Application " + status.toLowerCase() + " successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPendingApplications();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating application status: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginForm().setVisible(true);
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

    private class ViewButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean clicked;
        private int selectedRow;

        public ViewButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText(value.toString());
            clicked = true;
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                int userId = (int) applicationsTableModel.getValueAt(selectedRow, 0);
                showDocuments(userId);
            }
            clicked = false;
            return "View";
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }

    private class ApproveButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean clicked;
        private int selectedRow;

        public ApproveButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText(value.toString());
            clicked = true;
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                int userId = (int) applicationsTableModel.getValueAt(selectedRow, 0);
                setApplicationStatus(userId, "APPROVED");
            }
            clicked = false;
            return "Approve";
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }

    private class DeclineButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean clicked;
        private int selectedRow;

        public DeclineButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText(value.toString());
            clicked = true;
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                int userId = (int) applicationsTableModel.getValueAt(selectedRow, 0);
                setApplicationStatus(userId, "DECLINED");
            }
            clicked = false;
            return "Decline";
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }

    private class DeleteButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean clicked;
        private int selectedRow;

        public DeleteButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText(value.toString());
            clicked = true;
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                int userId = (int) usersTableModel.getValueAt(selectedRow, 0);
                if (userId == currentUser.getId()) {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Cannot delete yourself.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    int confirm = JOptionPane.showConfirmDialog(AdminDashboard.this,
                            "Are you sure you want to delete this user?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        SwingUtilities.invokeLater(() -> {
                            deleteUser(userId);
                        });
                    }
                }
            }
            clicked = false;
            return "Delete";
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }

    private class ActivateSuspendButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean clicked;
        private int selectedRow;

        public ActivateSuspendButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText(value.toString());
            clicked = true;
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                int userId = (int) usersTableModel.getValueAt(selectedRow, 0);
                String currentStatus = getUserStatus(userId);
                String newStatus = "SUSPENDED".equals(currentStatus) ? "ACTIVE" : "SUSPENDED";
                setUserStatus(userId, newStatus);
            }
            clicked = false;
            return "Activate/Suspend";
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }

    private String getUserStatus(int userId) {
        try (Connection conn = DriverManager.getConnection(getDbUrl(), getDbUser(), getDbPassword());
             PreparedStatement pstmt = conn.prepareStatement("SELECT status FROM users WHERE id = ?")) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getString("status") : "ACTIVE";
            }
        } catch (SQLException ex) {
            return "ACTIVE";
        }
    }

    private void setUserStatus(int userId, String status) {
        try (Connection conn = DriverManager.getConnection(getDbUrl(), getDbUser(), getDbPassword());
             PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET status = ? WHERE id = ?")) {
            pstmt.setString(1, status);
            pstmt.setInt(2, userId);
            if (pstmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "User status updated to " + status.toLowerCase() + " successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating user status: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class ResolveReclamationButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean clicked;
        private int selectedRow;

        public ResolveReclamationButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText(value.toString());
            clicked = true;
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                int reclamationId = (int) reclamationsTableModel.getValueAt(selectedRow, 0);
                resolveReclamation(reclamationId);
            }
            clicked = false;
            return "Resolve";
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }

    private void resolveReclamation(int reclamationId) {
        JDialog dialog = new JDialog(this, "Resolve Reclamation #" + reclamationId, true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(2, 2, 10, 10));

        JTextField resolutionField = new JTextField(20);
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        dialog.add(new JLabel("Resolution:"));
        dialog.add(resolutionField);
        dialog.add(cancelButton);
        dialog.add(saveButton);

        cancelButton.addActionListener(e -> dialog.dispose());
        saveButton.addActionListener(e -> {
            String resolution = resolutionField.getText().trim();
            if (resolution.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Resolution is required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try (Connection conn = DriverManager.getConnection(getDbUrl(), getDbUser(), getDbPassword());
                 PreparedStatement pstmt = conn.prepareStatement(
                         "UPDATE claims SET status = 'RESOLVED', resolution = ? WHERE id = ?")) {
                pstmt.setString(1, resolution);
                pstmt.setInt(2, reclamationId);
                if (pstmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(dialog, "Reclamation resolved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadReclamations();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error resolving reclamation: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    private void generateReport() {
        JDialog dialog = new JDialog(this, "Generate Report", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(2, 2, 10, 10));

        JComboBox<String> reportType = new JComboBox<>(new String[]{"User Statistics", "Trip Statistics", "Revenue Report"});
        JButton generateButton = new JButton("Generate");
        JTextArea reportArea = new JTextArea(5, 20);
        reportArea.setEditable(false);

        dialog.add(new JLabel("Report Type:"));
        dialog.add(reportType);
        dialog.add(generateButton);
        dialog.add(new JScrollPane(reportArea));

        generateButton.addActionListener(e -> {
            String type = (String) reportType.getSelectedItem();
            String report = generateReportContent(type);
            reportArea.setText(report);
        });

        dialog.setVisible(true);
    }

    private String generateReportContent(String type) {
        StringBuilder report = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(getDbUrl(), getDbUser(), getDbPassword())) {
            if ("User Statistics".equals(type)) {
                PreparedStatement pstmt = conn.prepareStatement("SELECT user_type, COUNT(*) as count FROM users GROUP BY user_type");
                try (ResultSet rs = pstmt.executeQuery()) {
                    report.append("User Statistics Report\n");
                    while (rs.next()) {
                        report.append(rs.getString("user_type")).append(": ").append(rs.getInt("count")).append(" users\n");
                    }
                }
            } else if ("Trip Statistics".equals(type)) {
                PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) as count FROM trips");
                try (ResultSet rs = pstmt.executeQuery()) {
                    report.append("Trip Statistics Report\n");
                    if (rs.next()) {
                        report.append("Total Trips: ").append(rs.getInt("count")).append("\n");
                    }
                }
            } else if ("Revenue Report".equals(type)) {
                PreparedStatement pstmt = conn.prepareStatement("SELECT SUM(price) as total_revenue FROM trips");
                try (ResultSet rs = pstmt.executeQuery()) {
                    report.append("Revenue Report\n");
                    if (rs.next()) {
                        report.append("Total Revenue: €").append(rs.getDouble("total_revenue")).append("\n");
                    }
                }
            }
        } catch (SQLException ex) {
            report.append("Error generating report: ").append(ex.getMessage());
        }
        return report.toString();
    }

    private void analyzeRevenue() {
        JDialog dialog = new JDialog(this, "Analyze Revenue", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JTextArea resultArea = new JTextArea(5, 20);
        resultArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(resultArea);
        dialog.add(scroll, BorderLayout.CENTER);

        try (Connection conn = DriverManager.getConnection(getDbUrl(), getDbUser(), getDbPassword());
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT SUM(price) as total_revenue FROM trips WHERE date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)")) {
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double revenue = rs.getDouble("total_revenue");
                    resultArea.setText("Revenue (Last 30 Days): €" + (revenue > 0 ? revenue : "0.00"));
                }
            }
        } catch (SQLException ex) {
            resultArea.setText("Error analyzing revenue: " + ex.getMessage());
        }

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        dialog.add(closeButton, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}

class AddUserDialog extends JDialog {
    private JTextField nameField, emailField, phoneField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private boolean userAdded = false;

    public AddUserDialog(JFrame parent) {
        super(parent, "Add New User", true);
        setLayout(new GridLayout(6, 2, 10, 10));

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel("Phone:"));
        phoneField = new JTextField();
        add(phoneField);

        add(new JLabel("Role:"));
        roleComboBox = new JComboBox<>(new String[]{"PASSENGER", "DRIVER", "BOTH", "ADMIN"});
        add(roleComboBox);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addUser());
        add(addButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);

        pack();
        setLocationRelativeTo(parent);
    }

    private void addUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String phone = phoneField.getText().trim();
        String role = (String) roleComboBox.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(AdminDashboard.getDbUrl(), AdminDashboard.getDbUser(), AdminDashboard.getDbPassword());
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO users (name, email, password, phone, user_type, application_status) VALUES (?, ?, ?, ?, ?, NULL)")) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, phone);
            pstmt.setString(5, role);
            if (pstmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "User added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                userAdded = true;
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding user: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isUserAdded() {
        return userAdded;
    }
}
