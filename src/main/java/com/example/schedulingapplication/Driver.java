package com.example.schedulingapplication;
import java.sql.*;

public interface Driver {
    /**
     * Gets a connection to the database
     *
     * @return Database Connection
     */
    static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(System.getenv("DB_URL"), System.getenv("DB_USER"), System.getenv("MYSQL_PASSWORD"));
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
