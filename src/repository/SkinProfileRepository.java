package repository;

import model.SkinProfile;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SkinProfileRepository extends BaseRepository {
    public boolean save(SkinProfile profile) {
        String sql = "INSERT INTO skin_profiles (profileID, userID, skinType, skinConcerns, knownAllergies, currentProducts, diet, sleepHours, stressLevel, sunExposure, completenessPercentage, updatedAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            if (profile.getProfileID() == null || profile.getProfileID().isEmpty()) {
                profile.setProfileID(UUID.randomUUID().toString());
            }
            if (profile.getUpdatedAt() == null) {
                profile.setUpdatedAt(LocalDateTime.now());
            }
            ps.setString(1, profile.getProfileID());
            ps.setString(2, profile.getUserID());
            ps.setString(3, profile.getSkinType());
            ps.setString(4, joinList(profile.getSkinConcerns()));
            ps.setString(5, joinList(profile.getKnownAllergies()));
            ps.setString(6, joinList(profile.getCurrentProducts()));
            ps.setString(7, profile.getDiet());
            ps.setInt(8, profile.getSleepHours());
            ps.setString(9, profile.getStressLevel());
            ps.setString(10, profile.getSunExposure());
            ps.setInt(11, profile.getCompletenessPercentage());
            ps.setTimestamp(12, Timestamp.valueOf(profile.getUpdatedAt()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public SkinProfile findByUserID(String userID) {
        String sql = "SELECT * FROM skin_profiles WHERE userID = ? ORDER BY updatedAt DESC LIMIT 1";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapProfile(rs);
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    public boolean update(SkinProfile profile) {
        String sql = "UPDATE skin_profiles SET skinType=?, skinConcerns=?, knownAllergies=?, currentProducts=?, diet=?, sleepHours=?, stressLevel=?, sunExposure=?, completenessPercentage=?, updatedAt=? WHERE profileID=?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            if (profile.getUpdatedAt() == null) {
                profile.setUpdatedAt(LocalDateTime.now());
            }
            ps.setString(1, profile.getSkinType());
            ps.setString(2, joinList(profile.getSkinConcerns()));
            ps.setString(3, joinList(profile.getKnownAllergies()));
            ps.setString(4, joinList(profile.getCurrentProducts()));
            ps.setString(5, profile.getDiet());
            ps.setInt(6, profile.getSleepHours());
            ps.setString(7, profile.getStressLevel());
            ps.setString(8, profile.getSunExposure());
            ps.setInt(9, profile.getCompletenessPercentage());
            ps.setTimestamp(10, Timestamp.valueOf(profile.getUpdatedAt()));
            ps.setString(11, profile.getProfileID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean delete(String userID) {
        String sql = "DELETE FROM skin_profiles WHERE userID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, userID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private SkinProfile mapProfile(ResultSet rs) throws SQLException {
        return new SkinProfile(
            rs.getString("profileID"),
            rs.getString("userID"),
            rs.getString("skinType"),
            splitList(rs.getString("skinConcerns")),
            splitList(rs.getString("knownAllergies")),
            splitList(rs.getString("currentProducts")),
            rs.getString("diet"),
            rs.getInt("sleepHours"),
            rs.getString("stressLevel"),
            rs.getString("sunExposure"),
            rs.getInt("completenessPercentage"),
            rs.getTimestamp("updatedAt") == null
                ? LocalDateTime.now()
                : rs.getTimestamp("updatedAt").toLocalDateTime()
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
