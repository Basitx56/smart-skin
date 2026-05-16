package repository;

import model.AuditLog;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuditLogRepository extends BaseRepository {
    /** Append-only: no update or delete methods */
    public boolean save(AuditLog log) {
        String sql = "INSERT INTO audit_logs (logID, actorID, action, targetID, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            if (log.getLogID() == null || log.getLogID().isEmpty()) {
                log.setLogID(UUID.randomUUID().toString());
            }
            if (log.getTimestamp() == null) {
                log.setTimestamp(LocalDateTime.now());
            }
            ps.setString(1, log.getLogID());
            ps.setString(2, log.getActorID());
            ps.setString(3, log.getAction());
            ps.setString(4, log.getTargetID());
            ps.setTimestamp(5, Timestamp.valueOf(log.getTimestamp()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<AuditLog> findAll() {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs ORDER BY timestamp DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                logs.add(mapLog(rs));
            }
        } catch (SQLException e) {
            return logs;
        }
        return logs;
    }

    public List<AuditLog> findByActor(String actorID) {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs WHERE actorID = ? ORDER BY timestamp DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, actorID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapLog(rs));
                }
            }
        } catch (SQLException e) {
            return logs;
        }
        return logs;
    }

    private AuditLog mapLog(ResultSet rs) throws SQLException {
        return new AuditLog(
            rs.getString("logID"),
            rs.getString("actorID"),
            rs.getString("action"),
            rs.getString("targetID"),
            rs.getTimestamp("timestamp") == null
                ? LocalDateTime.now()
                : rs.getTimestamp("timestamp").toLocalDateTime()
        );
    }
}
