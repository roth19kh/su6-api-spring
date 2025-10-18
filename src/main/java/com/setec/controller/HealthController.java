package com.setec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import java.sql.Connection;

@RestController
@Tag(name = "Health API", description = "Health check and system monitoring endpoints")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if application is healthy")
    public String health() {
        return "✅ HEALTHY - SU6 API is running smoothly";
    }

    @GetMapping("/db-test")
    @Operation(summary = "Database test", description = "Test PostgreSQL database connection")
    public String dbTest() {
        try (Connection conn = dataSource.getConnection()) {
            return "✅ DATABASE CONNECTED - PostgreSQL is working!";
        } catch (Exception e) {
            return "❌ DATABASE ERROR: " + e.getMessage();
        }
    }

    @GetMapping("/info")
    @Operation(summary = "System info", description = "Get application system information")
    public String info() {
        return String.format("""
            SU6 API Spring - System Information:
            
            📊 Status: Running
            🗄️  Database: Connected
            🌐 Profile: Production
            ⏰ Timestamp: %s
            🔧 Java Version: %s
            """, 
            java.time.LocalDateTime.now(),
            System.getProperty("java.version")
        );
    }
}