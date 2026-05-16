package service;

import model.AuditLog;
import model.SkinProfile;
import repository.AuditLogRepository;
import repository.SkinProfileRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Handles skin profile business logic.
 * GRASP Information Expert: knows profile rules
 * GRASP High Cohesion: only handles skin profile logic
 */
public class SkinProfileService {
    private final SkinProfileRepository skinProfileRepo;
    private final AuditLogRepository auditRepo;

    public SkinProfileService() {
        this.skinProfileRepo = new SkinProfileRepository();
        this.auditRepo = new AuditLogRepository();
    }

    /**
     * Creates or updates a skin profile.
     * Calculates completeness before saving.
     */
    public SkinProfile saveProfile(String userID,
                                   String skinType, List<String> skinConcerns,
                                   List<String> knownAllergies,
                                   List<String> currentProducts,
                                   String diet, int sleepHours,
                                   String stressLevel, String sunExposure) {
        if (skinType == null || skinType.trim().isEmpty()) {
            throw new IllegalArgumentException("skinType is required");
        }

        SkinProfile existing = skinProfileRepo.findByUserID(userID);
        SkinProfile profile = new SkinProfile(
            existing == null ? UUID.randomUUID().toString() : existing.getProfileID(),
            userID,
            skinType,
            skinConcerns,
            knownAllergies,
            currentProducts,
            diet,
            sleepHours,
            stressLevel,
            sunExposure,
            0,
            LocalDateTime.now()
        );
        profile.calculateCompleteness();

        boolean ok = existing == null
            ? skinProfileRepo.save(profile)
            : skinProfileRepo.update(profile);

        if (!ok) {
            throw new IllegalStateException("Failed to save skin profile");
        }

        auditRepo.save(new AuditLog(
            UUID.randomUUID().toString(),
            userID,
            existing == null ? "CREATE_SKIN_PROFILE" : "UPDATE_SKIN_PROFILE",
            profile.getProfileID(),
            LocalDateTime.now()
        ));
        return profile;
    }

    public SkinProfile getProfile(String userID) {
        return skinProfileRepo.findByUserID(userID);
    }
}
