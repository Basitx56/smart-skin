package repository;

import model.RoutineStep;
import model.SkincareRoutine;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoutineRepository extends BaseRepository {
    public boolean save(SkincareRoutine routine) {
        String sql = "INSERT INTO skincare_routines (routineID, userID, routineType, steps, generatedDate, isExpertValidated, expertID) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            if (routine.getRoutineID() == null || routine.getRoutineID().isEmpty()) {
                routine.setRoutineID(UUID.randomUUID().toString());
            }
            if (routine.getGeneratedDate() == null) {
                routine.setGeneratedDate(LocalDateTime.now());
            }
            ps.setString(1, routine.getRoutineID());
            ps.setString(2, routine.getUserID());
            ps.setString(3, routine.getRoutineType());
            ps.setString(4, serializeSteps(routine.getSteps()));
            ps.setTimestamp(5, Timestamp.valueOf(routine.getGeneratedDate()));
            ps.setBoolean(6, routine.isExpertValidated());
            ps.setString(7, routine.getExpertID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public SkincareRoutine findLatestByUser(String userID) {
        String sql = "SELECT * FROM skincare_routines WHERE userID = ? ORDER BY generatedDate DESC LIMIT 1";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRoutine(rs);
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    public List<SkincareRoutine> findAllByUser(String userID) {
        List<SkincareRoutine> routines = new ArrayList<>();
        String sql = "SELECT * FROM skincare_routines WHERE userID = ? ORDER BY generatedDate DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    routines.add(mapRoutine(rs));
                }
            }
        } catch (SQLException e) {
            return routines;
        }
        return routines;
    }

    public boolean updateValidationStatus(String routineID, boolean validated, String expertID) {
        String sql = "UPDATE skincare_routines SET isExpertValidated = ?, expertID = ? WHERE routineID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setBoolean(1, validated);
            ps.setString(2, expertID);
            ps.setString(3, routineID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteByUser(String userID) {
        String sql = "DELETE FROM skincare_routines WHERE userID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, userID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private SkincareRoutine mapRoutine(ResultSet rs) throws SQLException {
        return new SkincareRoutine(
            rs.getString("routineID"),
            rs.getString("userID"),
            rs.getString("routineType"),
            deserializeSteps(rs.getString("steps")),
            rs.getTimestamp("generatedDate") == null
                ? LocalDateTime.now()
                : rs.getTimestamp("generatedDate").toLocalDateTime(),
            rs.getBoolean("isExpertValidated"),
            rs.getString("expertID")
        );
    }

    private String serializeSteps(List<RoutineStep> steps) {
        if (steps == null || steps.isEmpty()) {
            return "";
        }
        List<String> chunks = new ArrayList<>();
        for (RoutineStep step : steps) {
            String chunk = step.getStepNumber() + "|"
                + safe(step.getPurpose()) + "|"
                + safe(step.getRecommendedIngredient()) + "|"
                + safe(step.getRecommendedProduct()) + "|"
                + safe(step.getExplanation());
            chunks.add(chunk.replace("\n", " "));
        }
        return String.join(";;", chunks);
    }

    private List<RoutineStep> deserializeSteps(String raw) {
        List<RoutineStep> steps = new ArrayList<>();
        if (raw == null || raw.trim().isEmpty()) {
            return steps;
        }
        String[] chunks = raw.split(";;");
        for (String c : chunks) {
            String[] fields = c.split("\\|", -1);
            if (fields.length >= 5) {
                int stepNumber;
                try {
                    stepNumber = Integer.parseInt(fields[0]);
                } catch (NumberFormatException e) {
                    stepNumber = steps.size() + 1;
                }
                steps.add(new RoutineStep(stepNumber, fields[1], fields[2], fields[3], fields[4]));
            }
        }
        return steps;
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("|", "/").replace(";;", ",");
    }
}
