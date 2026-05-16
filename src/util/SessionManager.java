package util;

import model.SkinProfile;
import model.User;

public class SessionManager {
    private static User currentUser;
    private static SkinProfile currentProfile;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentProfile(SkinProfile p) {
        currentProfile = p;
    }

    public static SkinProfile getCurrentProfile() {
        return currentProfile;
    }

    public static void clearSession() {
        currentUser = null;
        currentProfile = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
