package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Base repository with shared JDBC utilities.
 * Architecture: Data Access Layer
 */
public abstract class BaseRepository {
    protected Connection getConnection() {
        return DBConnection.getInstance().getConnection();
    }

    protected void closeResources(PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            System.err.println(
                "Error closing resources: "
                    + e.getMessage());
        }
    }
}
