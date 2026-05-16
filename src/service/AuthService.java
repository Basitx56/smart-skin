package service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import model.AuditLog;
import model.EndUser;
import model.User;
import repository.AuditLogRepository;
import repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Handles all authentication business logic.
 * GRASP Creator: creates User objects
 * GRASP High Cohesion: only handles auth logic
 * Architecture: Business Logic Layer
 */
public class AuthService {
    private final UserRepository userRepo;
    private final AuditLogRepository auditRepo;
    private final Map<String, Integer> failedAttempts;
    private final Map<String, LocalDateTime> lockUntil;

    public AuthService() {
        // GRASP Low Coupling: inject repositories
        this.userRepo = new UserRepository();
        this.auditRepo = new AuditLogRepository();
        this.failedAttempts = new HashMap<>();
        this.lockUntil = new HashMap<>();
    }

    /**
     * Registers a new end user.
     * Validates password strength, hashes password,
     * saves to DB, writes audit log.
     */
    public EndUser registerUser(String name, String email,
                                String password, int age, String gender) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (userRepo.emailExists(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (!isPasswordStrong(password)) {
            throw new IllegalArgumentException("Password must be at least 8 chars and include uppercase, digit, and special character");
        }

        String hashed = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        EndUser user = new EndUser(UUID.randomUUID().toString(), name, email, hashed, age, gender);
        boolean saved = userRepo.save(user);
        if (!saved) {
            throw new IllegalStateException("Failed to create user account");
        }

        auditRepo.save(new AuditLog(
            UUID.randomUUID().toString(),
            user.getUserID(),
            "REGISTER_USER",
            user.getUserID(),
            LocalDateTime.now()
        ));
        return user;
    }

    /**
     * Authenticates user login.
     * @return User object (correct subclass) or null
     */
    public User loginUser(String email, String password) {
        if (isLocked(email)) {
            throw new IllegalStateException("Account locked 15 minutes");
        }

        User user = userRepo.findByEmail(email);
        if (user == null) {
            registerFailedAttempt(email);
            return null;
        }

        if (!user.isActive()) {
            throw new IllegalStateException("Account deactivated");
        }

        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
        if (!result.verified) {
            registerFailedAttempt(email);
            return null;
        }

        failedAttempts.remove(email.toLowerCase());
        lockUntil.remove(email.toLowerCase());

        auditRepo.save(new AuditLog(
            UUID.randomUUID().toString(),
            user.getUserID(),
            "LOGIN_SUCCESS",
            user.getUserID(),
            LocalDateTime.now()
        ));
        return user;
    }

    /** Validate password meets requirements */
    private boolean isPasswordStrong(String password) {
        if (password == null) {
            return false;
        }
        String regex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$";
        return Pattern.compile(regex).matcher(password).matches();
    }

    /** Validate email format */
    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.compile(regex).matcher(email).matches();
    }

    private boolean isLocked(String email) {
        LocalDateTime until = lockUntil.get(email.toLowerCase());
        if (until == null) {
            return false;
        }
        if (LocalDateTime.now().isAfter(until)) {
            lockUntil.remove(email.toLowerCase());
            failedAttempts.remove(email.toLowerCase());
            return false;
        }
        return true;
    }

    private void registerFailedAttempt(String email) {
        String key = email.toLowerCase();
        int count = failedAttempts.getOrDefault(key, 0) + 1;
        failedAttempts.put(key, count);
        if (count >= 5) {
            lockUntil.put(key, LocalDateTime.now().plusMinutes(15));
            throw new IllegalStateException("Account locked 15 minutes");
        }
    }
}
