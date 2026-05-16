package ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Product;
import model.User;
import service.ProductService;
import util.AlertHelper;
import util.SessionManager;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductManagementController extends BaseController {
    @FXML private TextField searchField;
    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colBrand;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, Double> colPrice;

    private final ProductService productService = new ProductService();

    @FXML
    public void initialize() {
        User current = SessionManager.getCurrentUser();
        if (current == null || !"admin".equalsIgnoreCase(current.getRole())) {
            AlertHelper.showError("Admin only page");
            return;
        }

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        refreshTable();

        searchField.textProperty().addListener((o, ov, nv) -> filter(nv));
    }

    private void refreshTable() {
        productsTable.setItems(FXCollections.observableArrayList(productService.getAllProducts()));
    }

    private void filter(String q) {
        String query = q == null ? "" : q.toLowerCase();
        List<Product> filtered = productService.getAllProducts().stream().filter(p ->
            p.getName().toLowerCase().contains(query)
                || p.getBrand().toLowerCase().contains(query)
                || p.getCategory().toLowerCase().contains(query)
        ).collect(Collectors.toList());
        productsTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void onAddProduct() {
        Optional<String> name = AlertHelper.showInputDialog("Add Product", "Name");
        if (name.isEmpty()) return;
        if (productService.nameExists(name.get())) {
            AlertHelper.showError("Duplicate product name");
            return;
        }
        Optional<String> brand = AlertHelper.showInputDialog("Add Product", "Brand");
        Optional<String> category = AlertHelper.showInputDialog("Add Product", "Category");
        Optional<String> ingredients = AlertHelper.showInputDialog("Add Product", "Ingredients (comma-separated)");
        Optional<String> suitable = AlertHelper.showInputDialog("Add Product", "Suitable skin types (comma-separated)");
        Optional<String> price = AlertHelper.showInputDialog("Add Product", "Price");
        Optional<String> description = AlertHelper.showInputDialog("Add Product", "Description");
        if (brand.isEmpty() || category.isEmpty() || ingredients.isEmpty() || suitable.isEmpty() || price.isEmpty() || description.isEmpty()) return;

        List<String> ing = Arrays.stream(ingredients.get().split(",")).map(s -> s.trim().toLowerCase()).collect(Collectors.toList());
        List<String> skins = Arrays.stream(suitable.get().split(",")).map(String::trim).collect(Collectors.toList());
        Product p = new Product(null, name.get(), brand.get(), category.get(), ing, skins, Double.parseDouble(price.get()), description.get());
        boolean ok = productService.addProduct(p);
        if (ok) {
            AlertHelper.showSuccess("Product added");
            refreshTable();
        } else {
            AlertHelper.showError("Add failed");
        }
    }

    @FXML
    private void onEditProduct() {
        Product p = productsTable.getSelectionModel().getSelectedItem();
        if (p == null) return;
        Optional<String> price = AlertHelper.showInputDialog("Edit Product", "New price");
        if (price.isEmpty()) return;
        p.setPrice(Double.parseDouble(price.get()));
        boolean ok = productService.updateProduct(p);
        if (ok) {
            AlertHelper.showSuccess("Product updated");
            refreshTable();
        } else {
            AlertHelper.showError("Update failed");
        }
    }

    @FXML
    private void onDeleteProduct() {
        Product p = productsTable.getSelectionModel().getSelectedItem();
        if (p == null) return;
        if (!AlertHelper.showConfirmation("Delete", "Delete selected product?")) return;
        boolean ok = productService.deleteProduct(p.getProductID());
        if (ok) {
            AlertHelper.showSuccess("Deleted");
            refreshTable();
        } else {
            AlertHelper.showError("Delete failed");
        }
    }
}
