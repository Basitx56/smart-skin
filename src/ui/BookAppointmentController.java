package ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.util.Duration;
import model.Appointment;
import model.SkincareExpert;
import model.User;
import service.AppointmentService;
import util.AlertHelper;
import util.NavigationManager;
import util.SessionManager;

import java.time.LocalDate;
import java.util.List;

public class BookAppointmentController extends BaseController {
    @FXML private ListView<String> expertsList;
    @FXML private Label expertDetailsLabel;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> slotBox;
    @FXML private ComboBox<String> typeBox;
    @FXML private Label feeLabel;
    @FXML private Label holdTimerLabel;
    @FXML private ProgressIndicator loadingIndicator;

    private final AppointmentService appointmentService = new AppointmentService();
    private List<SkincareExpert> experts;
    private SkincareExpert selectedExpert;
    private Timeline timer;
    private int remainingSeconds;

    @FXML
    public void initialize() {
        loadingIndicator.setVisible(false);
        holdTimerLabel.setVisible(false);
        datePicker.setValue(LocalDate.now());
        typeBox.getItems().addAll("video", "in_person", "chat");

        experts = appointmentService.getVerifiedExperts();
        for (SkincareExpert expert : experts) {
            expertsList.getItems().add(expert.getName() + " | " + expert.getSpecialization() + " | " + expert.getConsultationFee());
        }

        expertsList.getSelectionModel().selectedIndexProperty().addListener((o, ov, nv) -> {
            int idx = nv.intValue();
            if (idx >= 0 && idx < experts.size()) {
                selectedExpert = experts.get(idx);
                expertDetailsLabel.setText(selectedExpert.getName() + "\n" + selectedExpert.getSpecialization() + "\nRating: " + selectedExpert.getRating());
                feeLabel.setText("Fee: $" + selectedExpert.getConsultationFee());
                loadSlots();
            }
        });

        datePicker.valueProperty().addListener((o, ov, nv) -> loadSlots());
        slotBox.valueProperty().addListener((o, ov, nv) -> startHoldTimer());
    }

    private void loadSlots() {
        if (selectedExpert == null || datePicker.getValue() == null) {
            return;
        }
        slotBox.getItems().setAll(appointmentService.getAvailableSlots(selectedExpert.getUserID(), datePicker.getValue()));
    }

    private void startHoldTimer() {
        if (slotBox.getValue() == null) return;
        if (timer != null) timer.stop();
        remainingSeconds = 5 * 60;
        holdTimerLabel.setVisible(true);
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            remainingSeconds--;
            holdTimerLabel.setText("Hold time: " + (remainingSeconds / 60) + ":" + String.format("%02d", remainingSeconds % 60));
            if (remainingSeconds <= 0) {
                timer.stop();
                holdTimerLabel.setText("Slot hold expired");
                holdTimerLabel.setStyle("-fx-text-fill:#F44336;");
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    @FXML
    private void onConfirmBooking() {
        User user = SessionManager.getCurrentUser();
        if (user == null || selectedExpert == null || slotBox.getValue() == null || typeBox.getValue() == null) {
            AlertHelper.showError("Please complete all booking fields");
            return;
        }

        loadingIndicator.setVisible(true);
        Task<Appointment> task = new Task<Appointment>() {
            @Override
            protected Appointment call() {
                return appointmentService.bookAppointment(
                    user.getUserID(),
                    selectedExpert.getUserID(),
                    datePicker.getValue(),
                    slotBox.getValue(),
                    typeBox.getValue(),
                    selectedExpert.getConsultationFee()
                );
            }
        };

        task.setOnSucceeded(e -> {
            loadingIndicator.setVisible(false);
            AlertHelper.showSuccess("Appointment booked successfully");
            NavigationManager.navigateTo("/fxml/DashboardPage.fxml");
        });

        task.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            AlertHelper.showError(task.getException() == null ? "Booking failed" : task.getException().getMessage());
        });

        new Thread(task).start();
    }
}
