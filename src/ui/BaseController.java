package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class BaseController {

    protected void navigate(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(
                getClass().getResource("/styles/darktheme.css").toExternalForm()
            );
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Navigation failed to: " + fxmlPath, e);
        }
    }

    @FXML
    public void goDashboard(ActionEvent e) {
        navigate(e, "/fxml/DashboardPage.fxml");
    }

    @FXML
    public void goSkinProfile(ActionEvent e) {
        navigate(e, "/fxml/SkinProfilePage.fxml");
    }

    @FXML
    public void goUploadPhoto(ActionEvent e) {
        navigate(e, "/fxml/UploadPhotoPage.fxml");
    }

    @FXML
    public void goSearch(ActionEvent e) {
        navigate(e, "/fxml/SearchProductsPage.fxml");
    }

    @FXML
    public void goAppointment(ActionEvent e) {
        navigate(e, "/fxml/BookAppointmentPage.fxml");
    }

    @FXML
    public void goRoutine(ActionEvent e) {
        navigate(e, "/fxml/GenerateRoutinePage.fxml");
    }

    @FXML
    public void goConflict(ActionEvent e) {
        navigate(e, "/fxml/DetectConflictsPage.fxml");
    }

    @FXML
    public void goProgress(ActionEvent e) {
        navigate(e, "/fxml/TrackProgressPage.fxml");
    }

    @FXML
    public void goFavorites(ActionEvent e) {
        navigate(e, "/fxml/FavoriteProductsPage.fxml");
    }

    @FXML
    public void goNotifications(ActionEvent e) {
        navigate(e, "/fxml/NotificationSettingsPage.fxml");
    }

    @FXML
    public void goUserManagement(ActionEvent e) {
        navigate(e, "/fxml/UserManagementPage.fxml");
    }

    @FXML
    public void goProductManagement(ActionEvent e) {
        navigate(e, "/fxml/ProductManagementPage.fxml");
    }

    @FXML
    public void onLogout(ActionEvent e) {
        util.SessionManager.clearSession();
        navigate(e, "/fxml/LoginPage.fxml");
    }
}