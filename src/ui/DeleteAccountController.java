package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import service.AccountService;
import util.AlertHelper;
import util.NavigationManager;
import util.SessionManager;

import java.util.Optional;

public class DeleteAccountController extends BaseController {
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final AccountService accountService = new AccountService();

    @FXML
    private void onDelete() {
        if (!AlertHelper.showConfirmation("Delete Account", "Are you absolutely sure?")) {
            return;
        }
        Optional<String> typed = AlertHelper.showInputDialog("Confirm Deletion", "Type DELETE to confirm");
        if (typed.isEmpty() || !"DELETE".equals(typed.get())) {
            statusLabel.setText("Deletion cancelled: confirmation word mismatch");
            statusLabel.setStyle("-fx-text-fill:#F44336;");
            return;
        }

        String pwd = passwordField.getText() == null ? "" : passwordField.getText();
        boolean ok = accountService.deleteAccount(SessionManager.getCurrentUser().getUserID(), pwd);
        if (ok) {
            SessionManager.clearSession();
            AlertHelper.showSuccess("Account deleted. Goodbye.");
            NavigationManager.navigateTo("/fxml/LoginPage.fxml");
        } else {
            statusLabel.setText("Failed to delete account. Check password.");
            statusLabel.setStyle("-fx-text-fill:#F44336;");
        }
    }

    @FXML
    private void onCancel() {
        NavigationManager.navigateTo("/fxml/DashboardPage.fxml");
    }
}
