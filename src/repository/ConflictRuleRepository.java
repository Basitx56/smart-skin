package repository;

import model.IngredientConflictRule;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ConflictRuleRepository extends BaseRepository {
    public List<IngredientConflictRule> findAll() {
        List<IngredientConflictRule> rules = new ArrayList<>();
        String sql = "SELECT * FROM conflict_rules ORDER BY lastUpdated DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rules.add(mapRule(rs));
            }
        } catch (SQLException e) {
            return rules;
        }
        return rules;
    }

    public boolean save(IngredientConflictRule rule) {
        String sql = "INSERT INTO conflict_rules (ruleID, interactingIngredients, safetyLevel, adviceText, expertReviewed, lastUpdated) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            if (rule.getRuleID() == null || rule.getRuleID().isEmpty()) {
                rule.setRuleID(UUID.randomUUID().toString());
            }
            if (rule.getLastUpdated() == null) {
                rule.setLastUpdated(LocalDateTime.now());
            }
            ps.setString(1, rule.getRuleID());
            ps.setString(2, joinList(rule.getInteractingIngredients()));
            ps.setString(3, rule.getSafetyLevel());
            ps.setString(4, rule.getAdviceText());
            ps.setBoolean(5, rule.isExpertReviewed());
            ps.setTimestamp(6, Timestamp.valueOf(rule.getLastUpdated()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean update(IngredientConflictRule rule) {
        String sql = "UPDATE conflict_rules SET interactingIngredients=?, safetyLevel=?, adviceText=?, expertReviewed=?, lastUpdated=? WHERE ruleID=?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            if (rule.getLastUpdated() == null) {
                rule.setLastUpdated(LocalDateTime.now());
            }
            ps.setString(1, joinList(rule.getInteractingIngredients()));
            ps.setString(2, rule.getSafetyLevel());
            ps.setString(3, rule.getAdviceText());
            ps.setBoolean(4, rule.isExpertReviewed());
            ps.setTimestamp(5, Timestamp.valueOf(rule.getLastUpdated()));
            ps.setString(6, rule.getRuleID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private IngredientConflictRule mapRule(ResultSet rs) throws SQLException {
        return new IngredientConflictRule(
            rs.getString("ruleID"),
            splitList(rs.getString("interactingIngredients")),
            rs.getString("safetyLevel"),
            rs.getString("adviceText"),
            rs.getBoolean("expertReviewed"),
            rs.getTimestamp("lastUpdated") == null
                ? LocalDateTime.now()
                : rs.getTimestamp("lastUpdated").toLocalDateTime()
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
