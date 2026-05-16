package model;

import java.util.List;

public class Product {
    private String productID;
    private String name;
    private String brand;
    private String category;
    private List<String> ingredients;
    private List<String> suitableSkinTypes;
    private double price;
    private String description;
    private String suitability;

    public Product(String productID,
                   String name,
                   String brand,
                   String category,
                   List<String> ingredients,
                   List<String> suitableSkinTypes,
                   double price,
                   String description) {
        this.productID = productID;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.ingredients = ingredients;
        this.suitableSkinTypes = suitableSkinTypes;
        this.price = price;
        this.description = description;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getSuitableSkinTypes() {
        return suitableSkinTypes;
    }

    public void setSuitableSkinTypes(List<String> suitableSkinTypes) {
        this.suitableSkinTypes = suitableSkinTypes;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSuitability() {
        return suitability;
    }

    public void setSuitability(String suitability) {
        this.suitability = suitability;
    }
}
