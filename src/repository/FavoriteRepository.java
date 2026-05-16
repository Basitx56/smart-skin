package repository;

import model.Product;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FavoriteRepository extends BaseRepository {
    public boolean save(String userID, String productID) {
        String sql = "INSERT INTO favorites (userID, productID) VALUES (?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, userID);
            ps.setString(2, productID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean delete(String userID, String productID) {
        String sql = "DELETE FROM favorites WHERE userID = ? AND productID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, userID);
            ps.setString(2, productID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Product> findByUser(String userID) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.* FROM favorites f JOIN products p ON f.productID = p.productID WHERE f.userID = ? ORDER BY f.savedAt DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, userID);
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

    public boolean exists(String userID, String productID) {
        String sql = "SELECT COUNT(*) FROM favorites WHERE userID = ? AND productID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, userID);
            ps.setString(2, productID);
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

    private List<String> splitList(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(raw.split(",")).map(String::trim).collect(Collectors.toList());
    }
}
