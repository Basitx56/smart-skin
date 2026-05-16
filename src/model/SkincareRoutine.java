package model;

import java.time.LocalDateTime;
import java.util.List;

public class SkincareRoutine {
    private String routineID;
    private String userID;
    private String routineType;
    private List<RoutineStep> steps;
    private LocalDateTime generatedDate;
    private boolean isExpertValidated;
    private String expertID;

    public SkincareRoutine(String routineID,
                           String userID,
                           String routineType,
                           List<RoutineStep> steps,
                           LocalDateTime generatedDate,
                           boolean isExpertValidated,
                           String expertID) {
        this.routineID = routineID;
        this.userID = userID;
        this.routineType = routineType;
        this.steps = steps;
        this.generatedDate = generatedDate;
        this.isExpertValidated = isExpertValidated;
        this.expertID = expertID;
    }

    public String getRoutineID() {
        return routineID;
    }

    public void setRoutineID(String routineID) {
        this.routineID = routineID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getRoutineType() {
        return routineType;
    }

    public void setRoutineType(String routineType) {
        this.routineType = routineType;
    }

    public List<RoutineStep> getSteps() {
        return steps;
    }

    public void setSteps(List<RoutineStep> steps) {
        this.steps = steps;
    }

    public LocalDateTime getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDateTime generatedDate) {
        this.generatedDate = generatedDate;
    }

    public boolean isExpertValidated() {
        return isExpertValidated;
    }

    public void setExpertValidated(boolean expertValidated) {
        isExpertValidated = expertValidated;
    }

    public String getExpertID() {
        return expertID;
    }

    public void setExpertID(String expertID) {
        this.expertID = expertID;
    }
}
