package model;

/**
 * Represents a system administrator.
 * Demonstrates: Inheritance, Polymorphism (OOP)
 */
public class SystemAdmin extends User {
    public SystemAdmin(String userID, String name,
                       String email, String password,
                       int age, String gender) {
        super(userID, name, email, password,
            age, gender, "admin");
    }

    @Override
    public String getDashboardType() {
        return "admin_dashboard";
    }
}
