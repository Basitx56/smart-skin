package ui;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.ProgressEntry;
import model.User;
import service.ProgressTrackingService;
import util.AlertHelper;
import util.NavigationManager;
import util.SessionManager;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TrackProgressController extends BaseController {
    @FXML private DatePicker datePicker;
    @FXML private Slider acneSlider;
    @FXML private Slider drynessSlider;
    @FXML private Slider pigmentationSlider;
    @FXML private Slider irritationSlider;
    @FXML private Label acneValue;
    @FXML private Label drynessValue;
    @FXML private Label pigmentationValue;
    @FXML private Label irritationValue;
    @FXML private TextArea notesArea;
    @FXML private LineChart<String, Number> progressChart;
    @FXML private Label insightLabel;
    @FXML private VBox warningBanner;
    @FXML private ProgressIndicator loadingIndicator;

    private final ProgressTrackingService progressService = new ProgressTrackingService();
    private List<ProgressEntry> entries;

    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now());
        loadingIndicator.setVisible(false);
        hookSlider(acneSlider, acneValue);
        hookSlider(drynessSlider, drynessValue);
        hookSlider(pigmentationSlider, pigmentationValue);
        hookSlider(irritationSlider, irritationValue);
        loadEntries();
    }

    private void hookSlider(Slider slider, Label label) {
        label.setText(String.valueOf((int) slider.getValue()));
        slider.valueProperty().addListener((o, ov, nv) -> label.setText(String.valueOf(nv.intValue())));
    }

    private void loadEntries() {
        User user = SessionManager.getCurrentUser();
        if (user == null) return;
        entries = progressService.getEntries(user.getUserID());
        populateChart(entries);
        Map<String, String> insights = progressService.getInsights(user.getUserID());
        insightLabel.setText(String.join(" | ", insights.values()));
        warningBanner.setVisible(insightLabel.getText().toLowerCase().contains("worsened"));
    }

    @FXML
    private void onSaveEntry() {
        User user = SessionManager.getCurrentUser();
        if (user == null) return;

        loadingIndicator.setVisible(true);
        Task<ProgressEntry> task = new Task<ProgressEntry>() {
            @Override
            protected ProgressEntry call() {
                return progressService.addEntry(
                    user.getUserID(),
                    datePicker.getValue(),
                    (int) acneSlider.getValue(),
                    (int) drynessSlider.getValue(),
                    (int) pigmentationSlider.getValue(),
                    (int) irritationSlider.getValue(),
                    null,
                    notesArea.getText()
                );
            }
        };

        task.setOnSucceeded(e -> {
            loadingIndicator.setVisible(false);
            AlertHelper.showSuccess("Entry saved");
            loadEntries();
        });
        task.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            AlertHelper.showError(task.getException() == null ? "Save failed" : task.getException().getMessage());
        });
        new Thread(task).start();
    }

    @FXML
    private void onUploadPhoto() {
        NavigationManager.navigateTo("/fxml/UploadPhotoPage.fxml");
    }

    @FXML
    private void onExportCsv() {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setInitialFileName("progress.csv");
            File f = chooser.showSaveDialog(progressChart.getScene().getWindow());
            if (f == null) return;
            try (FileWriter fw = new FileWriter(f)) {
                fw.write("Date,Acne,Dryness,Pigmentation,Irritation,Notes\n");
                for (ProgressEntry e : entries) {
                    fw.write(e.getEntryDate() + "," + e.getAcneLevel() + "," + e.getDryness() + "," + e.getPigmentation() + "," + e.getIrritation() + ",\"" + (e.getNotes() == null ? "" : e.getNotes().replace("\"", "''")) + "\"\n");
                }
            }
            AlertHelper.showSuccess("CSV exported");
        } catch (Exception ex) {
            AlertHelper.showError("CSV export failed: " + ex.getMessage());
        }
    }

    private void populateChart(List<ProgressEntry> data) {
        progressChart.getData().clear();
        XYChart.Series<String, Number> acne = new XYChart.Series<>(FXCollections.observableArrayList());
        acne.setName("Acne");
        XYChart.Series<String, Number> dry = new XYChart.Series<>(FXCollections.observableArrayList());
        dry.setName("Dryness");
        XYChart.Series<String, Number> pig = new XYChart.Series<>(FXCollections.observableArrayList());
        pig.setName("Pigmentation");
        XYChart.Series<String, Number> irr = new XYChart.Series<>(FXCollections.observableArrayList());
        irr.setName("Irritation");

        for (ProgressEntry e : data) {
            String d = e.getEntryDate().toString();
            acne.getData().add(new XYChart.Data<>(d, e.getAcneLevel()));
            dry.getData().add(new XYChart.Data<>(d, e.getDryness()));
            pig.getData().add(new XYChart.Data<>(d, e.getPigmentation()));
            irr.getData().add(new XYChart.Data<>(d, e.getIrritation()));
        }

        progressChart.getData().add(acne);
        progressChart.getData().add(dry);
        progressChart.getData().add(pig);
        progressChart.getData().add(irr);
    }
}
