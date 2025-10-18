package com.setec.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadDir = "/tmp/myApp/static";
        
        // Ensure directory exists
        try {
            java.nio.file.Files.createDirectories(Paths.get(uploadDir));
            System.out.println("✅ Created directory: " + uploadDir);
        } catch (Exception e) {
            System.out.println("❌ Failed to create directory: " + e.getMessage());
        }
        
        String resourceLocation = "file:" + uploadDir + "/";
        
        System.out.println("🔄 Configuring static resource handler:");
        System.out.println("   - Pattern: /static/**");
        System.out.println("   - Location: " + resourceLocation);
        
        registry.addResourceHandler("/static/**")
                .addResourceLocations(resourceLocation)
                .setCachePeriod(3600);
        
        System.out.println("✅ Static resources configured!");
    }
}