package ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import model.ValidationQueueItem;
import model.User;
import service.ValidationService;
import util.AlertHelper;
import util.NavigationManager;
import util.SessionManager;

import java.util.List;

public class ValidationQueueController extends BaseController {
    @FXML private Label pendingCountLabel;
    @FXML private TableView<ValidationQueueItem> queueTable;
    @FXML private TableColumn<ValidationQueueItem, String> colDate;
    @FXML private TableColumn<ValidationQueueItem, String> colSkinType;
    @FXML private TableColumn<ValidationQueueItem, String> colStatus;
    @FXML private Label profileLabel;
    @FXML private VBox morningBox;
    @FXML private VBox eveningBox;
    @FXML private TextField rejectionReasonField;

    private final ValidationService validationService = new ValidationService();

    @FXML
    public void initialize() {
        User current = SessionManager.getCurrentUser();
        if (current == null || !"expert".equalsIgnoreCase(current.getRole())) {
            NavigationManager.navigateTo("/fxml/LoginPage.fxml");
            return;
        }

        colDate.setCellValueFactory(new PropertyValueFactory<>("submittedAt"));
        colSkinType.setCellValueFactory(new PropertyValueFactory<>("routineID"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        loadQueue();

        queueTable.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv != null) {
                profileLabel.setText("Skin Type: Unknown | Concerns: Anonymized");
                morningBox.getChildren().setAll(new Label("Morning routine steps editable in modify mode"));
                eveningBox.getChildren().setAll(new Label("Evening routine steps editable in modify mode"));
            }
        });
    }

    private void loadQueue() {
        List<ValidationQueueItem> pending = validationService.getPendingItems();
        queueTable.setItems(FXCollections.observableArrayList(pending));
        pendingCountLabel.setText(String.valueOf(pending.size()));
    }

    @FXML
    private void onApprove() {
        ValidationQueueItem item = queueTable.getSelectionModel().getSelectedItem();
        if (item == null) return;
        boolean ok = validationService.processReview(item.getItemID(), SessionManager.getCurrentUser().getUserID(), "approve", null);
        if (ok) {
            AlertHelper.showSuccess("Approved");
            loadQueue();
        } else {
            AlertHelper.showError("Failed to approve");
        }
    }

    @FXML
    private void onModify() {
        ValidationQueueItem item = queueTable.getSelectionModel().getSelectedItem();
        if (item == null) return;
        boolean ok = validationService.processReview(item.getItemID(), SessionManager.getCurrentUser().getUserID(), "modify", null);
        if (ok) {
            AlertHelper.showWarning("Marked as modified");
            loadQueue();
        }
    }

    @FXML
    private void onReject() {
        ValidationQueueItem item = queueTable.getSelectionModel().getSelectedItem();
        if (item == null) return;
        String reason = rejectionReasonField.getText();
        boolean ok = validationService.processReview(item.getItemID(), SessionManager.getCurrentUser().getUserID(), "reject", reason);
        if (ok) {
            AlertHelper.showError("Rejected");
            loadQueue();
        }
    }

    @FXML
    private void onFlagSafety() {
        ValidationQueueItem item = queueTable.getSelectionModel().getSelectedItem();
        if (item == null) return;
        String reason = rejectionReasonField.getText();
        boolean ok = validationService.processReview(item.getItemID(), SessionManager.getCurrentUser().getUserID(), "flag_safety", reason);
        if (ok) {
            AlertHelper.showWarning("Flagged and reported to admin");
            loadQueue();
        }
    }
}
