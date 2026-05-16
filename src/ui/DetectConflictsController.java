package ui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import model.Product;
import model.SkinProfile;
import service.ConflictResult;
import service.IngredientConflictService;
import service.ProductService;
import util.AlertHelper;
import util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DetectConflictsController extends BaseController {
    @FXML private TextField searchField;
    @FXML private ListView<String> suggestionsList;
    @FXML private Label selectedProductLabel;
    @FXML private Label ingredientsLabel;
    @FXML private VBox resultBanner;
    @FXML private Label resultText;
    @FXML private CheckBox acknowledgeBox;
    @FXML private VBox alternativesBox;
    @FXML private ProgressIndicator loadingIndicator;

    private final ProductService productService = new ProductService();
    private final IngredientConflictService conflictService = new IngredientConflictService();
    private List<Product> products = new ArrayList<>();
    private Product selectedProduct;

    @FXML
    public void initialize() {
        products = productService.getAllProducts();
        suggestionsList.setVisible(false);
        resultBanner.setVisible(false);
        alternativesBox.setVisible(false);
        loadingIndicator.setVisible(false);

        searchField.textProperty().addListener((o, ov, nv) -> {
            if (nv == null || nv.trim().length() < 2) {
                suggestionsList.setVisible(false);
                return;
            }
            List<String> names = products.stream()
                .map(Product::getName)
                .filter(n -> n.toLowerCase().contains(nv.toLowerCase()))
                .limit(10)
                .collect(Collectors.toList());
            suggestionsList.getItems().setAll(names);
            suggestionsList.setVisible(!names.isEmpty());
        });

        suggestionsList.setOnMouseClicked(e -> {
            String name = suggestionsList.getSelectionModel().getSelectedItem();
            if (name == null) return;
            selectedProduct = products.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
            suggestionsList.setVisible(false);
            searchField.setText(name);
            runCheck();
        });

        acknowledgeBox.selectedProperty().addListener((o, ov, nv) -> {
            if (nv && selectedProduct != null && SessionManager.getCurrentUser() != null) {
                conflictService.logAcknowledgement(SessionManager.getCurrentUser().getUserID(), selectedProduct.getName());
                AlertHelper.showSuccess("Acknowledgment logged");
            }
        });
    }

    private void runCheck() {
        if (selectedProduct == null) return;
        selectedProductLabel.setText(selectedProduct.getName() + " - " + selectedProduct.getBrand());
        loadingIndicator.setVisible(true);

        Task<ConflictResult> task = new Task<ConflictResult>() {
            @Override
            protected ConflictResult call() {
                SkinProfile profile = SessionManager.getCurrentProfile();
                return conflictService.checkConflicts(selectedProduct.getIngredients(), profile);
            }
        };

        task.setOnSucceeded(e -> {
            loadingIndicator.setVisible(false);
            ConflictResult result = task.getValue();
            ingredientsLabel.setText(formatIngredients(result));
            resultBanner.setVisible(true);
            if (result.isSafe()) {
                resultBanner.setStyle("-fx-background-color:#4CAF50; -fx-padding:10; -fx-background-radius:8;");
                resultText.setText("Safe to Use");
                alternativesBox.setVisible(false);
            } else if (!result.getConflicts().isEmpty()) {
                resultBanner.setStyle("-fx-background-color:#F44336; -fx-padding:10; -fx-background-radius:8;");
                resultText.setText("Warnings found: " + result.getConflicts().size());
                alternativesBox.setVisible(true);
                fillAlternatives();
            } else {
                resultBanner.setStyle("-fx-background-color:#FF9800; -fx-padding:10; -fx-background-radius:8;");
                resultText.setText("Caution: possible allergen interaction");
                alternativesBox.setVisible(true);
                fillAlternatives();
            }
        });

        task.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            AlertHelper.showError(task.getException() == null ? "Conflict check failed" : task.getException().getMessage());
        });

        new Thread(task).start();
    }

    @FXML
    private void onShowAlternatives() {
        alternativesBox.setVisible(true);
        fillAlternatives();
    }

    private void fillAlternatives() {
        alternativesBox.getChildren().clear();
        productService.getAllProducts().stream().limit(5).forEach(p -> alternativesBox.getChildren().add(new Label("• " + p.getName())));
    }

    private String formatIngredients(ConflictResult result) {
        List<String> parts = new ArrayList<>();
        for (String ing : selectedProduct.getIngredients()) {
            boolean hit = result.getAllergenHits().stream().anyMatch(a -> ing.toLowerCase().contains(a.toLowerCase()));
            parts.add(hit ? "[RED] " + ing : ing);
        }
        return String.join(", ", parts);
    }
}
