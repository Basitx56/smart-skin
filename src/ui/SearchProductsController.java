package ui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Product;
import model.SkinProfile;
import service.FavoriteService;
import service.ProductService;
import util.AlertHelper;
import util.SessionManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchProductsController extends BaseController {
    @FXML private TextField searchField;
    @FXML private ListView<String> suggestionsList;
    @FXML private VBox resultsBox;
    @FXML private Label noResultsLabel;
    @FXML private ProgressIndicator loadingIndicator;

    private final ProductService productService = new ProductService();
    private final FavoriteService favoriteService = new FavoriteService();
    private List<Product> allProducts = new ArrayList<>();

    @FXML
    public void initialize() {
        loadingIndicator.setVisible(false);
        noResultsLabel.setVisible(false);
        suggestionsList.setVisible(false);
        allProducts = productService.getAllProducts();

        searchField.textProperty().addListener((obs, oldV, newV) -> onTyping(newV));
        suggestionsList.setOnMouseClicked(e -> {
            String selected = suggestionsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                searchField.setText(selected);
                suggestionsList.setVisible(false);
            }
        });
    }

    private void onTyping(String text) {
        if (text == null || text.trim().length() < 2) {
            suggestionsList.setVisible(false);
            return;
        }
        Set<String> suggestions = new HashSet<>();
        for (Product p : allProducts) {
            if (p.getIngredients() != null) {
                for (String ing : p.getIngredients()) {
                    if (ing.toLowerCase().contains(text.toLowerCase())) {
                        suggestions.add(ing);
                    }
                }
            }
        }
        suggestionsList.getItems().setAll(suggestions.stream().limit(10).collect(Collectors.toList()));
        suggestionsList.setVisible(!suggestionsList.getItems().isEmpty());
    }

    @FXML
    private void onSearch() {
        String ingredient = searchField.getText() == null ? "" : searchField.getText().trim();
        if (ingredient.isEmpty()) {
            AlertHelper.showWarning("Enter an ingredient to search");
            return;
        }

        loadingIndicator.setVisible(true);
        resultsBox.getChildren().clear();

        Task<List<Product>> task = new Task<List<Product>>() {
            @Override
            protected List<Product> call() {
                SkinProfile profile = SessionManager.getCurrentProfile();
                return productService.searchByIngredient(ingredient, profile);
            }
        };

        task.setOnSucceeded(e -> {
            loadingIndicator.setVisible(false);
            List<Product> products = task.getValue();
            noResultsLabel.setVisible(products.isEmpty());
            for (Product product : products) {
                resultsBox.getChildren().add(buildCard(product));
            }
        });

        task.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            AlertHelper.showError(task.getException() == null ? "Search failed" : task.getException().getMessage());
        });

        new Thread(task).start();
    }

    private VBox buildCard(Product product) {
        Label name = new Label(product.getName());
        name.setStyle("-fx-font-size:16; -fx-font-weight:bold;");
        Label meta = new Label(product.getBrand() + " | " + product.getCategory());

        Label badge = new Label();
        String s = product.getSuitability();
        if ("suitable".equalsIgnoreCase(s)) {
            badge.setText("✓ Suitable");
            badge.setStyle("-fx-background-color:#4CAF50; -fx-text-fill:white; -fx-padding:4 8;");
        } else if ("not_recommended".equalsIgnoreCase(s)) {
            badge.setText("✗ Not Recommended");
            badge.setStyle("-fx-background-color:#F44336; -fx-text-fill:white; -fx-padding:4 8;");
        } else {
            badge.setText("⚠ Caution");
            badge.setStyle("-fx-background-color:#FF9800; -fx-text-fill:white; -fx-padding:4 8;");
        }

        Button view = new Button("View Details");
        view.setOnAction(e -> onViewDetails(product));

        VBox card = new VBox(6, name, meta, badge, new HBox(8, view));
        card.setStyle("-fx-background-color:white; -fx-padding:10; -fx-background-radius:8;");
        return card;
    }

    private void onViewDetails(Product product) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Product Details");
        alert.setHeaderText(product.getName());
        String details = "Ingredients: " + String.join(", ", product.getIngredients())
            + "\nUsage: Apply as directed"
            + "\nPrice: " + product.getPrice();
        alert.setContentText(details + "\n\nPress OK to Save to Favorites");
        ButtonType ok = ButtonType.OK;
        ButtonType cancel = ButtonType.CANCEL;
        alert.getButtonTypes().setAll(ok, cancel);
        if (alert.showAndWait().orElse(cancel) == ok) {
            boolean saved = favoriteService.addFavorite(SessionManager.getCurrentUser().getUserID(), product.getProductID());
            if (saved) {
                AlertHelper.showSuccess("Saved to favorites");
            } else {
                AlertHelper.showWarning("Already in favorites");
            }
        }
    }
}
