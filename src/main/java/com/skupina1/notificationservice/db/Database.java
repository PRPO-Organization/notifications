package com.skupina1.notificationservice.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.skupina1.notificationservice.config.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Database {
    private static final HikariDataSource ds;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Config.getDBUrl());
        config.setUsername(Config.getDBUser());
        config.setPassword(Config.getDBPassword());
        config.setDriverClassName("org.postgresql.Driver");
        ds = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}