package service;

import model.AuditLog;
import model.Notification;
import model.User;
import model.ValidationQueueItem;
import repository.AuditLogRepository;
import repository.NotificationRepository;
import repository.RoutineRepository;
import repository.UserRepository;
import repository.ValidationQueueRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ValidationService {
    private final ValidationQueueRepository validationRepo;
    private final RoutineRepository routineRepo;
    private final AuditLogRepository auditRepo;
    private final NotificationRepository notifRepo;
    private final UserRepository userRepo;

    public ValidationService() {
        this.validationRepo = new ValidationQueueRepository();
        this.routineRepo = new RoutineRepository();
        this.auditRepo = new AuditLogRepository();
        this.notifRepo = new NotificationRepository();
        this.userRepo = new UserRepository();
    }

    public List<ValidationQueueItem> getPendingItems() {
        return validationRepo.findPending();
    }

    /**
     * Expert reviews a routine recommendation.
     * @param action approve/modify/reject/flag_safety
     */
    public boolean processReview(String itemID,
                                 String expertID, String action,
                                 String rejectionReason) {
        ValidationQueueItem item = validationRepo.findByID(itemID);
        if (item == null) {
            return false;
        }

        String mappedStatus = mapActionToStatus(action);
        boolean queueUpdated = validationRepo.updateStatus(
            itemID,
            mappedStatus,
            expertID,
            rejectionReason
        );
        if (!queueUpdated) {
            return false;
        }

        if ("approve".equalsIgnoreCase(action)) {
            routineRepo.updateValidationStatus(item.getRoutineID(), true, expertID);
        }

        if ("reject".equalsIgnoreCase(action) || "flag_safety".equalsIgnoreCase(action)) {
            for (String adminId : findAdminIDs()) {
                notifRepo.save(new Notification(
                    UUID.randomUUID().toString(),
                    adminId,
                    "Routine Review Escalation",
                    "Validation item " + itemID + " flagged with action: " + action,
                    false,
                    LocalDateTime.now()
                ));
            }
        }

        auditRepo.save(new AuditLog(
            UUID.randomUUID().toString(),
            expertID,
            "VALIDATION_REVIEW:" + action,
            itemID,
            LocalDateTime.now()
        ));
        return true;
    }

    private String mapActionToStatus(String action) {
        if ("approve".equalsIgnoreCase(action)) {
            return "approved";
        }
        if ("modify".equalsIgnoreCase(action)) {
            return "modified";
        }
        if ("reject".equalsIgnoreCase(action) || "flag_safety".equalsIgnoreCase(action)) {
            return "rejected";
        }
        return "pending";
    }

    private List<String> findAdminIDs() {
        List<String> ids = new ArrayList<>();
        for (User user : userRepo.findAll()) {
            if ("admin".equalsIgnoreCase(user.getRole())) {
                ids.add(user.getUserID());
            }
        }
        return ids;
    }
}
