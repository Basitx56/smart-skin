package ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.AuditLog;
import model.User;
import service.AdminService;
import util.AlertHelper;
import util.SessionManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserManagementController extends BaseController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterBox;
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, Boolean> colStatus;
    @FXML private TableColumn<User, String> colRegistered;
    @FXML private TableView<AuditLog> auditTable;
    @FXML private TableColumn<AuditLog, String> auditActorCol;
    @FXML private TableColumn<AuditLog, String> auditActionCol;
    @FXML private TableColumn<AuditLog, String> auditTimeCol;
    @FXML private Label statusLabel;

    private final AdminService adminService = AdminService.getInstance();

    @FXML
    public void initialize() {
        User current = SessionManager.getCurrentUser();
        if (current == null || !"admin".equalsIgnoreCase(current.getRole())) {
            AlertHelper.showError("Admin only page");
            return;
        }

        filterBox.getItems().addAll("all", "end_user", "expert", "admin", "active", "inactive");
        filterBox.setValue("all");

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("active"));
        colRegistered.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        auditActorCol.setCellValueFactory(new PropertyValueFactory<>("actorID"));
        auditActionCol.setCellValueFactory(new PropertyValueFactory<>("action"));
        auditTimeCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        loadTables();
        searchField.textProperty().addListener((o, ov, nv) -> applyFilter());
        filterBox.valueProperty().addListener((o, ov, nv) -> applyFilter());
    }

    private void loadTables() {
        usersTable.setItems(FXCollections.observableArrayList(adminService.getAllUsers()));
        auditTable.setItems(FXCollections.observableArrayList(adminService.getAuditLogs()));
    }

    private void applyFilter() {
        String query = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String filter = filterBox.getValue();
        List<User> filtered = adminService.getAllUsers().stream().filter(u -> {
            boolean matchQ = u.getName().toLowerCase().contains(query) || u.getEmail().toLowerCase().contains(query);
            boolean matchF = "all".equals(filter)
                || u.getRole().equalsIgnoreCase(filter)
                || ("active".equals(filter) && u.isActive())
                || ("inactive".equals(filter) && !u.isActive());
            return matchQ && matchF;
        }).collect(Collectors.toList());
        usersTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void onDeactivateActivate() {
        User target = usersTable.getSelectionModel().getSelectedItem();
        if (target == null) return;
        User admin = SessionManager.getCurrentUser();
        boolean ok;
        if (target.isActive()) {
            Optional<String> reason = AlertHelper.showInputDialog("Deactivate User", "Enter reason");
            ok = reason.isPresent() && adminService.deactivateUser(admin.getUserID(), target.getUserID(), reason.get());
        } else {
            ok = adminService.activateUser(admin.getUserID(), target.getUserID());
        }
        statusLabel.setText(ok ? "Updated" : "Operation failed");
        statusLabel.setStyle(ok ? "-fx-text-fill:#4CAF50;" : "-fx-text-fill:#F44336;");
        loadTables();
    }

    @FXML
    private void onChangeRole() {
        User target = usersTable.getSelectionModel().getSelectedItem();
        if (target == null) return;
        Optional<String> role = AlertHelper.showInputDialog("Assign Role", "Enter role: end_user/expert/admin");
        if (role.isEmpty()) return;
        boolean ok = adminService.assignRole(SessionManager.getCurrentUser().getUserID(), target.getUserID(), role.get());
        statusLabel.setText(ok ? "Role updated" : "Role update failed");
        statusLabel.setStyle(ok ? "-fx-text-fill:#4CAF50;" : "-fx-text-fill:#F44336;");
        loadTables();
    }

    @FXML
    private void onCreateUser() {
        Optional<String> name = AlertHelper.showInputDialog("Create User", "Name");
        if (name.isEmpty()) return;
        Optional<String> email = AlertHelper.showInputDialog("Create User", "Email");
        if (email.isEmpty()) return;
        Optional<String> pass = AlertHelper.showInputDialog("Create User", "Password");
        if (pass.isEmpty()) return;
        Optional<String> role = AlertHelper.showInputDialog("Create User", "Role end_user/expert/admin");
        if (role.isEmpty()) return;
        boolean ok = adminService.createAccount(SessionManager.getCurrentUser().getUserID(), name.get(), email.get(), pass.get(), role.get());
        statusLabel.setText(ok ? "User created" : "Create failed");
        statusLabel.setStyle(ok ? "-fx-text-fill:#4CAF50;" : "-fx-text-fill:#F44336;");
        loadTables();
    }
}
