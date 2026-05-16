package ui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import model.User;
import service.ProgressTrackingService;
import util.AlertHelper;
import util.NavigationManager;
import util.SessionManager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

public class UploadPhotoController extends BaseController {
    @FXML private ImageView previewImage;
    @FXML private DatePicker photoDatePicker;
    @FXML private TextArea notesArea;
    @FXML private Label statusLabel;
    @FXML private ProgressIndicator loadingIndicator;

    private File selectedFile;
    private final ProgressTrackingService progressService = new ProgressTrackingService();

    @FXML
    public void initialize() {
        previewImage.setVisible(false);
        loadingIndicator.setVisible(false);
        photoDatePicker.setValue(LocalDate.now());
    }

    @FXML
    private void onSelectPhoto() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.webp")
        );
        File file = chooser.showOpenDialog(previewImage.getScene().getWindow());
        if (file == null) {
            return;
        }

        if (file.length() > 10 * 1024 * 1024L) {
            statusLabel.setText("File exceeds 10MB");
            statusLabel.setStyle("-fx-text-fill:#F44336;");
            return;
        }

        String n = file.getName().toLowerCase();
        if (!(n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png") || n.endsWith(".webp"))) {
            statusLabel.setText("Only JPG, PNG, WEBP are allowed");
            statusLabel.setStyle("-fx-text-fill:#F44336;");
            return;
        }

        selectedFile = file;
        previewImage.setImage(new Image(file.toURI().toString()));
        previewImage.setVisible(true);
        statusLabel.setText("Photo selected");
        statusLabel.setStyle("-fx-text-fill:#4CAF50;");
    }

    @FXML
    private void onUpload() {
        User user = SessionManager.getCurrentUser();
        if (user == null || selectedFile == null) {
            statusLabel.setText("Please select a photo first");
            statusLabel.setStyle("-fx-text-fill:#F44336;");
            return;
        }

        loadingIndicator.setVisible(true);
        statusLabel.setText("Uploading...");

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Path photosDir = Path.of("photos");
                if (!Files.exists(photosDir)) {
                    Files.createDirectories(photosDir);
                }
                String targetName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path target = photosDir.resolve(targetName);
                Files.copy(selectedFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);

                progressService.addEntry(
                    user.getUserID(),
                    photoDatePicker.getValue(),
                    3,
                    3,
                    3,
                    3,
                    target.toString(),
                    notesArea.getText()
                );
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            loadingIndicator.setVisible(false);
            statusLabel.setText("Photo uploaded successfully");
            statusLabel.setStyle("-fx-text-fill:#4CAF50;");
            AlertHelper.showSuccess("Photo uploaded");
        });

        task.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            statusLabel.setText(task.getException() == null ? "Upload failed" : task.getException().getMessage());
            statusLabel.setStyle("-fx-text-fill:#F44336;");
        });

        new Thread(task).start();
    }

    @FXML
    private void onBack() {
        NavigationManager.navigateTo("/fxml/DashboardPage.fxml");
    }
}
