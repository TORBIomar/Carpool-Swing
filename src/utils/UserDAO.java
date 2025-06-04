package utils;

import com.mysql.cj.xdevapi.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Data Access Object for User entity
 * Handles all database operations related to users
 */
public class UserDAO {
    private Connection connection;

    public UserDAO() {
        try {
            connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            System.err.println("Error obtaining database connection");
            e.printStackTrace();
        }
    }

    /**
     * Create a new user in the database
     * @param name User's name
     * @param email User's email
     * @param password User's password
     * @return The id of the newly created user, or -1 if creation failed
     */
    public int createUser(String name, String email, String password) {
        String query = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        int userId = -1;

        try (PreparedStatement pstmt = connection.prepareStatement(query, 1)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating user");
            e.printStackTrace();
        }

        return userId;
    }

    /**
     * Get user by ID
     * @param id User ID
     * @return User object, or null if user is not found
     */
   public User getUserById(int id) {
        User user = null;
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                user = new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("password"),
                    rs.getString("user_type"),
                    rs.getTimestamp("created_at")
                );
                user.setIdCardPath(rs.getString("id_card_path"));
                user.setVehicleRegistrationPath(rs.getString("vehicle_registration_path"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by ID");
            e.printStackTrace();
        }
        return user;
    }

    public User getUserByEmail(String email) {
        User user = null;
        String query = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                user = new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("password"),
                    rs.getString("user_type"),
                    rs.getTimestamp("created_at")
                );
                user.setIdCardPath(rs.getString("id_card_path"));
                user.setVehicleRegistrationPath(rs.getString("vehicle_registration_path"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by email");
            e.printStackTrace();
        }
        return user;
    }

    public boolean updateIdCardPath(int id, String path) {
        String query = "UPDATE users SET id_card_path = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, path);
            pstmt.setInt(2, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating ID card path");
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateVehicleRegistrationPath(int id, String path) {
        String query = "UPDATE users SET vehicle_registration_path = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, path);
            pstmt.setInt(2, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating vehicle registration path");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update user information
     * @param id User ID
     * @param name New name
     * @param email New email
     * @return true if update was successful, false otherwise
     */
    public boolean updateUser(int id, String name, String email) {
        String query = "UPDATE users SET name = ?, email = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setInt(3, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update user password
     * @param id User ID
     * @param password New password
     * @return true if update was successful, false otherwise
     */
    public boolean updatePassword(int id, String password) {
        String query = "UPDATE users SET password = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, password);
            pstmt.setInt(2, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a user from the database
     * @param id User ID
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteUser(int id) {
        String query = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Authenticate a user
     * @param email User's email
     * @param password User's password
     * @return User ID if authentication is successful, -1 otherwise
     */
    public int authenticateUser(String email, String password) {
        String query = "SELECT id FROM users WHERE email = ? AND password = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user");
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Check if a user is a driver (has created trips)
     * @param userId User ID
     * @return true if user has created trips, false otherwise
     */
    public boolean isDriver(int userId) {
        String query = "SELECT COUNT(*) FROM trips WHERE driver_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if user is driver");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if a user is a passenger (has made reservations)
     * @param userId User ID
     * @return true if user has made reservations, false otherwise
     */
    public boolean isPassenger(int userId) {
        String query = "SELECT COUNT(*) FROM reservations WHERE passenger_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if user is passenger");
            e.printStackTrace();
        }
        return false;
    }
}