package ui;

import utils.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewReviews extends JDialog {
    private DefaultTableModel tableModel;
    private JTable reviewsTable;
    private final int driverId;

    public ViewReviews(JFrame parent, int driverId) {
        super(parent, "My Reviews", true);
        this.driverId = driverId;

        UserInfo currentUser = AuthUtils.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.getId() != driverId) {
            JOptionPane.showMessageDialog(this, "You are not authorized to view these reviews", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        setSize(500, 300);
        initComponents();
        loadReviews();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        tableModel = new DefaultTableModel(new String[]{"Review ID", "Passenger Name", "Rating", "Comment"}, 0);
        reviewsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(reviewsTable);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadReviews() {
        tableModel.setRowCount(0);
        List<Review> reviews = new ReviewDAO().getReviewsByDriverId(driverId);
        for (Review review : reviews) {
            tableModel.addRow(new Object[]{
                    review.getId(),
                    review.getPassengerName(),
                    review.getRating(),
                    review.getComment()
            });
        }
    }
}