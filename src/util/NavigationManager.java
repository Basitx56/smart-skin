package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationManager {
    private static Stage mainStage;

    public static void setStage(Stage stage) {
        mainStage = stage;
    }

    public static void navigateTo(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(NavigationManager.class.getResource(fxmlPath));
            Scene scene = new Scene(root);
            mainStage.setScene(scene);
            mainStage.show();
        } catch (Exception e) {
            throw new RuntimeException("Navigation failed to " + fxmlPath + ": " + e.getMessage(), e);
        }
    }

    public static void navigateWithData(String fxmlPath, Object data) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            Object controller = loader.getController();
            try {
                controller.getClass().getMethod("setData", Object.class).invoke(controller, data);
            } catch (NoSuchMethodException ignored) {
            }
            Scene scene = new Scene(root);
            mainStage.setScene(scene);
            mainStage.show();
        } catch (Exception e) {
            throw new RuntimeException("Navigation with data failed to " + fxmlPath + ": " + e.getMessage(), e);
        }
    }
}
