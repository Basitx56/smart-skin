package service;

import model.Notification;
import model.ProgressEntry;
import repository.NotificationRepository;
import repository.ProgressRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Handles skin progress tracking and insights.
 * GOF Observer: notifies when worsening trend detected
 * GRASP High Cohesion: only handles progress logic
 */
public class ProgressTrackingService {
    private final ProgressRepository progressRepo;
    private final NotificationRepository notifRepo;

    public ProgressTrackingService() {
        this.progressRepo = new ProgressRepository();
        this.notifRepo = new NotificationRepository();
    }

    public ProgressEntry addEntry(String userID,
                                  LocalDate date, int acneLevel,
                                  int dryness, int pigmentation, int irritation,
                                  String photoPath, String notes) {
        validateRating(acneLevel);
        validateRating(dryness);
        validateRating(pigmentation);
        validateRating(irritation);

        ProgressEntry entry = new ProgressEntry(
            UUID.randomUUID().toString(),
            userID,
            date,
            acneLevel,
            dryness,
            pigmentation,
            irritation,
            photoPath,
            notes,
            LocalDateTime.now()
        );
        if (!progressRepo.save(entry)) {
            throw new IllegalStateException("Failed to save progress entry");
        }

        List<ProgressEntry> entries = progressRepo.findByUser(userID);
        checkWorseningTrend(userID, entries);
        return entry;
    }

    /**
     * Generate insights from progress history.
     * @return Map of parameter to insight string
     */
    public Map<String, String> getInsights(String userID) {
        List<ProgressEntry> entries = progressRepo.findByUser(userID);
        Map<String, String> insights = new HashMap<>();
        if (entries.size() < 2) {
            insights.put("summary", "Not enough history yet. Add at least two entries for trend insights.");
            return insights;
        }

        ProgressEntry latest = entries.get(entries.size() - 1);
        LocalDate baselineDate = latest.getEntryDate().minusWeeks(4);
        ProgressEntry baseline = entries.get(0);
        for (ProgressEntry entry : entries) {
            if (!entry.getEntryDate().isAfter(baselineDate)) {
                baseline = entry;
            }
        }

        insights.put("acne", buildInsight("acne", baseline.getAcneLevel(), latest.getAcneLevel()));
        insights.put("dryness", buildInsight("dryness", baseline.getDryness(), latest.getDryness()));
        insights.put("pigmentation", buildInsight("pigmentation", baseline.getPigmentation(), latest.getPigmentation()));
        insights.put("irritation", buildInsight("irritation", baseline.getIrritation(), latest.getIrritation()));
        return insights;
    }

    /**
     * Detect if any parameter is worsening.
     * GOF Observer: triggers notification if worsening
     */
    private void checkWorseningTrend(String userID, List<ProgressEntry> entries) {
        if (entries.size() < 3) {
            return;
        }

        ProgressEntry a = entries.get(entries.size() - 3);
        ProgressEntry b = entries.get(entries.size() - 2);
        ProgressEntry c = entries.get(entries.size() - 1);

        boolean worsening =
            (a.getAcneLevel() < b.getAcneLevel() && b.getAcneLevel() < c.getAcneLevel())
                || (a.getDryness() < b.getDryness() && b.getDryness() < c.getDryness())
                || (a.getPigmentation() < b.getPigmentation() && b.getPigmentation() < c.getPigmentation())
                || (a.getIrritation() < b.getIrritation() && b.getIrritation() < c.getIrritation());

        if (worsening) {
            notifRepo.save(new Notification(
                UUID.randomUUID().toString(),
                userID,
                "Skin Trend Alert",
                "We detected a worsening trend in your recent skin entries. Consider revising your routine or booking expert consultation.",
                false,
                LocalDateTime.now()
            ));
        }
    }

    public List<ProgressEntry> getEntries(String userID) {
        return progressRepo.findByUser(userID);
    }

    private void validateRating(int value) {
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("Ratings must be between 1 and 5");
        }
    }

    private String buildInsight(String metric, int oldValue, int newValue) {
        if (oldValue <= 0) {
            return "Insufficient baseline data for " + metric;
        }
        double deltaPercent = ((oldValue - newValue) * 100.0) / oldValue;
        if (deltaPercent > 0) {
            return String.format("Your %s improved %.0f%% in 4 weeks", metric, Math.abs(deltaPercent));
        }
        if (deltaPercent < 0) {
            return String.format("Your %s worsened %.0f%% in 4 weeks", metric, Math.abs(deltaPercent));
        }
        return "Your " + metric + " stayed stable in 4 weeks";
    }
}
