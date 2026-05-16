import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.NavigationManager;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginPage.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Smart Skin Health System");
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(650);
            primaryStage.setScene(scene);
            NavigationManager.setStage(primaryStage);
            primaryStage.show();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load initial UI: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
