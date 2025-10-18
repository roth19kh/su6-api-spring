package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Spring Boot API");
        
        // Check database connectivity
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            health.put("database", "UP");
        } catch (Exception e) {
            health.put("database", "DOWN");
            health.put("databaseError", e.getMessage());
        }
        
        return health;
    }

    @GetMapping("/")
    public String home() {
        return """
            <html>
                <body>
                    <h1>Spring Boot API</h1>
                    <p>API is running successfully!</p>
                    <ul>
                        <li><a href="/health">Health Check</a></li>
                        <li><a href="/api/users">Users API</a></li>
                    </ul>
                </body>
            </html>
            """;
    }
}