package com.university.db;

import java.sql.*;

public class DatabaseConnector {

    private static final String URL  = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root";
    private static final String PASS = "ou@M&AIMA123"; // change to your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Run this main() first to test your connection
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("✓ Connected to MySQL successfully!");
            System.out.println("  Database: " + conn.getCatalog());
        } catch (SQLException e) {
            System.out.println("✗ Connection failed: " + e.getMessage());
        }
    }
}