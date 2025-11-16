package com.attic.scheduler.db;


import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private final Properties props = new Properties();

    private DatabaseConnection() {
        try (InputStream in = DatabaseConnection.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (in == null) {
                throw new IOException("db.properties not found on classpath");
            }

            props.load(in);

            // Optional (modern JDBC auto-registers drivers)
            Class.forName("org.postgresql.Driver");

        } catch (Exception e) {
            throw new RuntimeException("Failed to load DB config", e);
        }
    }
    
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            props.getProperty("url"),
            props.getProperty("user"),
            props.getProperty("password")
            );
    }
}