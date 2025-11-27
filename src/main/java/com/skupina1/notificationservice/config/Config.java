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

    public static String getJwtSecret(){
        return System.getenv().getOrDefault("JWT_SECRET_TOKEN", "53V3NPR41535T0L0RDJUR1CL0NGM4YHER31GNF0RH15RUL3W45PR0M153D");
    }
}
