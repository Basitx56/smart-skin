package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a regular end user of the system.
 * Demonstrates: Inheritance, Polymorphism (OOP)
 */
public class EndUser extends User {
    private String skinProfileID;
    private List<String> favoriteProducts;
    private boolean notificationsEnabled;
    private String morningReminderTime;
    private String eveningReminderTime;

    public EndUser(String userID, String name,
                   String email, String password,
                   int age, String gender) {
        super(userID, name, email, password,
            age, gender, "end_user");
        this.favoriteProducts = new ArrayList<>();
        this.notificationsEnabled = false;
    }

    @Override
    public String getDashboardType() {
        return "user_dashboard";
    }

    public String getSkinProfileID() {
        return skinProfileID;
    }

    public void setSkinProfileID(String skinProfileID) {
        this.skinProfileID = skinProfileID;
    }

    public List<String> getFavoriteProducts() {
        return favoriteProducts;
    }

    public void setFavoriteProducts(List<String> favoriteProducts) {
        this.favoriteProducts = favoriteProducts;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public String getMorningReminderTime() {
        return morningReminderTime;
    }

    public void setMorningReminderTime(String morningReminderTime) {
        this.morningReminderTime = morningReminderTime;
    }

    public String getEveningReminderTime() {
        return eveningReminderTime;
    }

    public void setEveningReminderTime(String eveningReminderTime) {
        this.eveningReminderTime = eveningReminderTime;
    }
}
