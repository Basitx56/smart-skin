package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProgressEntry {
    private String entryID;
    private String userID;
    private LocalDate entryDate;
    private int acneLevel;
    private int dryness;
    private int pigmentation;
    private int irritation;
    private String photoPath;
    private String notes;
    private LocalDateTime createdAt;

    public ProgressEntry(String entryID,
                         String userID,
                         LocalDate entryDate,
                         int acneLevel,
                         int dryness,
                         int pigmentation,
                         int irritation,
                         String photoPath,
                         String notes,
                         LocalDateTime createdAt) {
        this.entryID = entryID;
        this.userID = userID;
        this.entryDate = entryDate;
        this.acneLevel = acneLevel;
        this.dryness = dryness;
        this.pigmentation = pigmentation;
        this.irritation = irritation;
        this.photoPath = photoPath;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public String getEntryID() {
        return entryID;
    }

    public void setEntryID(String entryID) {
        this.entryID = entryID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public int getAcneLevel() {
        return acneLevel;
    }

    public void setAcneLevel(int acneLevel) {
        this.acneLevel = acneLevel;
    }

    public int getDryness() {
        return dryness;
    }

    public void setDryness(int dryness) {
        this.dryness = dryness;
    }

    public int getPigmentation() {
        return pigmentation;
    }

    public void setPigmentation(int pigmentation) {
        this.pigmentation = pigmentation;
    }

    public int getIrritation() {
        return irritation;
    }

    public void setIrritation(int irritation) {
        this.irritation = irritation;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
