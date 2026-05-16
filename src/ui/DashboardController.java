package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import model.Appointment;
import model.Notification;
import model.SkinProfile;
import model.User;
import service.AppointmentService;
import service.FavoriteService;
import service.NotificationService;
import service.ProgressTrackingService;
import service.SkinProfileService;
import util.NavigationManager;
import util.SessionManager;

import java.util.List;

public class DashboardController extends BaseController {
    @FXML private Label welcomeLabel;
    @FXML private Label unreadBadgeLabel;
    @FXML private Label completenessLabel;
    @FXML private Label completenessStatLabel;
    @FXML private Label appointmentStatLabel;
    @FXML private Label progressStatLabel;
    @FXML private Label favoriteStatLabel;
    @FXML private ProgressBar completenessBar;
    @FXML private VBox notificationsBox;
    @FXML private VBox appointmentsBox;

    private final NotificationService notificationService = new NotificationService();
    private final SkinProfileService skinProfileService = new SkinProfileService();
    private final AppointmentService appointmentService = new AppointmentService();
    private final ProgressTrackingService progressTrackingService = new ProgressTrackingService();
    private final FavoriteService favoriteService = new FavoriteService();

    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            NavigationManager.navigateTo("/fxml/LoginPage.fxml");
            return;
        }

        welcomeLabel.setText("Welcome, " + user.getName() + "!");
        unreadBadgeLabel.setText(String.valueOf(notificationService.getUnreadCount(user.getUserID())));

        SkinProfile profile = skinProfileService.getProfile(user.getUserID());
        int completeness = 0;
        if (profile != null) {
            SessionManager.setCurrentProfile(profile);
            completeness = profile.getCompletenessPercentage();
            completenessLabel.setText("Profile " + completeness + "% complete");
        } else {
            completenessLabel.setText("Profile 0% complete");
        }

        if (completenessStatLabel != null) {
            completenessStatLabel.setText(completeness + "%");
        }
        if (completenessBar != null) {
            completenessBar.setProgress(Math.max(0, Math.min(1, completeness / 100.0)));
        }

        List<Notification> notifs = notificationService.getUserNotifications(user.getUserID());
        notificationsBox.getChildren().clear();
        notifs.stream().limit(5).forEach(n -> notificationsBox.getChildren().add(new Label("• " + n.getTitle())));

        List<Appointment> upcoming = appointmentService.getUpcomingAppointments(user.getUserID());
        appointmentsBox.getChildren().clear();
        upcoming.stream().limit(5).forEach(a -> appointmentsBox.getChildren().add(new Label(a.getAppointmentDate() + " " + a.getTimeSlot() + " - " + a.getStatus())));

        if (appointmentStatLabel != null) {
            appointmentStatLabel.setText(String.valueOf(upcoming.size()));
        }
        if (progressStatLabel != null) {
            progressStatLabel.setText(String.valueOf(progressTrackingService.getEntries(user.getUserID()).size()));
        }
        if (favoriteStatLabel != null) {
            favoriteStatLabel.setText(String.valueOf(favoriteService.getFavorites(user.getUserID()).size()));
        }
    }

}
