package service;

import model.Appointment;
import model.AuditLog;
import model.Notification;
import model.SkincareExpert;
import repository.AppointmentRepository;
import repository.AuditLogRepository;
import repository.NotificationRepository;
import repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles appointment booking business logic.
 * GRASP High Cohesion: only handles appointment logic
 */
public class AppointmentService {
    private final AppointmentRepository appointmentRepo;
    private final NotificationRepository notifRepo;
    private final AuditLogRepository auditRepo;
    private final UserRepository userRepo;

    public AppointmentService() {
        this.appointmentRepo = new AppointmentRepository();
        this.notifRepo = new NotificationRepository();
        this.auditRepo = new AuditLogRepository();
        this.userRepo = new UserRepository();
    }

    /**
     * Book an appointment with an expert.
     * Checks availability before booking.
     */
    public Appointment bookAppointment(String userID, String expertID,
                                       LocalDate date, String timeSlot,
                                       String type, double fee) {
        if (!appointmentRepo.isSlotAvailable(expertID, date, timeSlot)) {
            throw new IllegalStateException("Slot not available");
        }

        Appointment appointment = new Appointment(
            UUID.randomUUID().toString(),
            userID,
            expertID,
            date,
            timeSlot,
            type,
            fee,
            "pending",
            LocalDateTime.now()
        );

        boolean saved = appointmentRepo.save(appointment);
        boolean booked = appointmentRepo.bookSlot(expertID, date, timeSlot);
        if (!saved || !booked) {
            throw new IllegalStateException("Failed to book appointment");
        }

        notifRepo.save(new Notification(
            UUID.randomUUID().toString(),
            userID,
            "Appointment Requested",
            "Your appointment request has been placed for " + date + " at " + timeSlot + ".",
            false,
            LocalDateTime.now()
        ));

        notifRepo.save(new Notification(
            UUID.randomUUID().toString(),
            expertID,
            "New Appointment Request",
            "A user booked " + date + " at " + timeSlot + ".",
            false,
            LocalDateTime.now()
        ));

        auditRepo.save(new AuditLog(
            UUID.randomUUID().toString(),
            userID,
            "BOOK_APPOINTMENT",
            appointment.getAppointmentID(),
            LocalDateTime.now()
        ));

        return appointment;
    }

    public boolean cancelAppointment(String appointmentID, String userID) {
        Appointment appointment = appointmentRepo.findByID(appointmentID);
        if (appointment == null || !appointment.getUserID().equals(userID)) {
            return false;
        }
        boolean ok = appointmentRepo.updateStatus(appointmentID, "cancelled");
        if (ok) {
            auditRepo.save(new AuditLog(
                UUID.randomUUID().toString(),
                userID,
                "CANCEL_APPOINTMENT",
                appointmentID,
                LocalDateTime.now()
            ));
        }
        return ok;
    }

    public List<Appointment> getUserAppointments(String userID) {
        return appointmentRepo.findByUser(userID);
    }

    public List<Appointment> getExpertAppointments(String expertID) {
        return appointmentRepo.findByExpert(expertID);
    }

    public List<String> getAvailableSlots(String expertID, LocalDate date) {
        return appointmentRepo.getAvailableSlots(expertID, date);
    }

    public List<SkincareExpert> getVerifiedExperts() {
        return userRepo.findAllExperts();
    }

    public List<Appointment> getUpcomingAppointments(String userID) {
        List<Appointment> upcoming = new ArrayList<>();
        for (Appointment appt : appointmentRepo.findByUser(userID)) {
            if (!appt.getAppointmentDate().isBefore(LocalDate.now())
                && !"cancelled".equalsIgnoreCase(appt.getStatus())) {
                upcoming.add(appt);
            }
        }
        return upcoming;
    }
}
