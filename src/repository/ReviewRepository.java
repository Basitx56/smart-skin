package repository;

import model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReviewRepository extends BaseRepository {
    public boolean save(Review review) {
        String sql = "INSERT INTO reviews (reviewID, userID, expertID, appointmentID, rating, comment, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            if (review.getReviewID() == null || review.getReviewID().isEmpty()) {
                review.setReviewID(UUID.randomUUID().toString());
            }
            if (review.getCreatedAt() == null) {
                review.setCreatedAt(LocalDateTime.now());
            }
            ps.setString(1, review.getReviewID());
            ps.setString(2, review.getUserID());
            ps.setString(3, review.getExpertID());
            ps.setString(4, review.getAppointmentID());
            ps.setInt(5, review.getRating());
            ps.setString(6, review.getComment());
            ps.setTimestamp(7, Timestamp.valueOf(review.getCreatedAt()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Review> findByExpert(String expertID) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE expertID = ? ORDER BY createdAt DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, expertID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapReview(rs));
                }
            }
        } catch (SQLException e) {
            return reviews;
        }
        return reviews;
    }

    public Review findByAppointment(String appointmentID) {
        String sql = "SELECT * FROM reviews WHERE appointmentID = ? LIMIT 1";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, appointmentID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapReview(rs);
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    public boolean existsForAppointment(String appointmentID) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE appointmentID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, appointmentID);
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

    private Review mapReview(ResultSet rs) throws SQLException {
        return new Review(
            rs.getString("reviewID"),
            rs.getString("userID"),
            rs.getString("expertID"),
            rs.getString("appointmentID"),
            rs.getInt("rating"),
            rs.getString("comment"),
            rs.getTimestamp("createdAt") == null
                ? LocalDateTime.now()
                : rs.getTimestamp("createdAt").toLocalDateTime()
        );
    }
}
