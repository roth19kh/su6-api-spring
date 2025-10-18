package com.setec.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Match the same directory as FileStorageService - /tmp/myApp/static
        String uploadDir = "/tmp/myApp/static";
        
        registry.addResourceHandler("/static/**")
                .addResourceLocations("file:" + uploadDir + "/")
                .setCachePeriod(3600);
        
        System.out.println("🔄 Static resources configured for: " + uploadDir);
    }
}
