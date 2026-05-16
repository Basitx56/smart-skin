package service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import model.AuditLog;
import model.User;
import repository.AppointmentRepository;
import repository.AuditLogRepository;
import repository.ProgressRepository;
import repository.RoutineRepository;
import repository.SkinProfileRepository;
import repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class AccountService {
    private final UserRepository userRepo;
    private final SkinProfileRepository skinProfileRepo;
    private final ProgressRepository progressRepo;
    private final RoutineRepository routineRepo;
    private final AppointmentRepository appointmentRepo;
    private final AuditLogRepository auditRepo;

    public AccountService() {
        this.userRepo = new UserRepository();
        this.skinProfileRepo = new SkinProfileRepository();
        this.progressRepo = new ProgressRepository();
        this.routineRepo = new RoutineRepository();
        this.appointmentRepo = new AppointmentRepository();
        this.auditRepo = new AuditLogRepository();
    }

    /**
     * Permanently delete user account and all data.
     * Deletes in order: photos, progress, routines,
     * appointments, skin profile, then user.
     */
    public boolean deleteAccount(String userID,
                                 String passwordConfirmation) {
        User user = userRepo.findByID(userID);
        if (user == null) {
            return false;
        }

        BCrypt.Result check = BCrypt.verifyer().verify(passwordConfirmation.toCharArray(), user.getPassword());
        if (!check.verified) {
            return false;
        }

        // Photo assets are stored by path in progress entries; file deletion should be handled by file storage layer.
        progressRepo.deleteByUser(userID);
        routineRepo.deleteByUser(userID);
        appointmentRepo.deleteByUser(userID);
        skinProfileRepo.delete(userID);
        boolean deleted = userRepo.delete(userID);

        if (deleted) {
            auditRepo.save(new AuditLog(
                UUID.randomUUID().toString(),
                userID,
                "DELETE_ACCOUNT",
                userID,
                LocalDateTime.now()
            ));
        }
        return deleted;
    }
}
