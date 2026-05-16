package service;

import model.Product;
import model.SkinProfile;
import repository.ProductRepository;

import java.util.Collections;
import java.util.List;

/**
 * Handles product search and suitability logic.
 * GRASP Information Expert: calculates suitability
 * GOF Strategy: suitability calculation
 */
public class ProductService {
    private final ProductRepository productRepo;

    public ProductService() {
        this.productRepo = new ProductRepository();
    }

    /**
     * Search products by ingredient with suitability.
     */
    public List<Product> searchByIngredient(String ingredient, SkinProfile userProfile) {
        List<Product> products = productRepo.findByIngredient(ingredient);
        if (products == null) {
            return Collections.emptyList();
        }
        for (Product product : products) {
            product.setSuitability(calculateSuitability(product, userProfile));
        }
        return products;
    }

    /**
     * Calculate product suitability for user.
     * Returns: "suitable", "caution", "not_recommended"
     */
    public String calculateSuitability(Product product, SkinProfile profile) {
        if (product == null || profile == null) {
            return "caution";
        }

        List<String> allergies = profile.getKnownAllergies();
        List<String> ingredients = product.getIngredients();
        if (allergies != null && ingredients != null) {
            for (String allergy : allergies) {
                for (String ingredient : ingredients) {
                    if (ingredient.toLowerCase().contains(allergy.toLowerCase())) {
                        return "not_recommended";
                    }
                }
            }
        }

        if (profile.getSkinType() != null && product.getSuitableSkinTypes() != null) {
            String skin = profile.getSkinType().toLowerCase();
            for (String t : product.getSuitableSkinTypes()) {
                if (t.toLowerCase().contains(skin)) {
                    return "suitable";
                }
            }
        }

        return "caution";
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product getProductByID(String id) {
        return productRepo.findByID(id);
    }

    public boolean addProduct(Product product) {
        return productRepo.save(product);
    }

    public boolean updateProduct(Product product) {
        return productRepo.update(product);
    }

    public boolean deleteProduct(String productID) {
        return productRepo.delete(productID);
    }

    public boolean nameExists(String name) {
        return productRepo.nameExists(name);
    }
}
