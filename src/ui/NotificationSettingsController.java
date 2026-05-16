package ui;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import model.Notification;
import model.User;
import service.NotificationService;
import util.AlertHelper;
import util.SessionManager;

import java.util.Map;
import java.util.stream.IntStream;

public class NotificationSettingsController extends BaseController {
    @FXML private CheckBox enableCheck;
    @FXML private ComboBox<String> morningHourBox;
    @FXML private ComboBox<String> morningMinuteBox;
    @FXML private ComboBox<String> eveningHourBox;
    @FXML private ComboBox<String> eveningMinuteBox;
    @FXML private Label previewLabel;
    @FXML private ListView<Notification> historyList;
    @FXML private ProgressIndicator loadingIndicator;

    private final NotificationService notificationService = new NotificationService();

    @FXML
    public void initialize() {
        loadingIndicator.setVisible(false);
        IntStream.range(0, 24).forEach(i -> {
            String h = String.format("%02d", i);
            morningHourBox.getItems().add(h);
            eveningHourBox.getItems().add(h);
        });
        IntStream.range(0, 60).forEach(i -> {
            String m = String.format("%02d", i);
            morningMinuteBox.getItems().add(m);
            eveningMinuteBox.getItems().add(m);
        });

        loadData();

        historyList.setCellFactory(lv -> new ListCell<Notification>() {
            @Override
            protected void updateItem(Notification item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.getTitle() + " - " + item.getBody() + " (" + item.getCreatedAt() + ")");
                    setStyle(item.isRead() ? "-fx-font-weight:normal;" : "-fx-font-weight:bold;");
                }
            }
        });
    }

    private void loadData() {
        User user = SessionManager.getCurrentUser();
        if (user == null) return;

        Map<String, String> s = notificationService.getReminderSettings(user.getUserID());
        boolean enabled = Boolean.parseBoolean(s.getOrDefault("enabled", "false"));
        enableCheck.setSelected(enabled);

        String morning = s.getOrDefault("morning", "07:00");
        String evening = s.getOrDefault("evening", "21:00");

        morningHourBox.setValue(morning.split(":")[0]);
        morningMinuteBox.setValue(morning.split(":")[1]);
        eveningHourBox.setValue(evening.split(":")[0]);
        eveningMinuteBox.setValue(evening.split(":")[1]);

        previewLabel.setText("Morning reminder set for " + morning + "");
        historyList.getItems().setAll(notificationService.getUserNotifications(user.getUserID()));
    }

    @FXML
    private void onSaveSettings() {
        User user = SessionManager.getCurrentUser();
        if (user == null) return;

        loadingIndicator.setVisible(true);
        String morning = morningHourBox.getValue() + ":" + morningMinuteBox.getValue();
        String evening = eveningHourBox.getValue() + ":" + eveningMinuteBox.getValue();

        boolean ok = notificationService.saveReminderSettings(user.getUserID(), morning, evening, enableCheck.isSelected());
        loadingIndicator.setVisible(false);
        if (ok) {
            previewLabel.setText("Morning reminder set for " + morning);
            previewLabel.setStyle("-fx-text-fill:#4CAF50;");
            AlertHelper.showSuccess("Settings saved");
            loadData();
        } else {
            previewLabel.setText("Failed to save settings");
            previewLabel.setStyle("-fx-text-fill:#F44336;");
        }
    }
}
