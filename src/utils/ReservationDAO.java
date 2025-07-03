package utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/carpool";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public List<Reservation> getReservationsByPassengerId(int passengerId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE passenger_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, passengerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Reservation res = new Reservation();
                res.setId(rs.getInt("id"));
                res.setTripId(rs.getInt("trip_id"));
                res.setPassengerId(rs.getInt("passenger_id"));
                res.setSeatsReserved(rs.getInt("seats_reserved"));
                res.setReservedAt(rs.getTimestamp("reserved_at"));
                reservations.add(res);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error fetching reservations: " + ex.getMessage(), ex);
        }
        return reservations;
    }

    public void createReservation(int passengerId, int tripId, int seats) {
        String sql = "INSERT INTO reservations (trip_id, passenger_id, seats_reserved) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tripId);
            pstmt.setInt(2, passengerId);
            pstmt.setInt(3, seats);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Error creating reservation: " + ex.getMessage(), ex);
        }
    }

    public void deleteReservation(int reservationId, int passengerId) {
        String sql = "DELETE FROM reservations WHERE id = ? AND passenger_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservationId);
            pstmt.setInt(2, passengerId);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Error deleting reservation: " + ex.getMessage(), ex);
        }
    }
}