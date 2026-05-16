package model;

import java.time.LocalDateTime;
import java.util.List;

public class IngredientConflictRule {
    private String ruleID;
    private List<String> interactingIngredients;
    private String safetyLevel;
    private String adviceText;
    private boolean expertReviewed;
    private LocalDateTime lastUpdated;

    public IngredientConflictRule(String ruleID,
                                  List<String> interactingIngredients,
                                  String safetyLevel,
                                  String adviceText,
                                  boolean expertReviewed,
                                  LocalDateTime lastUpdated) {
        this.ruleID = ruleID;
        this.interactingIngredients = interactingIngredients;
        this.safetyLevel = safetyLevel;
        this.adviceText = adviceText;
        this.expertReviewed = expertReviewed;
        this.lastUpdated = lastUpdated;
    }

    public String getRuleID() {
        return ruleID;
    }

    public void setRuleID(String ruleID) {
        this.ruleID = ruleID;
    }

    public List<String> getInteractingIngredients() {
        return interactingIngredients;
    }

    public void setInteractingIngredients(List<String> interactingIngredients) {
        this.interactingIngredients = interactingIngredients;
    }

    public String getSafetyLevel() {
        return safetyLevel;
    }

    public void setSafetyLevel(String safetyLevel) {
        this.safetyLevel = safetyLevel;
    }

    public String getAdviceText() {
        return adviceText;
    }

    public void setAdviceText(String adviceText) {
        this.adviceText = adviceText;
    }

    public boolean isExpertReviewed() {
        return expertReviewed;
    }

    public void setExpertReviewed(boolean expertReviewed) {
        this.expertReviewed = expertReviewed;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
