package utils;

public class AuthUtils {
    private static AuthUtils instance;
<<<<<<< HEAD
    private UserInfo currentUser;
=======
    private User currentUser;
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a

    private AuthUtils() {}

    public static AuthUtils getInstance() {
        if (instance == null) {
            instance = new AuthUtils();
        }
        return instance;
    }

<<<<<<< HEAD
    public void setCurrentUser(UserInfo user) {
        this.currentUser = user;
    }

    public UserInfo getCurrentUser() {
        return currentUser;
    }
=======
    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
>>>>>>> f3b1b1c592740ac86847beaaa37f7a9a949dd01a
}