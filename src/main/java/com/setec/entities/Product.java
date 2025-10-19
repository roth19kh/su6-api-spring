package com.setec.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Generated;

@Entity(name = "tbl_product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private double price;
    private int qty;
    @JsonIgnore
    private String imageUrl;

    public String getFullImageUrl() {
        return "https://su6-api-spring.onrender.com" + this.imageUrl;
    }

    public double amount() {
        return this.price * (double)this.qty;
    }

    @Generated
    public Integer getId() {
        return this.id;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public double getPrice() {
        return this.price;
    }

    @Generated
    public int getQty() {
        return this.qty;
    }

    @Generated
    public String getImageUrl() {
        return this.imageUrl;
    }

    @Generated
    public void setId(Integer id) {
        this.id = id;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public void setPrice(double price) {
        this.price = price;
    }

    @Generated
    public void setQty(int qty) {
        this.qty = qty;
    }

    @Generated
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Generated
    public Product(Integer id, String name, double price, int qty, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.qty = qty;
        this.imageUrl = imageUrl;
    }

    @Generated
    public Product() {
    }
}