package repository;

import model.Product;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProductRepository extends BaseRepository {
    public boolean save(Product product) {
        String sql = "INSERT INTO products (productID, name, brand, category, ingredients, suitableSkinTypes, price, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            if (product.getProductID() == null || product.getProductID().isEmpty()) {
                product.setProductID(UUID.randomUUID().toString());
            }
            ps.setString(1, product.getProductID());
            ps.setString(2, product.getName());
            ps.setString(3, product.getBrand());
            ps.setString(4, product.getCategory());
            ps.setString(5, joinList(product.getIngredients()));
            ps.setString(6, joinList(product.getSuitableSkinTypes()));
            ps.setDouble(7, product.getPrice());
            ps.setString(8, product.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY createdAt DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            return products;
        }
        return products;
    }

    public Product findByID(String productID) {
        String sql = "SELECT * FROM products WHERE productID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, productID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapProduct(rs);
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    /** Search products containing ingredient */
    public List<Product> findByIngredient(String ingredient) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE LOWER(ingredients) LIKE ? ORDER BY name ASC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, "%" + ingredient.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapProduct(rs));
                }
            }
        } catch (SQLException e) {
            return products;
        }
        return products;
    }

    public boolean update(Product product) {
        String sql = "UPDATE products SET name=?, brand=?, category=?, ingredients=?, suitableSkinTypes=?, price=?, description=? WHERE productID=?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getBrand());
            ps.setString(3, product.getCategory());
            ps.setString(4, joinList(product.getIngredients()));
            ps.setString(5, joinList(product.getSuitableSkinTypes()));
            ps.setDouble(6, product.getPrice());
            ps.setString(7, product.getDescription());
            ps.setString(8, product.getProductID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean delete(String productID) {
        String sql = "DELETE FROM products WHERE productID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, productID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean nameExists(String name) {
        String sql = "SELECT COUNT(*) FROM products WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        return new Product(
            rs.getString("productID"),
            rs.getString("name"),
            rs.getString("brand"),
            rs.getString("category"),
            splitList(rs.getString("ingredients")),
            splitList(rs.getString("suitableSkinTypes")),
            rs.getDouble("price"),
            rs.getString("description")
        );
    }

    private String joinList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.stream().map(String::trim).collect(Collectors.joining(","));
    }

    private List<String> splitList(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(raw.split(",")).map(String::trim).collect(Collectors.toList());
    }
}
