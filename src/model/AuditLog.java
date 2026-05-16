package model;

import java.time.LocalDateTime;

public class AuditLog {
    private String logID;
    private String actorID;
    private String action;
    private String targetID;
    private LocalDateTime timestamp;

    public AuditLog(String logID,
                    String actorID,
                    String action,
                    String targetID,
                    LocalDateTime timestamp) {
        this.logID = logID;
        this.actorID = actorID;
        this.action = action;
        this.targetID = targetID;
        this.timestamp = timestamp;
    }

    public String getLogID() {
        return logID;
    }

    public void setLogID(String logID) {
        this.logID = logID;
    }

    public String getActorID() {
        return actorID;
    }

    public void setActorID(String actorID) {
        this.actorID = actorID;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTargetID() {
        return targetID;
    }

    public void setTargetID(String targetID) {
        this.targetID = targetID;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
