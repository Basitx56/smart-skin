package model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a user's skin profile.
 * GRASP: Information Expert - calculates its own
 * completeness percentage
 */
public class SkinProfile {
    private String profileID;
    private String userID;
    private String skinType;
    private List<String> skinConcerns;
    private List<String> knownAllergies;
    private List<String> currentProducts;
    private String diet;
    private int sleepHours;
    private String stressLevel;
    private String sunExposure;
    private int completenessPercentage;
    private LocalDateTime updatedAt;

    public SkinProfile(String profileID,
                       String userID,
                       String skinType,
                       List<String> skinConcerns,
                       List<String> knownAllergies,
                       List<String> currentProducts,
                       String diet,
                       int sleepHours,
                       String stressLevel,
                       String sunExposure,
                       int completenessPercentage,
                       LocalDateTime updatedAt) {
        this.profileID = profileID;
        this.userID = userID;
        this.skinType = skinType;
        this.skinConcerns = skinConcerns;
        this.knownAllergies = knownAllergies;
        this.currentProducts = currentProducts;
        this.diet = diet;
        this.sleepHours = sleepHours;
        this.stressLevel = stressLevel;
        this.sunExposure = sunExposure;
        this.completenessPercentage = completenessPercentage;
        this.updatedAt = updatedAt;
    }

    public String getProfileID() {
        return profileID;
    }

    public void setProfileID(String profileID) {
        this.profileID = profileID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getSkinType() {
        return skinType;
    }

    public void setSkinType(String skinType) {
        this.skinType = skinType;
    }

    public List<String> getSkinConcerns() {
        return skinConcerns;
    }

    public void setSkinConcerns(List<String> skinConcerns) {
        this.skinConcerns = skinConcerns;
    }

    public List<String> getKnownAllergies() {
        return knownAllergies;
    }

    public void setKnownAllergies(List<String> knownAllergies) {
        this.knownAllergies = knownAllergies;
    }

    public List<String> getCurrentProducts() {
        return currentProducts;
    }

    public void setCurrentProducts(List<String> currentProducts) {
        this.currentProducts = currentProducts;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public int getSleepHours() {
        return sleepHours;
    }

    public void setSleepHours(int sleepHours) {
        this.sleepHours = sleepHours;
    }

    public String getStressLevel() {
        return stressLevel;
    }

    public void setStressLevel(String stressLevel) {
        this.stressLevel = stressLevel;
    }

    public String getSunExposure() {
        return sunExposure;
    }

    public void setSunExposure(String sunExposure) {
        this.sunExposure = sunExposure;
    }

    public int getCompletenessPercentage() {
        return completenessPercentage;
    }

    public void setCompletenessPercentage(int completenessPercentage) {
        this.completenessPercentage = completenessPercentage;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Calculates profile completeness percentage.
     * GRASP Information Expert: SkinProfile knows
     * its own data best.
     *
     * @return int completeness score 0-100
     */
    public int calculateCompleteness() {
        int score = 0;
        if (skinType != null && !skinType.isEmpty()) {
            score += 20;
        }
        if (skinConcerns != null && !skinConcerns.isEmpty()) {
            score += 20;
        }
        if (knownAllergies != null) {
            score += 20;
        }
        if (currentProducts != null && !currentProducts.isEmpty()) {
            score += 20;
        }
        if (diet != null && !diet.isEmpty()) {
            score += 20;
        }
        this.completenessPercentage = score;
        return score;
    }
}
