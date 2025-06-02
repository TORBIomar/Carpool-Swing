package utils;

public class AuthUtils {
    private static AuthUtils instance;
    private User currentUser;

    private AuthUtils() {}

    public static AuthUtils getInstance() {
        if (instance == null) {
            instance = new AuthUtils();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}