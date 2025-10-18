// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.setec.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
   public WebConfig() {
   }

   public void addResourceHandlers(ResourceHandlerRegistry registry) {
      registry.addResourceHandler(new String[]{"/static/**"}).addResourceLocations(new String[]{"file:/tmp/myApp/static/"});
   }
}
