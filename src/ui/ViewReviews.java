package ui;

<<<<<<< HEAD
import utils.*;
=======
import utils.Review;
import utils.ReviewDAO;
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a

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
<<<<<<< HEAD

        UserInfo currentUser = AuthUtils.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.getId() != driverId) {
            JOptionPane.showMessageDialog(this, "You are not authorized to view these reviews", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

=======
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
        setSize(500, 300);
        initComponents();
        loadReviews();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
<<<<<<< HEAD
        tableModel = new DefaultTableModel(new String[]{"Review ID", "Passenger Name", "Rating", "Comment"}, 0);
=======
        tableModel = new DefaultTableModel(new String[]{"Review ID", "Passenger ID", "Rating", "Comment"}, 0);
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
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
<<<<<<< HEAD
                    review.getPassengerName(),
=======
                    review.getPassengerId(),
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
                    review.getRating(),
                    review.getComment()
            });
        }
    }
}