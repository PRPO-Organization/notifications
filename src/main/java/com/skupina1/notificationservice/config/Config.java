package com.skupina1.notificationservice.config;

public class Config {
    public static String getDBUrl() {
        return System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/notificationdb");
    }

    public static String getDBUser() {
        return System.getenv().getOrDefault("DB_USER", "admin");
    }

    public static String getDBPassword() {
        return System.getenv().getOrDefault("DB_PASSWORD", "pass");
    }
}
