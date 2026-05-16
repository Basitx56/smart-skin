package service;

import model.RoutineStep;
import model.SkincareRoutine;
import model.SkinProfile;
import model.ValidationQueueItem;
import repository.RoutineRepository;
import repository.SkinProfileRepository;
import repository.ValidationQueueRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Generates personalized skincare routines.
 * GOF Strategy Pattern: different strategy per skin type
 * GOF Factory Pattern: RoutineStrategyFactory
 * GRASP Creator: creates SkincareRoutine objects
 */
public class RoutineGeneratorService {
    private final RoutineRepository routineRepo;
    private final SkinProfileRepository skinProfileRepo;
    private final ValidationQueueRepository validationRepo;

    public RoutineGeneratorService() {
        this.routineRepo = new RoutineRepository();
        this.skinProfileRepo = new SkinProfileRepository();
        this.validationRepo = new ValidationQueueRepository();
    }

    /**
     * Generates morning and evening routine for user.
     * @throws IllegalStateException if profile incomplete
     */
    public Map<String, SkincareRoutine> generateRoutine(String userID) {
        SkinProfile profile = skinProfileRepo.findByUserID(userID);
        if (profile == null) {
            throw new IllegalStateException("Skin profile not found");
        }

        int completeness = profile.calculateCompleteness();
        if (completeness < 40) {
            throw new IllegalStateException("Profile incomplete: minimum 40% required");
        }

        RoutineStrategy strategy = RoutineStrategyFactory.getStrategy(profile.getSkinType());
        List<RoutineStep> morningSteps = filterAllergens(
            strategy.generateMorning(profile),
            profile.getKnownAllergies()
        );
        List<RoutineStep> eveningSteps = filterAllergens(
            strategy.generateEvening(profile),
            profile.getKnownAllergies()
        );

        SkincareRoutine morningRoutine = new SkincareRoutine(
            UUID.randomUUID().toString(),
            userID,
            "morning",
            morningSteps,
            LocalDateTime.now(),
            false,
            null
        );
        SkincareRoutine eveningRoutine = new SkincareRoutine(
            UUID.randomUUID().toString(),
            userID,
            "evening",
            eveningSteps,
            LocalDateTime.now(),
            false,
            null
        );

        if (!routineRepo.save(morningRoutine) || !routineRepo.save(eveningRoutine)) {
            throw new IllegalStateException("Failed to persist generated routines");
        }

        validationRepo.save(new ValidationQueueItem(
            UUID.randomUUID().toString(),
            morningRoutine.getRoutineID(),
            userID,
            "pending",
            LocalDateTime.now(),
            null,
            null,
            null
        ));
        validationRepo.save(new ValidationQueueItem(
            UUID.randomUUID().toString(),
            eveningRoutine.getRoutineID(),
            userID,
            "pending",
            LocalDateTime.now(),
            null,
            null,
            null
        ));

        Map<String, SkincareRoutine> result = new HashMap<>();
        result.put("morning", morningRoutine);
        result.put("evening", eveningRoutine);
        return result;
    }

    /**
     * Filter steps that contain user allergens.
     */
    private List<RoutineStep> filterAllergens(List<RoutineStep> steps, List<String> allergies) {
        if (steps == null || allergies == null || allergies.isEmpty()) {
            return steps;
        }

        steps.removeIf(step -> {
            String haystack = (step.getRecommendedIngredient() + " " + step.getRecommendedProduct()).toLowerCase();
            for (String allergy : allergies) {
                if (allergy != null && !allergy.trim().isEmpty() && haystack.contains(allergy.toLowerCase())) {
                    return true;
                }
            }
            return false;
        });

        for (int i = 0; i < steps.size(); i++) {
            steps.get(i).setStepNumber(i + 1);
        }
        return steps;
    }
}
