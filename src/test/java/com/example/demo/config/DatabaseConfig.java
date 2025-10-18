package com.example.demo.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    @Profile("production")
    public DataSource dataSource() {
        String dbUrl = System.getenv("DATABASE_URL");
        
        // If DATABASE_URL is provided as full connection string
        if (dbUrl != null && dbUrl.startsWith("postgresql://")) {
            return DataSourceBuilder.create()
                    .url("jdbc:" + dbUrl)
                    .username(System.getenv("DATABASE_USERNAME"))
                    .password(System.getenv("DATABASE_PASSWORD"))
                    .build();
        }
        
        // Fallback to individual properties
        return DataSourceBuilder.create()
                .url(System.getenv("DATABASE_URL") != null ? 
                     System.getenv("DATABASE_URL") : 
                     "jdbc:postgresql://dpg-d3fn6bp5pdvs73be510g-a.oregon-postgres.render.com:5432/roth")
                .username(System.getenv("DATABASE_USERNAME") != null ? 
                         System.getenv("DATABASE_USERNAME") : "roth_user")
                .password(System.getenv("DATABASE_PASSWORD") != null ? 
                         System.getenv("DATABASE_PASSWORD") : "ArTLhsMDelJWYbabV93WhrQI60g5rZUL")
                .build();
    }
}