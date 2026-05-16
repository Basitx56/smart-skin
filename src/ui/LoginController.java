package ui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import model.User;
import service.AuthService;
import util.NavigationManager;
import util.SessionManager;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Hyperlink registerLink;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        loadingIndicator.setVisible(false);
    }

    @FXML
    private void onLogin() {
        errorLabel.setVisible(false);
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Email and password are required");
            return;
        }

        loginButton.setText("Logging in...");
        loginButton.setDisable(true);
        loadingIndicator.setVisible(true);

        Task<User> task = new Task<User>() {
            @Override
            protected User call() {
                return authService.loginUser(email, password);
            }
        };

        task.setOnSucceeded(e -> {
            loginButton.setText("Login");
            loginButton.setDisable(false);
            loadingIndicator.setVisible(false);

            User user = task.getValue();
            if (user == null) {
                showError("Invalid email or password");
                return;
            }

            SessionManager.setCurrentUser(user);
            if ("end_user".equalsIgnoreCase(user.getRole())) {
                NavigationManager.navigateTo("/fxml/DashboardPage.fxml");
            } else if ("expert".equalsIgnoreCase(user.getRole())) {
                NavigationManager.navigateTo("/fxml/ValidationQueuePage.fxml");
            } else {
                NavigationManager.navigateTo("/fxml/UserManagementPage.fxml");
            }
        });

        task.setOnFailed(e -> {
            loginButton.setText("Login");
            loginButton.setDisable(false);
            loadingIndicator.setVisible(false);
            showError(task.getException() == null ? "Login failed" : task.getException().getMessage());
        });

        new Thread(task).start();
    }

    @FXML
    private void onRegister() {
        NavigationManager.navigateTo("/fxml/RegisterPage.fxml");
    }

    @FXML
    private void onForgotPassword() {
        showError("Password reset is not configured yet");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill:#F44336; -fx-font-size:14;");
        errorLabel.setVisible(true);
    }
}
