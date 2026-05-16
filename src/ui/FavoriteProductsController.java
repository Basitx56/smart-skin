package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import model.Product;
import model.User;
import service.FavoriteService;
import util.AlertHelper;
import util.SessionManager;

import java.util.List;

public class FavoriteProductsController extends BaseController {
    @FXML private FlowPane cardsPane;
    @FXML private Label emptyLabel;

    private final FavoriteService favoriteService = new FavoriteService();

    @FXML
    public void initialize() {
        refresh();
    }

    private void refresh() {
        User user = SessionManager.getCurrentUser();
        if (user == null) return;
        List<Product> items = favoriteService.getFavorites(user.getUserID());
        cardsPane.getChildren().clear();
        emptyLabel.setVisible(items.isEmpty());

        for (Product p : items) {
            Label name = new Label(p.getName());
            name.setStyle("-fx-font-size:16; -fx-font-weight:bold;");
            Label meta = new Label(p.getBrand() + " | " + p.getCategory());
            Button remove = new Button("Remove");
            remove.setStyle("-fx-background-color:#F44336; -fx-text-fill:white;");
            remove.setOnAction(e -> {
                favoriteService.removeFavorite(user.getUserID(), p.getProductID());
                refresh();
            });
            Button details = new Button("View Details");
            details.setOnAction(e -> AlertHelper.showSuccess("Ingredients: " + String.join(", ", p.getIngredients())));
            VBox card = new VBox(6, name, meta, remove, details);
            card.setStyle("-fx-background-color:white; -fx-padding:10; -fx-background-radius:8;");
            card.setPrefWidth(220);
            cardsPane.getChildren().add(card);
        }
    }
}
