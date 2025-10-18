package com.setec.entities;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "tbl_product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private double price;
    private int qty;
    
    @JsonIgnore
    private String imageUrl;
    
    @Column(columnDefinition = "TEXT") // Store base64 image data
    private String imageData;
    
    @Transient // This field won't be stored in database
    private String imageBase64;
    
    public String getFullImageUrl() {
        if (imageUrl != null && !imageUrl.equals("#")) {
            return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + imageUrl;
        }
        return null;
    }
    
    // Getter that returns base64 data for API responses
    public String getImageBase64() {
        return this.imageData;
    }
    
    // Setter for base64 data
    public void setImageBase64(String imageBase64) {
        this.imageData = imageBase64;
    }
    
    public double amount() {
        return price * qty;
    }
}