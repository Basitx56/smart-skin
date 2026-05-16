package service;

import model.IngredientConflictRule;

import java.util.List;

public class ConflictResult {
    private final List<IngredientConflictRule> conflicts;
    private final List<String> allergenHits;
    private final boolean isSafe;

    public ConflictResult(List<IngredientConflictRule> conflicts,
                          List<String> allergenHits,
                          boolean isSafe) {
        this.conflicts = conflicts;
        this.allergenHits = allergenHits;
        this.isSafe = isSafe;
    }

    public List<IngredientConflictRule> getConflicts() {
        return conflicts;
    }

    public List<String> getAllergenHits() {
        return allergenHits;
    }

    public boolean isSafe() {
        return isSafe;
    }
}
