package utils;

public class AuthUtils {
    private static AuthUtils instance;
    private UserInfo currentUser;

    private AuthUtils() {}

    public static AuthUtils getInstance() {
        if (instance == null) {
            instance = new AuthUtils();
        }
        return instance;
    }

    public void setCurrentUser(UserInfo user) {
        this.currentUser = user;
    }

    public UserInfo getCurrentUser() {
        return currentUser;
    }
}