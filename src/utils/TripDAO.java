package utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TripDAO {
    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/carpool";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }

    public List<Trip> searchTrips(String departure, String destination) {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT * FROM trips WHERE departure LIKE ? AND destination LIKE ? AND seats_available > 0";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + departure + "%");
            pstmt.setString(2, "%" + destination + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Trip trip = new Trip();
                trip.setId(rs.getInt("id"));
                trip.setDriverId(rs.getInt("driver_id"));
                trip.setDeparture(rs.getString("departure"));
                trip.setDestination(rs.getString("destination"));
                trip.setDate(rs.getDate("date"));
                trip.setTime(rs.getTime("time"));
                trip.setSeatsAvailable(rs.getInt("seats_available"));
                trip.setPrice(rs.getDouble("price"));
                trips.add(trip);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trips;
    }

    public Trip getTripById(int id) {
        String sql = "SELECT * FROM trips WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Trip trip = new Trip();
                trip.setId(rs.getInt("id"));
                trip.setDriverId(rs.getInt("driver_id"));
                trip.setDeparture(rs.getString("departure"));
                trip.setDestination(rs.getString("destination"));
                trip.setDate(rs.getDate("date"));
                trip.setTime(rs.getTime("time"));
                trip.setSeatsAvailable(rs.getInt("seats_available"));
                trip.setPrice(rs.getDouble("price"));
                return trip;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createTrip(Trip trip) {
        String sql = "INSERT INTO trips (driver_id, departure, destination, date, time, seats_available, price) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, trip.getDriverId());
            pstmt.setString(2, trip.getDeparture());
            pstmt.setString(3, trip.getDestination());
            pstmt.setDate(4, trip.getDate());
            pstmt.setTime(5, trip.getTime());
            pstmt.setInt(6, trip.getSeatsAvailable());
            pstmt.setDouble(7, trip.getPrice());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTrip(Trip trip) {
        String sql = "UPDATE trips SET departure = ?, destination = ?, date = ?, time = ?, seats_available = ?, price = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, trip.getDeparture());
            pstmt.setString(2, trip.getDestination());
            pstmt.setDate(3, trip.getDate());
            pstmt.setTime(4, trip.getTime());
            pstmt.setInt(5, trip.getSeatsAvailable());
            pstmt.setDouble(6, trip.getPrice());
            pstmt.setInt(7, trip.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTrip(int id) {
        String sql = "DELETE FROM trips WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void increaseSeats(int tripId, int seats) {
        String sql = "UPDATE trips SET seats_available = seats_available + ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, seats);
            pstmt.setInt(2, tripId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}