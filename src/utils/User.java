package utils;

import java.sql.Timestamp;

public class User {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String userType; // Enum values: DRIVER, PASSENGER, BOTH
    private Timestamp createdAt;
    private String idCardPath; // New field
    private String vehicleRegistrationPath; // New field

    // Constructor
    public User(int id, String name, String email, String phone, String password, String userType, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.userType = userType;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public String getIdCardPath() { return idCardPath; }
    public void setIdCardPath(String idCardPath) { this.idCardPath = idCardPath; }
    public String getVehicleRegistrationPath() { return vehicleRegistrationPath; }
    public void setVehicleRegistrationPath(String vehicleRegistrationPath) { this.vehicleRegistrationPath = vehicleRegistrationPath; }
}