package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {
    private JTable applicationsTable;
    private DefaultTableModel applicationsTableModel;
    private LoginForm.UserInfo currentUser;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/carpool";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public AdminDashboard(LoginForm.UserInfo userInfo) {
        this.currentUser = userInfo;
        initializeComponents();
        setupLayout();
        loadPendingApplications();

        setTitle("Admin Dashboard - " + userInfo.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
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
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Welcome, " + currentUser.getName() + " (Admin)"));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        topPanel.add(logoutButton);

        JScrollPane scrollPane = new JScrollPane(applicationsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Pending Driver Applications"));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadPendingApplications() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id, name, email FROM users WHERE application_status = 'PENDING' AND (user_type = 'DRIVER' OR user_type = 'BOTH')";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            applicationsTableModel.setRowCount(0);
            while (rs.next()) {
                int userId = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                applicationsTableModel.addRow(new Object[]{userId, name, email, "View", "Approve", "Decline"});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading pending applications: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDocuments(int userId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id_card_path, vehicle_registration_path FROM users WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String idCard = rs.getString("id_card_path");
                String vehicleReg = rs.getString("vehicle_registration_path");
                String msg = "ID Card: " + (idCard != null ? idCard : "Not uploaded") + "\n" +
                             "Vehicle Registration: " + (vehicleReg != null ? vehicleReg : "Not uploaded");
                JOptionPane.showMessageDialog(this, msg, "Documents", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching documents: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setApplicationStatus(int userId, String status) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "UPDATE users SET application_status = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, userId);
            int result = pstmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Application " + status.toLowerCase() + " successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPendingApplications();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update application status.",
                        "Error", JOptionPane.ERROR_MESSAGE);
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
}