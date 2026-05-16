package service;
import java.util.Map;
import model.Notification;
import model.User;
import repository.NotificationRepository;
import repository.UserRepository;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Handles in-app notification delivery.
 * GOF Observer: subscribes to system events,
 * fires notifications
 * GRASP High Cohesion: only notification logic
 */
public class NotificationService {
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);
    private static final ConcurrentHashMap<String, ScheduledFuture<?>> MORNING_TASKS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ScheduledFuture<?>> EVENING_TASKS = new ConcurrentHashMap<>();

    private final NotificationRepository notifRepo;
    private final UserRepository userRepo;

    public NotificationService() {
        this.notifRepo = new NotificationRepository();
        this.userRepo = new UserRepository();
    }

    /**
     * Send notification to a user.
     * GOF Observer: called by other services when
     * events occur (appointment booked,
     * trend worsening, etc.)
     */
    public boolean sendNotification(String userID,
                                    String title, String body) {
        Notification notification = new Notification(
            UUID.randomUUID().toString(),
            userID,
            title,
            body,
            false,
            LocalDateTime.now()
        );
        return notifRepo.save(notification);
    }

    public List<Notification> getUserNotifications(String userID) {
        return notifRepo.findByUser(userID);
    }

    public boolean markAsRead(String notifID) {
        return notifRepo.markAsRead(notifID);
    }

    public int getUnreadCount(String userID) {
        return notifRepo.countUnread(userID);
    }

    /**
     * Save reminder schedule for user.
     * JavaFX Timeline or ScheduledExecutorService
     * used for local scheduling.
     */
    public boolean saveReminderSettings(String userID,
                                        String morningTime, String eveningTime,
                                        boolean enabled) {
        User user = userRepo.findByID(userID);
        if (user == null) {
            return false;
        }

        cancelReminder(MORNING_TASKS, userID);
        cancelReminder(EVENING_TASKS, userID);

        if (enabled) {
            scheduleReminder(MORNING_TASKS, userID, morningTime, "Morning Skincare Reminder", "Time for your morning routine.");
            scheduleReminder(EVENING_TASKS, userID, eveningTime, "Evening Skincare Reminder", "Time for your evening routine.");
        }

        // Persist settings in DB-backed notification history to avoid schema change.
        notifRepo.save(new Notification(
            UUID.randomUUID().toString(),
            userID,
            "REMINDER_SETTINGS",
            "enabled=" + enabled + ";morning=" + morningTime + ";evening=" + eveningTime,
            true,
            LocalDateTime.now()
        ));

        return sendNotification(
            userID,
            "Reminder Settings Updated",
            "Your reminder schedule has been " + (enabled ? "enabled" : "disabled") + "."
        );
    }

    public Map<String, String> getReminderSettings(String userID) {
        ConcurrentHashMap<String, String> result = new ConcurrentHashMap<>();
        for (Notification n : notifRepo.findByUser(userID)) {
            if ("REMINDER_SETTINGS".equals(n.getTitle()) && n.getBody() != null) {
                String[] chunks = n.getBody().split(";");
                for (String c : chunks) {
                    String[] kv = c.split("=");
                    if (kv.length == 2) {
                        result.put(kv[0], kv[1]);
                    }
                }
                return result;
            }
        }
        return result;
    }

    private void scheduleReminder(ConcurrentHashMap<String, ScheduledFuture<?>> taskMap,
                                  String userID,
                                  String timeRaw,
                                  String title,
                                  String body) {
        LocalTime target = LocalTime.parse(timeRaw);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = now.withHour(target.getHour()).withMinute(target.getMinute()).withSecond(0).withNano(0);
        if (!next.isAfter(now)) {
            next = next.plusDays(1);
        }

        long initialDelay = Duration.between(now, next).toSeconds();
        long period = TimeUnit.DAYS.toSeconds(1);

        ScheduledFuture<?> future = SCHEDULER.scheduleAtFixedRate(() ->
            sendNotification(userID, title, body),
            initialDelay,
            period,
            TimeUnit.SECONDS
        );
        taskMap.put(userID, future);
    }

    private void cancelReminder(ConcurrentHashMap<String, ScheduledFuture<?>> taskMap, String userID) {
        ScheduledFuture<?> future = taskMap.remove(userID);
        if (future != null) {
            future.cancel(false);
        }
    }
}
