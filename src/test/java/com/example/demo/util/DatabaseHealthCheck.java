package com.example.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class DatabaseHealthCheck implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseHealthCheck.class);
    
    private final DataSource dataSource;

    public DatabaseHealthCheck(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            logger.info("✅ Database connection successful to: {}", 
                connection.getMetaData().getURL());
            logger.info("✅ Database name: {}", 
                connection.getMetaData().getDatabaseProductName());
            logger.info("✅ Database version: {}", 
                connection.getMetaData().getDatabaseProductVersion());
        } catch (Exception e) {
            logger.error("❌ Database connection failed: {}", e.getMessage());
            throw e;
        }
    }
}