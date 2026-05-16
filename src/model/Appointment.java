package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Appointment {
    private String appointmentID;
    private String userID;
    private String expertID;
    private LocalDate appointmentDate;
    private String timeSlot;
    private String type;
    private double fee;
    private String status;
    private LocalDateTime createdAt;

    public Appointment(String appointmentID,
                       String userID,
                       String expertID,
                       LocalDate appointmentDate,
                       String timeSlot,
                       String type,
                       double fee,
                       String status,
                       LocalDateTime createdAt) {
        this.appointmentID = appointmentID;
        this.userID = userID;
        this.expertID = expertID;
        this.appointmentDate = appointmentDate;
        this.timeSlot = timeSlot;
        this.type = type;
        this.fee = fee;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
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

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
