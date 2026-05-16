package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Singleton database connection manager.
 * GOF Pattern: Singleton - ensures only one
 * DB connection instance exists.
 * Architecture: Data Access Layer
 */
public class DBConnection {
    private static DBConnection instance;
    private Connection connection;

    private DBConnection() {
        try {
            Properties props = new Properties();
            props.load(getClass().getResourceAsStream(
                "/config.properties"));
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String pass = props.getProperty("db.password");
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(
                url, user, pass);
        } catch (Exception e) {
            throw new RuntimeException(
                "Database connection failed: "
                + e.getMessage());
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
