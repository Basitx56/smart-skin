package model;

import java.time.LocalDateTime;

public class Notification {
    private String notifID;
    private String userID;
    private String title;
    private String body;
    private boolean isRead;
    private LocalDateTime createdAt;

    public Notification(String notifID,
                        String userID,
                        String title,
                        String body,
                        boolean isRead,
                        LocalDateTime createdAt) {
        this.notifID = notifID;
        this.userID = userID;
        this.title = title;
        this.body = body;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public String getNotifID() {
        return notifID;
    }

    public void setNotifID(String notifID) {
        this.notifID = notifID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
