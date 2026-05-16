package service;

import model.IngredientConflictRule;
import model.RoutineStep;
import model.SkincareRoutine;
import model.SkinProfile;
import model.AuditLog;
import repository.AuditLogRepository;
import repository.ConflictRuleRepository;
import repository.RoutineRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Detects ingredient conflicts in user routines.
 * GRASP Protected Variation: rules from DB not hardcoded
 * GRASP High Cohesion: only handles conflict logic
 */
public class IngredientConflictService {
    private final ConflictRuleRepository conflictRepo;
    private final RoutineRepository routineRepo;
    private final AuditLogRepository auditRepo;

    public IngredientConflictService() {
        this.conflictRepo = new ConflictRuleRepository();
        this.routineRepo = new RoutineRepository();
        this.auditRepo = new AuditLogRepository();
    }

    /**
     * Check product ingredients for conflicts.
     * @param productIngredients ingredients of new product
     * @param userProfile user profile with allergies
     * @return ConflictResult with conflicts and allergens
     */
    public ConflictResult checkConflicts(List<String> productIngredients, SkinProfile userProfile) {
        List<IngredientConflictRule> rules = conflictRepo.findAll();
        List<String> routineIngredients = getActiveRoutineIngredients(userProfile.getUserID());

        Set<String> combined = new HashSet<>();
        if (productIngredients != null) {
            productIngredients.forEach(i -> combined.add(i.toLowerCase()));
        }
        if (routineIngredients != null) {
            routineIngredients.forEach(i -> combined.add(i.toLowerCase()));
        }

        List<IngredientConflictRule> hits = new ArrayList<>();
        for (IngredientConflictRule rule : rules) {
            boolean allPresent = true;
            for (String required : rule.getInteractingIngredients()) {
                boolean found = containsIngredient(combined, required);
                if (!found) {
                    allPresent = false;
                    break;
                }
            }
            if (allPresent) {
                hits.add(rule);
            }
        }

        List<String> allergenHits = new ArrayList<>();
        List<String> allergies = userProfile.getKnownAllergies();
        if (allergies != null && productIngredients != null) {
            for (String allergy : allergies) {
                for (String ingredient : productIngredients) {
                    if (ingredient.toLowerCase().contains(allergy.toLowerCase())) {
                        allergenHits.add(allergy);
                        break;
                    }
                }
            }
        }

        boolean safe = hits.isEmpty() && allergenHits.isEmpty();
        return new ConflictResult(hits, allergenHits, safe);
    }

    /** Get all ingredients in user's active routine */
    private List<String> getActiveRoutineIngredients(String userID) {
        SkincareRoutine latest = routineRepo.findLatestByUser(userID);
        if (latest == null || latest.getSteps() == null) {
            return Collections.emptyList();
        }
        List<String> ingredients = new ArrayList<>();
        for (RoutineStep step : latest.getSteps()) {
            if (step.getRecommendedIngredient() != null) {
                ingredients.add(step.getRecommendedIngredient());
            }
        }
        return ingredients;
    }

    private boolean containsIngredient(Set<String> combined, String required) {
        String req = required.toLowerCase();
        for (String c : combined) {
            if (c.contains(req) || req.contains(c)) {
                return true;
            }
        }
        return false;
    }

    public boolean logAcknowledgement(String userID, String productName) {
        return auditRepo.save(new AuditLog(
            UUID.randomUUID().toString(),
            userID,
            "CONFLICT_ACKNOWLEDGED for product: " + productName,
            userID,
            LocalDateTime.now()
        ));
    }
}
