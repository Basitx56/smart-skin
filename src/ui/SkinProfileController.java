package ui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import model.SkinProfile;
import model.User;
import service.SkinProfileService;
import util.AlertHelper;
import util.NavigationManager;
import util.SessionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkinProfileController extends BaseController {
    @FXML private ToggleGroup skinTypeGroup;
    @FXML private RadioButton oilyBtn;
    @FXML private RadioButton dryBtn;
    @FXML private RadioButton combinationBtn;
    @FXML private RadioButton sensitiveBtn;
    @FXML private RadioButton normalBtn;
    @FXML private CheckBox concernAcne;
    @FXML private CheckBox concernDryness;
    @FXML private CheckBox concernPigmentation;
    @FXML private CheckBox concernAging;
    @FXML private CheckBox concernOiliness;
    @FXML private CheckBox concernSensitivity;
    @FXML private TextField allergyField;
    @FXML private ListView<String> allergiesList;
    @FXML private TextArea currentProductsArea;
    @FXML private ComboBox<String> dietBox;
    @FXML private Slider sleepSlider;
    @FXML private Label sleepValueLabel;
    @FXML private ComboBox<String> stressBox;
    @FXML private ComboBox<String> sunBox;
    @FXML private ProgressBar completenessBar;
    @FXML private Label completenessLabel;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label statusLabel;

    private final SkinProfileService skinProfileService = new SkinProfileService();

    @FXML
    public void initialize() {
        dietBox.getItems().addAll("Poor", "Average", "Good", "Excellent");
        stressBox.getItems().addAll("Low", "Medium", "High");
        sunBox.getItems().addAll("Low", "Moderate", "High");
        loadingIndicator.setVisible(false);

        sleepSlider.valueProperty().addListener((o, ov, nv) -> {
            sleepValueLabel.setText(String.valueOf(nv.intValue()));
            recalcCompleteness();
        });

        skinTypeGroup.selectedToggleProperty().addListener((o, ov, nv) -> recalcCompleteness());
        concernAcne.selectedProperty().addListener((o, ov, nv) -> recalcCompleteness());
        concernDryness.selectedProperty().addListener((o, ov, nv) -> recalcCompleteness());
        concernPigmentation.selectedProperty().addListener((o, ov, nv) -> recalcCompleteness());
        concernAging.selectedProperty().addListener((o, ov, nv) -> recalcCompleteness());
        concernOiliness.selectedProperty().addListener((o, ov, nv) -> recalcCompleteness());
        concernSensitivity.selectedProperty().addListener((o, ov, nv) -> recalcCompleteness());
        currentProductsArea.textProperty().addListener((o, ov, nv) -> recalcCompleteness());
        dietBox.valueProperty().addListener((o, ov, nv) -> recalcCompleteness());

        loadExistingProfile();
    }

    private void loadExistingProfile() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            NavigationManager.navigateTo("/fxml/LoginPage.fxml");
            return;
        }

        SkinProfile profile = skinProfileService.getProfile(user.getUserID());
        if (profile == null) {
            recalcCompleteness();
            return;
        }

        setSkinType(profile.getSkinType());
        setConcerns(profile.getSkinConcerns());
        allergiesList.getItems().setAll(profile.getKnownAllergies());
        currentProductsArea.setText(String.join(", ", profile.getCurrentProducts()));
        dietBox.setValue(profile.getDiet());
        sleepSlider.setValue(profile.getSleepHours());
        stressBox.setValue(profile.getStressLevel());
        sunBox.setValue(profile.getSunExposure());
        SessionManager.setCurrentProfile(profile);
        recalcCompleteness();
    }

    @FXML
    private void onAddAllergy() {
        String item = allergyField.getText() == null ? "" : allergyField.getText().trim();
        if (!item.isEmpty()) {
            allergiesList.getItems().add(item);
            allergyField.clear();
            recalcCompleteness();
        }
    }

    @FXML
    private void onSaveProfile() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            return;
        }

        loadingIndicator.setVisible(true);
        statusLabel.setText("Saving...");

        Task<SkinProfile> task = new Task<SkinProfile>() {
            @Override
            protected SkinProfile call() {
                return skinProfileService.saveProfile(
                    user.getUserID(),
                    getSkinType(),
                    getConcerns(),
                    new ArrayList<>(allergiesList.getItems()),
                    splitCsv(currentProductsArea.getText()),
                    dietBox.getValue(),
                    (int) sleepSlider.getValue(),
                    stressBox.getValue(),
                    sunBox.getValue()
                );
            }
        };

        task.setOnSucceeded(e -> {
            loadingIndicator.setVisible(false);
            statusLabel.setStyle("-fx-text-fill:#4CAF50; -fx-font-size:14;");
            statusLabel.setText("Profile saved successfully");
            SessionManager.setCurrentProfile(task.getValue());
            AlertHelper.showSuccess("Profile saved");
            recalcCompleteness();
        });

        task.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            statusLabel.setStyle("-fx-text-fill:#F44336; -fx-font-size:14;");
            statusLabel.setText(task.getException() == null ? "Save failed" : task.getException().getMessage());
        });

        new Thread(task).start();
    }

    @FXML
    private void onBack() {
        NavigationManager.navigateTo("/fxml/DashboardPage.fxml");
    }

    private void recalcCompleteness() {
        int score = 0;
        if (getSkinType() != null && !getSkinType().isEmpty()) score += 20;
        if (!getConcerns().isEmpty()) score += 20;
        if (!allergiesList.getItems().isEmpty()) score += 20;
        if (!splitCsv(currentProductsArea.getText()).isEmpty()) score += 20;
        if (dietBox.getValue() != null && !dietBox.getValue().isEmpty()) score += 20;

        completenessBar.setProgress(score / 100.0);
        completenessLabel.setText("Profile " + score + "% complete");
    }

    private String getSkinType() {
        if (oilyBtn.isSelected()) return "oily";
        if (dryBtn.isSelected()) return "dry";
        if (combinationBtn.isSelected()) return "combination";
        if (sensitiveBtn.isSelected()) return "sensitive";
        if (normalBtn.isSelected()) return "normal";
        return null;
    }

    private List<String> getConcerns() {
        List<String> concerns = new ArrayList<>();
        if (concernAcne.isSelected()) concerns.add("Acne");
        if (concernDryness.isSelected()) concerns.add("Dryness");
        if (concernPigmentation.isSelected()) concerns.add("Pigmentation");
        if (concernAging.isSelected()) concerns.add("Aging");
        if (concernOiliness.isSelected()) concerns.add("Oiliness");
        if (concernSensitivity.isSelected()) concerns.add("Sensitivity");
        return concerns;
    }

    private void setSkinType(String type) {
        if (type == null) return;
        switch (type.toLowerCase()) {
            case "oily": oilyBtn.setSelected(true); break;
            case "dry": dryBtn.setSelected(true); break;
            case "combination": combinationBtn.setSelected(true); break;
            case "sensitive": sensitiveBtn.setSelected(true); break;
            default: normalBtn.setSelected(true);
        }
    }

    private void setConcerns(List<String> concerns) {
        if (concerns == null) return;
        String all = String.join(",", concerns).toLowerCase();
        concernAcne.setSelected(all.contains("acne"));
        concernDryness.setSelected(all.contains("dryness"));
        concernPigmentation.setSelected(all.contains("pigmentation"));
        concernAging.setSelected(all.contains("aging"));
        concernOiliness.setSelected(all.contains("oiliness"));
        concernSensitivity.setSelected(all.contains("sensitivity"));
    }

    private List<String> splitCsv(String csv) {
        if (csv == null || csv.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(csv.split("\\s*,\\s*")));
    }
}
