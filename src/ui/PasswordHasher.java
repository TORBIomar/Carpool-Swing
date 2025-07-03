package ui;

import java.security.MessageDigest;
import java.util.Base64;

public class PasswordHasher {
    public static void main(String[] args) throws Exception {
        String password = "admin"; // Replace with your chosen password
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes("UTF-8"));
        String hashedPassword = Base64.getEncoder().encodeToString(hash);
        System.out.println("Hashed password: " + hashedPassword);
    }
}