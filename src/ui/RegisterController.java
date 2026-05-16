package ui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import model.EndUser;
import service.AuthService;
import util.AlertHelper;
import util.NavigationManager;
import util.PasswordUtil;

public class RegisterController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField ageField;
    @FXML private ComboBox<String> genderBox;
    @FXML private Region strengthBar;
    @FXML private Label passwordError;
    @FXML private Label generalError;
    @FXML private Button registerButton;
    @FXML private ProgressIndicator loadingIndicator;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        genderBox.getItems().addAll("Male", "Female", "Other");
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(false);
        }
        passwordField.textProperty().addListener((obs, o, n) -> updateStrength(n));
    }

    @FXML
    private void onRegister() {
        clearErrors();
        String name = safe(nameField.getText());
        String email = safe(emailField.getText());
        String password = safe(passwordField.getText());
        String confirm = safe(confirmPasswordField.getText());
        String ageRaw = safe(ageField.getText());
        String gender = genderBox.getValue();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty() || ageRaw.isEmpty() || gender == null) {
            showGeneral("All fields are required");
            return;
        }
        if (!password.equals(confirm)) {
            passwordError.setText("Passwords do not match");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageRaw);
        } catch (NumberFormatException e) {
            showGeneral("Age must be numeric");
            return;
        }

        registerButton.setDisable(true);
        registerButton.setText("Registering...");
        setLoadingVisible(true);

        Task<EndUser> task = new Task<EndUser>() {
            @Override
            protected EndUser call() {
                return authService.registerUser(name, email, password, age, gender);
            }
        };

        task.setOnSucceeded(e -> {
            registerButton.setDisable(false);
            registerButton.setText("Register");
            setLoadingVisible(false);
            AlertHelper.showSuccess("Account created!");
            NavigationManager.navigateTo("/fxml/LoginPage.fxml");
        });

        task.setOnFailed(e -> {
            registerButton.setDisable(false);
            registerButton.setText("Register");
            setLoadingVisible(false);
            showGeneral(task.getException() == null ? "Registration failed" : task.getException().getMessage());
        });

        new Thread(task).start();
    }

    @FXML
    private void onBackToLogin() {
        NavigationManager.navigateTo("/fxml/LoginPage.fxml");
    }

    private void updateStrength(String password) {
        if (password == null || password.isEmpty()) {
            strengthBar.setStyle("-fx-background-color:transparent;");
            return;
        }
        if (PasswordUtil.isStrong(password)) {
            strengthBar.setStyle("-fx-background-color:#4CAF50; -fx-background-radius:4;");
        } else if (password.length() >= 6) {
            strengthBar.setStyle("-fx-background-color:#FF9800; -fx-background-radius:4;");
        } else {
            strengthBar.setStyle("-fx-background-color:#F44336; -fx-background-radius:4;");
        }
    }

    private void showGeneral(String msg) {
        generalError.setText(msg);
        generalError.setStyle("-fx-text-fill:#F44336; -fx-font-size:14;");
    }

    private void clearErrors() {
        generalError.setText("");
        passwordError.setText("");
    }

    private void setLoadingVisible(boolean visible) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(visible);
        }
    }

    private String safe(String v) {
        return v == null ? "" : v.trim();
    }
}
