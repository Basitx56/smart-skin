package repository;

import model.ProgressEntry;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProgressRepository extends BaseRepository {
    public boolean save(ProgressEntry entry) {
        String sql = "INSERT INTO progress_entries (entryID, userID, entryDate, acneLevel, dryness, pigmentation, irritation, photoPath, notes, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            if (entry.getEntryID() == null || entry.getEntryID().isEmpty()) {
                entry.setEntryID(UUID.randomUUID().toString());
            }
            if (entry.getCreatedAt() == null) {
                entry.setCreatedAt(LocalDateTime.now());
            }
            ps.setString(1, entry.getEntryID());
            ps.setString(2, entry.getUserID());
            ps.setDate(3, Date.valueOf(entry.getEntryDate()));
            ps.setInt(4, entry.getAcneLevel());
            ps.setInt(5, entry.getDryness());
            ps.setInt(6, entry.getPigmentation());
            ps.setInt(7, entry.getIrritation());
            ps.setString(8, entry.getPhotoPath());
            ps.setString(9, entry.getNotes());
            ps.setTimestamp(10, Timestamp.valueOf(entry.getCreatedAt()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<ProgressEntry> findByUser(String userID) {
        List<ProgressEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM progress_entries WHERE userID = ? ORDER BY entryDate ASC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapProgressEntry(rs));
                }
            }
        } catch (SQLException e) {
            return entries;
        }
        return entries;
    }

    public boolean delete(String entryID) {
        String sql = "DELETE FROM progress_entries WHERE entryID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, entryID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteByUser(String userID) {
        String sql = "DELETE FROM progress_entries WHERE userID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, userID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private ProgressEntry mapProgressEntry(ResultSet rs) throws SQLException {
        return new ProgressEntry(
            rs.getString("entryID"),
            rs.getString("userID"),
            rs.getDate("entryDate").toLocalDate(),
            rs.getInt("acneLevel"),
            rs.getInt("dryness"),
            rs.getInt("pigmentation"),
            rs.getInt("irritation"),
            rs.getString("photoPath"),
            rs.getString("notes"),
            rs.getTimestamp("createdAt") == null
                ? LocalDateTime.now()
                : rs.getTimestamp("createdAt").toLocalDateTime()
        );
    }
}
