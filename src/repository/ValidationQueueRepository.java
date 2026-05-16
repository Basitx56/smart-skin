package repository;

import model.ValidationQueueItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ValidationQueueRepository extends BaseRepository {
    public boolean save(ValidationQueueItem item) {
        String sql = "INSERT INTO validation_queue (itemID, routineID, userID, status, submittedAt, reviewedBy, reviewedAt, rejectionReason) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            if (item.getItemID() == null || item.getItemID().isEmpty()) {
                item.setItemID(UUID.randomUUID().toString());
            }
            if (item.getSubmittedAt() == null) {
                item.setSubmittedAt(LocalDateTime.now());
            }
            ps.setString(1, item.getItemID());
            ps.setString(2, item.getRoutineID());
            ps.setString(3, item.getUserID());
            ps.setString(4, item.getStatus());
            ps.setTimestamp(5, Timestamp.valueOf(item.getSubmittedAt()));
            ps.setString(6, item.getReviewedBy());
            if (item.getReviewedAt() != null) {
                ps.setTimestamp(7, Timestamp.valueOf(item.getReviewedAt()));
            } else {
                ps.setNull(7, java.sql.Types.TIMESTAMP);
            }
            ps.setString(8, item.getRejectionReason());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<ValidationQueueItem> findPending() {
        List<ValidationQueueItem> items = new ArrayList<>();
        String sql = "SELECT * FROM validation_queue WHERE status = 'pending' ORDER BY submittedAt ASC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(mapItem(rs));
            }
        } catch (SQLException e) {
            return items;
        }
        return items;
    }

    public ValidationQueueItem findByID(String itemID) {
        String sql = "SELECT * FROM validation_queue WHERE itemID = ? LIMIT 1";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, itemID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapItem(rs);
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    public boolean updateStatus(String itemID, String status, String reviewedBy, String rejectionReason) {
        String sql = "UPDATE validation_queue SET status=?, reviewedBy=?, reviewedAt=?, rejectionReason=? WHERE itemID=?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, reviewedBy);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(4, rejectionReason);
            ps.setString(5, itemID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private ValidationQueueItem mapItem(ResultSet rs) throws SQLException {
        Timestamp reviewedAt = rs.getTimestamp("reviewedAt");
        return new ValidationQueueItem(
            rs.getString("itemID"),
            rs.getString("routineID"),
            rs.getString("userID"),
            rs.getString("status"),
            rs.getTimestamp("submittedAt") == null
                ? LocalDateTime.now()
                : rs.getTimestamp("submittedAt").toLocalDateTime(),
            rs.getString("reviewedBy"),
            reviewedAt == null ? null : reviewedAt.toLocalDateTime(),
            rs.getString("rejectionReason")
        );
    }
}
