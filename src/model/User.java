package model;

import java.time.LocalDateTime;

/**
 * Abstract base class for all user types.
 * Demonstrates: Abstraction, Encapsulation (OOP)
 * GRASP: Information Expert - knows its own data
 */
public abstract class User {
    private String userID;
    private String name;
    private String email;
    private String password;
    private int age;
    private String gender;
    private String role;
    private boolean isActive;
    private LocalDateTime createdAt;

    public User(String userID, String name, String email,
                String password, int age, String gender,
                String role) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
        this.gender = gender;
        this.role = role;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    // Abstract method - Abstraction + Polymorphism (OOP)
    public abstract String getDashboardType();

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
