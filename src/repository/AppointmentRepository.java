package repository;

import model.Appointment;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AppointmentRepository extends BaseRepository {
    public boolean save(Appointment appointment) {
        String sql = "INSERT INTO appointments (appointmentID, userID, expertID, appointmentDate, timeSlot, type, fee, status, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            if (appointment.getAppointmentID() == null || appointment.getAppointmentID().isEmpty()) {
                appointment.setAppointmentID(UUID.randomUUID().toString());
            }
            if (appointment.getCreatedAt() == null) {
                appointment.setCreatedAt(LocalDateTime.now());
            }
            ps.setString(1, appointment.getAppointmentID());
            ps.setString(2, appointment.getUserID());
            ps.setString(3, appointment.getExpertID());
            ps.setDate(4, Date.valueOf(appointment.getAppointmentDate()));
            ps.setString(5, appointment.getTimeSlot());
            ps.setString(6, appointment.getType());
            ps.setDouble(7, appointment.getFee());
            ps.setString(8, appointment.getStatus());
            ps.setTimestamp(9, Timestamp.valueOf(appointment.getCreatedAt()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Appointment> findByUser(String userID) {
        List<Appointment> items = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE userID = ? ORDER BY appointmentDate DESC, timeSlot DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapAppointment(rs));
                }
            }
        } catch (SQLException e) {
            return items;
        }
        return items;
    }

    public List<Appointment> findByExpert(String expertID) {
        List<Appointment> items = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE expertID = ? ORDER BY appointmentDate DESC, timeSlot DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, expertID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapAppointment(rs));
                }
            }
        } catch (SQLException e) {
            return items;
        }
        return items;
    }

    public Appointment findByID(String appointmentID) {
        String sql = "SELECT * FROM appointments WHERE appointmentID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, appointmentID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAppointment(rs);
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    public boolean updateStatus(String appointmentID, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE appointmentID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, appointmentID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    /** Get expert availability slots for a date */
    public List<String> getAvailableSlots(String expertID, LocalDate date) {
        List<String> slots = new ArrayList<>();
        String sql = "SELECT timeSlot FROM expert_availability WHERE expertID = ? AND availableDate = ? AND status = 'available' ORDER BY timeSlot ASC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, expertID);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    slots.add(rs.getString("timeSlot"));
                }
            }
        } catch (SQLException e) {
            return slots;
        }
        return slots;
    }

    /** Mark slot as booked */
    public boolean bookSlot(String expertID, LocalDate date, String timeSlot) {
        String sql = "UPDATE expert_availability SET status = 'booked' WHERE expertID = ? AND availableDate = ? AND timeSlot = ? AND status = 'available'";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, expertID);
            ps.setDate(2, Date.valueOf(date));
            ps.setString(3, timeSlot);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    /** Check if slot is available */
    public boolean isSlotAvailable(String expertID, LocalDate date, String timeSlot) {
        String sql = "SELECT COUNT(*) FROM expert_availability WHERE expertID = ? AND availableDate = ? AND timeSlot = ? AND status = 'available'";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, expertID);
            ps.setDate(2, Date.valueOf(date));
            ps.setString(3, timeSlot);
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

    public boolean deleteByUser(String userID) {
        String sql = "DELETE FROM appointments WHERE userID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, userID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private Appointment mapAppointment(ResultSet rs) throws SQLException {
        return new Appointment(
            rs.getString("appointmentID"),
            rs.getString("userID"),
            rs.getString("expertID"),
            rs.getDate("appointmentDate").toLocalDate(),
            rs.getString("timeSlot"),
            rs.getString("type"),
            rs.getDouble("fee"),
            rs.getString("status"),
            rs.getTimestamp("createdAt") == null
                ? LocalDateTime.now()
                : rs.getTimestamp("createdAt").toLocalDateTime()
        );
    }
}
