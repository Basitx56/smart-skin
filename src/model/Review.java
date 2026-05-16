package model;

import java.time.LocalDateTime;

public class Review {
    private String reviewID;
    private String userID;
    private String expertID;
    private String appointmentID;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public Review(String reviewID,
                  String userID,
                  String expertID,
                  String appointmentID,
                  int rating,
                  String comment,
                  LocalDateTime createdAt) {
        this.reviewID = reviewID;
        this.userID = userID;
        this.expertID = expertID;
        this.appointmentID = appointmentID;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public String getReviewID() {
        return reviewID;
    }

    public void setReviewID(String reviewID) {
        this.reviewID = reviewID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getExpertID() {
        return expertID;
    }

    public void setExpertID(String expertID) {
        this.expertID = expertID;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
