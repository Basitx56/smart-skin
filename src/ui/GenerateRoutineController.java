package ui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import model.RoutineStep;
import model.SkincareRoutine;
import model.SkinProfile;
import model.User;
import service.RoutineGeneratorService;
import util.AlertHelper;
import util.NavigationManager;
import util.SessionManager;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public class GenerateRoutineController extends BaseController {
    @FXML private Label incompleteLabel;
    @FXML private ProgressBar profileProgress;
    @FXML private Hyperlink profileLink;
    @FXML private VBox morningCard;
    @FXML private VBox eveningCard;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label statusLabel;

    private final RoutineGeneratorService routineService = new RoutineGeneratorService();
    private SkincareRoutine morning;
    private SkincareRoutine evening;

    @FXML
    public void initialize() {
        loadingIndicator.setVisible(false);
        checkProfileStatus();
    }

    private void checkProfileStatus() {
        SkinProfile profile = SessionManager.getCurrentProfile();
        int completeness = profile == null ? 0 : profile.calculateCompleteness();
        profileProgress.setProgress(completeness / 100.0);
        boolean incomplete = completeness < 40;
        incompleteLabel.setVisible(incomplete);
        profileLink.setVisible(incomplete);
        if (incomplete) {
            incompleteLabel.setText("Complete your skin profile first");
            incompleteLabel.setStyle("-fx-text-fill:#FF9800; -fx-font-size:14;");
        }
    }

    @FXML
    private void onGenerate() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            NavigationManager.navigateTo("/fxml/LoginPage.fxml");
            return;
        }

        SkinProfile profile = SessionManager.getCurrentProfile();
        if (profile == null || profile.calculateCompleteness() < 40) {
            AlertHelper.showWarning("Complete your skin profile first");
            return;
        }

        loadingIndicator.setVisible(true);
        statusLabel.setText("Generating routine...");

        Task<Map<String, SkincareRoutine>> task = new Task<Map<String, SkincareRoutine>>() {
            @Override
            protected Map<String, SkincareRoutine> call() {
                return routineService.generateRoutine(user.getUserID());
            }
        };

        task.setOnSucceeded(e -> {
            loadingIndicator.setVisible(false);
            Map<String, SkincareRoutine> map = task.getValue();
            morning = map.get("morning");
            evening = map.get("evening");
            renderRoutine(morningCard, morning);
            renderRoutine(eveningCard, evening);
            statusLabel.setText("Routine generated");
            statusLabel.setStyle("-fx-text-fill:#4CAF50;");
        });

        task.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            statusLabel.setText(task.getException() == null ? "Failed" : task.getException().getMessage());
            statusLabel.setStyle("-fx-text-fill:#F44336;");
        });

        new Thread(task).start();
    }

    private void renderRoutine(VBox box, SkincareRoutine routine) {
        box.getChildren().removeIf(n -> n instanceof VBox);
        if (routine == null || routine.getSteps() == null) return;

        for (RoutineStep step : routine.getSteps()) {
            Label title = new Label(step.getStepNumber() + ". " + step.getPurpose() + " - " + step.getRecommendedProduct());
            title.setStyle("-fx-font-size:14; -fx-font-weight:bold;");
            Label ing = new Label(step.getRecommendedIngredient());
            ing.setStyle("-fx-font-size:14;");
            Label ex = new Label(step.getExplanation());
            ex.setStyle("-fx-font-size:12; -fx-text-fill:#666666;");
            VBox item = new VBox(2, title, ing, ex);
            item.setStyle("-fx-background-color:#FFFFFF; -fx-padding:8; -fx-background-radius:6;");
            box.getChildren().add(item);
        }
    }

    @FXML
    private void onSaveRoutine() {
        AlertHelper.showSuccess("Routine already saved during generation");
    }

    @FXML
    private void onRegenerate() {
        onGenerate();
    }

    @FXML
    private void onExportPdf() {
        try {
            File out = new File("routine_export.html");
            try (FileWriter fw = new FileWriter(out)) {
                fw.write("<html><body><h2>Morning Routine</h2>");
                if (morning != null) {
                    for (RoutineStep s : morning.getSteps()) {
                        fw.write("<p><b>" + s.getStepNumber() + ". " + s.getPurpose() + "</b> - " + s.getRecommendedProduct() + "<br/>" + s.getRecommendedIngredient() + "<br/>" + s.getExplanation() + "</p>");
                    }
                }
                fw.write("<h2>Evening Routine</h2>");
                if (evening != null) {
                    for (RoutineStep s : evening.getSteps()) {
                        fw.write("<p><b>" + s.getStepNumber() + ". " + s.getPurpose() + "</b> - " + s.getRecommendedProduct() + "<br/>" + s.getRecommendedIngredient() + "<br/>" + s.getExplanation() + "</p>");
                    }
                }
                fw.write("</body></html>");
            }
            Desktop.getDesktop().browse(out.toURI());
            AlertHelper.showSuccess("Export created. Use browser print to PDF.");
        } catch (Exception ex) {
            AlertHelper.showError("Export failed: " + ex.getMessage());
        }
    }

    @FXML
    private void onGoProfile() {
        NavigationManager.navigateTo("/fxml/SkinProfilePage.fxml");
    }
}
