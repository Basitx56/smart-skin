package repository;

import model.EndUser;
import model.SkincareExpert;
import model.SystemAdmin;
import model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all user database operations.
 * Architecture: Data Access Layer
 * GRASP: Low Coupling - only depends on DBConnection
 */
public class UserRepository extends BaseRepository {

    /** Save a new user to database */
    public boolean save(User user) {
        String sql = "INSERT INTO users (userID, name, email, password, age, gender, role, specialization, consultationFee, rating, isVerified, isActive, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, user.getUserID());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setInt(5, user.getAge());
            ps.setString(6, user.getGender());
            ps.setString(7, user.getRole());

            if (user instanceof SkincareExpert) {
                SkincareExpert expert = (SkincareExpert) user;
                ps.setString(8, expert.getSpecialization());
                ps.setDouble(9, expert.getConsultationFee());
                ps.setDouble(10, expert.getRating());
                ps.setBoolean(11, expert.isVerified());
            } else {
                ps.setNull(8, java.sql.Types.VARCHAR);
                ps.setNull(9, java.sql.Types.DOUBLE);
                ps.setDouble(10, 0.0);
                ps.setBoolean(11, false);
            }

            ps.setBoolean(12, user.isActive());
            ps.setTimestamp(13, Timestamp.valueOf(user.getCreatedAt()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    /** Find user by email for login */
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    /** Find user by ID */
    public User findByID(String userID) {
        String sql = "SELECT * FROM users WHERE userID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    /** Get all users (admin use) */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY createdAt DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            return users;
        }
        return users;
    }

    /** Get all verified experts */
    public List<SkincareExpert> findAllExperts() {
        List<SkincareExpert> experts = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role='expert' AND isVerified = true AND isActive = true ORDER BY rating DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = mapUser(rs);
                if (user instanceof SkincareExpert) {
                    experts.add((SkincareExpert) user);
                }
            }
        } catch (SQLException e) {
            return experts;
        }
        return experts;
    }

    /** Update user active status */
    public boolean setActive(String userID, boolean isActive) {
        String sql = "UPDATE users SET isActive = ? WHERE userID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setBoolean(1, isActive);
            ps.setString(2, userID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    /** Update user role */
    public boolean updateRole(String userID, String newRole) {
        String sql = "UPDATE users SET role = ? WHERE userID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, newRole);
            ps.setString(2, userID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    /** Update expert rating */
    public boolean updateExpertRating(String expertID, double rating) {
        String sql = "UPDATE users SET rating = ? WHERE userID = ? AND role = 'expert'";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setDouble(1, rating);
            ps.setString(2, expertID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    /** Delete user and all cascade data */
    public boolean delete(String userID) {
        String sql = "DELETE FROM users WHERE userID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, userID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    /** Check if email already exists */
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
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

    public boolean isVerified(String userID) {
        String sql = "SELECT isVerified FROM users WHERE userID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("isVerified");
                }
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        String userID = rs.getString("userID");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String password = rs.getString("password");
        int age = rs.getInt("age");
        String gender = rs.getString("gender");
        boolean isActive = rs.getBoolean("isActive");
        LocalDateTime createdAt = rs.getTimestamp("createdAt") == null
            ? LocalDateTime.now()
            : rs.getTimestamp("createdAt").toLocalDateTime();

        User user;
        if ("expert".equalsIgnoreCase(role)) {
            SkincareExpert expert = new SkincareExpert(
                userID,
                name,
                email,
                password,
                age,
                gender,
                rs.getString("specialization"),
                rs.getDouble("consultationFee")
            );
            expert.setRating(rs.getDouble("rating"));
            expert.setVerified(rs.getBoolean("isVerified"));
            user = expert;
        } else if ("admin".equalsIgnoreCase(role)) {
            user = new SystemAdmin(userID, name, email, password, age, gender);
        } else {
            user = new EndUser(userID, name, email, password, age, gender);
        }

        user.setRole(role);
        user.setActive(isActive);
        user.setCreatedAt(createdAt);
        return user;
    }
}
