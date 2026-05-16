package service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import model.AuditLog;
import model.EndUser;
import model.Notification;
import model.SkincareExpert;
import model.SystemAdmin;
import model.User;
import repository.AuditLogRepository;
import repository.NotificationRepository;
import repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Handles all admin operations.
 * GOF Singleton: only one instance allowed
 * GRASP High Cohesion: only admin operations
 */
public class AdminService {
    private static AdminService instance;
    private final UserRepository userRepo;
    private final AuditLogRepository auditRepo;
    private final NotificationRepository notifRepo;

    private AdminService() {
        this.userRepo = new UserRepository();
        this.auditRepo = new AuditLogRepository();
        this.notifRepo = new NotificationRepository();
    }

    /** GOF Singleton getInstance */
    public static AdminService getInstance() {
        if (instance == null) {
            instance = new AdminService();
        }
        return instance;
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public List<AuditLog> getAuditLogs() {
        return auditRepo.findAll();
    }

    public User getUserById(String userID) {
        return userRepo.findByID(userID);
    }

    public boolean deactivateUser(String adminID, String targetUserID, String reason) {
        boolean ok = userRepo.setActive(targetUserID, false);
        if (!ok) {
            return false;
        }

        notifRepo.save(new Notification(
            UUID.randomUUID().toString(),
            targetUserID,
            "Account Deactivated",
            "Your account has been deactivated. Reason: " + reason,
            false,
            LocalDateTime.now()
        ));

        auditRepo.save(new AuditLog(
            UUID.randomUUID().toString(),
            adminID,
            "DEACTIVATE_USER: " + reason,
            targetUserID,
            LocalDateTime.now()
        ));
        return true;
    }

    public boolean activateUser(String adminID, String targetUserID) {
        boolean ok = userRepo.setActive(targetUserID, true);
        if (ok) {
            notifRepo.save(new Notification(
                UUID.randomUUID().toString(),
                targetUserID,
                "Account Activated",
                "Your account is active again.",
                false,
                LocalDateTime.now()
            ));
            auditRepo.save(new AuditLog(
                UUID.randomUUID().toString(),
                adminID,
                "ACTIVATE_USER",
                targetUserID,
                LocalDateTime.now()
            ));
        }
        return ok;
    }

    public boolean assignRole(String adminID, String targetUserID, String newRole) {
        if ("expert".equalsIgnoreCase(newRole) && !userRepo.isVerified(targetUserID)) {
            throw new IllegalStateException("User must be verified before assigning expert role");
        }

        boolean ok = userRepo.updateRole(targetUserID, newRole);
        if (ok) {
            auditRepo.save(new AuditLog(
                UUID.randomUUID().toString(),
                adminID,
                "ASSIGN_ROLE:" + newRole,
                targetUserID,
                LocalDateTime.now()
            ));
        }
        return ok;
    }

    public boolean createAccount(String adminID,
                                 String name, String email, String password,
                                 String role) {
        String hashed = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        User user;

        if ("admin".equalsIgnoreCase(role)) {
            user = new SystemAdmin(UUID.randomUUID().toString(), name, email, hashed, 0, "unspecified");
        } else if ("expert".equalsIgnoreCase(role)) {
            SkincareExpert expert = new SkincareExpert(
                UUID.randomUUID().toString(),
                name,
                email,
                hashed,
                0,
                "unspecified",
                "General Dermatology",
                0.0
            );
            expert.setVerified(false);
            user = expert;
        } else {
            user = new EndUser(UUID.randomUUID().toString(), name, email, hashed, 0, "unspecified");
        }

        boolean ok = userRepo.save(user);
        if (ok) {
            auditRepo.save(new AuditLog(
                UUID.randomUUID().toString(),
                adminID,
                "CREATE_ACCOUNT:" + role,
                user.getUserID(),
                LocalDateTime.now()
            ));
        }
        return ok;
    }
}
