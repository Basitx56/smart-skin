package repository;

import model.Notification;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotificationRepository extends BaseRepository {
    public boolean save(Notification notif) {
        String sql = "INSERT INTO notifications (notifID, userID, title, body, isRead, createdAt) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            if (notif.getNotifID() == null || notif.getNotifID().isEmpty()) {
                notif.setNotifID(UUID.randomUUID().toString());
            }
            if (notif.getCreatedAt() == null) {
                notif.setCreatedAt(LocalDateTime.now());
            }
            ps.setString(1, notif.getNotifID());
            ps.setString(2, notif.getUserID());
            ps.setString(3, notif.getTitle());
            ps.setString(4, notif.getBody());
            ps.setBoolean(5, notif.isRead());
            ps.setTimestamp(6, Timestamp.valueOf(notif.getCreatedAt()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Notification> findByUser(String userID) {
        List<Notification> items = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE userID = ? ORDER BY createdAt DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapNotification(rs));
                }
            }
        } catch (SQLException e) {
            return items;
        }
        return items;
    }

    public boolean markAsRead(String notifID) {
        String sql = "UPDATE notifications SET isRead = true WHERE notifID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, notifID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public int countUnread(String userID) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE userID = ? AND isRead = false";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            return 0;
        }
        return 0;
    }

    private Notification mapNotification(ResultSet rs) throws SQLException {
        return new Notification(
            rs.getString("notifID"),
            rs.getString("userID"),
            rs.getString("title"),
            rs.getString("body"),
            rs.getBoolean("isRead"),
            rs.getTimestamp("createdAt") == null
                ? LocalDateTime.now()
                : rs.getTimestamp("createdAt").toLocalDateTime()
        );
    }
}
