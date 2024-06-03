package com.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.SQLException;

public class DBClientManager {
    private static final Logger log = LogManager.getLogger(DBClientManager.class);
    private static HikariDataSource dataSource;

    public static void setConnection(String dbUrl, String user, String password) {
        if (dataSource == null) {
            try {
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(dbUrl);
                config.setUsername(user);
                config.setPassword(password);
                config.setDriverClassName("com.mysql.cj.jdbc.Driver");
                config.setMaximumPoolSize(10);
                dataSource = new HikariDataSource(config);
                log.info("Database connection pool established successfully.");
            } catch (Exception e) {
                log.error("Error establishing database connection pool: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Connection pool not yet established!");
        }
        return dataSource.getConnection();
    }

    public static void closeConnectionPool() {
        if (dataSource != null) {
            dataSource.close();
            log.info("Database connection pool closed.");
        }
    }
}