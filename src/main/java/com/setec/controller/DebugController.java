package com.setec.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DebugController {

    @Value("${DATABASE_URL:Not Found}")
    private String databaseUrl;

    @Value("${DATABASE_USERNAME:Not Found}")
    private String databaseUsername;

    @Value("${DATABASE_PASSWORD:Not Found}")
    private String databasePassword;

    @Value("${spring.datasource.url:Not Found}")
    private String springDatasourceUrl;

    @GetMapping("/debug")
    public Map<String, String> debug() {
        return Map.of(
            "DATABASE_URL", databaseUrl,
            "DATABASE_USERNAME", databaseUsername,
            "DATABASE_PASSWORD", databasePassword.substring(0, Math.min(5, databasePassword.length())) + "***",
            "SPRING_DATASOURCE_URL", springDatasourceUrl,
            "ALL_ENV_DATABASE_URL", System.getenv("DATABASE_URL") != null ? "Set" : "Not Set",
            "ALL_ENV_DATABASE_USERNAME", System.getenv("DATABASE_USERNAME") != null ? "Set" : "Not Set",
            "ALL_ENV_DATABASE_PASSWORD", System.getenv("DATABASE_PASSWORD") != null ? "Set" : "Not Set"
        );
    }
}