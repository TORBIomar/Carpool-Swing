package utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/carpooling_db";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }

    public List<Review> getReviewsByDriverId(int driverId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE driver_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, driverId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Review review = new Review();
                review.setId(rs.getInt("id"));
                review.setDriverId(rs.getInt("driver_id"));
                review.setPassengerId(rs.getInt("passenger_id"));
                review.setRating(rs.getInt("rating"));
                review.setComment(rs.getString("comment"));
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }
}