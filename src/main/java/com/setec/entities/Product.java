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
    
    @Column(columnDefinition = "TEXT")
    private String imageData;
    
    // Remove the @Transient annotation and add proper getter
    public String getFullImageUrl() {
        // If we have imageData, return a URL to access it
    	if (this.imageData != null && !this.imageData.isEmpty()) {
            return "/api/product/" + this.id + "/image-file";
        }
        if (imageUrl != null && !imageUrl.equals("#")) {
            return imageUrl;
        }
        return null;
    }
    
    public double amount() {
        return price * qty;
    }
    
}