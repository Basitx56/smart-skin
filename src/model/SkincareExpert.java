package model;

/**
 * Represents a skincare expert/dermatologist.
 * Demonstrates: Inheritance, Polymorphism (OOP)
 */
public class SkincareExpert extends User {
    private String specialization;
    private double consultationFee;
    private double rating;
    private boolean isVerified;

    public SkincareExpert(String userID, String name,
                          String email, String password,
                          int age, String gender,
                          String specialization,
                          double consultationFee) {
        super(userID, name, email, password,
            age, gender, "expert");
        this.specialization = specialization;
        this.consultationFee = consultationFee;
        this.rating = 0.0;
        this.isVerified = false;
    }

    @Override
    public String getDashboardType() {
        return "expert_dashboard";
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }
}
