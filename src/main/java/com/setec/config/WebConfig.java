package com.setec.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;
import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @PostConstruct // Fixed: using jakarta.annotation instead of javax.annotation
    public void init() {
        System.out.println("📍 WebConfig initialized - static file serving enabled");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadDir = "/tmp/myApp/static";
        
        System.out.println("📁 Configuring static resources from: " + uploadDir);
        
        File directory = new File(uploadDir);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            System.out.println("📁 Directory exists with " + (files != null ? files.length : 0) + " files");
        } else {
            System.out.println("❌ Directory does not exist: " + uploadDir);
        }
        
        registry.addResourceHandler("/static/**")
                .addResourceLocations("file:" + uploadDir + "/")
                .setCachePeriod(0);
        
        System.out.println("✅ Static resources configured for: /static/** -> " + uploadDir);
    }
}