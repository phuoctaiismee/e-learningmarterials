package com.manage.library.utils;

import com.manage.library.config.ApplicationConstant;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class JdbcHelper {

    private static String driver = "org.sqlite.JDBC";
    private static String dburl = "jdbc:sqlite:bin/db/library.db";

    static {
        try {
            Class.forName(driver);
            initializeDatabase();
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void initializeDatabase() {

        File file = new File(ApplicationConstant.PathConfig.APPLICATION_DATABASE);

        if (!file.exists()) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy file cở sở dữ liệu");
            throw new RuntimeException("Database file not found!");
        }

    }

    private static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dburl);
//            System.out.println("Connection to SQLite has been established.");
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return conn;
    }

    private static PreparedStatement getStmt(String sql, Object... args) throws SQLException {
        Connection conn = connect();
        PreparedStatement stmt;
        if (sql.trim().startsWith("{")) {
            stmt = conn.prepareCall(sql);
        } else {
            stmt = conn.prepareStatement(sql);
        }
        for (int i = 0; i < args.length; i++) {
            stmt.setObject(i + 1, args[i]);
        }
        return stmt;
    }

    public static int update(String sql, Object... args) {
        try (PreparedStatement stmt = getStmt(sql, args)) {
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultSet query(String sql, Object... args) {
        try {
            PreparedStatement stmt = getStmt(sql, args);
            return stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object value(String sql, Object... args) {
        try {
            ResultSet rs = query(sql, args);
            if (rs.next()) {
                return rs.getObject(1);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
