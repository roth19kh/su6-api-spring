package com.setec.entities;

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
    
    private String imageUrl;
    
    @Column(columnDefinition = "TEXT") // Add this field for base64 storage
    private String imageData;
    
    public String getFullImageUrl() {
        if (this.imageData != null && !this.imageData.isEmpty()) {
            return "/api/product/" + this.id + "/image";
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