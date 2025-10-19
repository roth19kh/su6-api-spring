package com.setec.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadDir = "/tmp/myApp/static";
        
        registry.addResourceHandler("/static/**")
                .addResourceLocations("file:" + uploadDir + "/");
        
        System.out.println("✅ Static resources configured for: " + uploadDir);
    }
}