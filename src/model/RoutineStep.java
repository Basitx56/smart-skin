package model;

public class RoutineStep {
    private int stepNumber;
    private String purpose;
    private String recommendedIngredient;
    private String recommendedProduct;
    private String explanation;

    public RoutineStep(int stepNumber,
                       String purpose,
                       String recommendedIngredient,
                       String recommendedProduct,
                       String explanation) {
        this.stepNumber = stepNumber;
        this.purpose = purpose;
        this.recommendedIngredient = recommendedIngredient;
        this.recommendedProduct = recommendedProduct;
        this.explanation = explanation;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getRecommendedIngredient() {
        return recommendedIngredient;
    }

    public void setRecommendedIngredient(String recommendedIngredient) {
        this.recommendedIngredient = recommendedIngredient;
    }

    public String getRecommendedProduct() {
        return recommendedProduct;
    }

    public void setRecommendedProduct(String recommendedProduct) {
        this.recommendedProduct = recommendedProduct;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
