package com.setec.dao;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PutProductDAO {
    private Integer id;
    private String name;
    private Double price;
    private Integer qyt; // Make sure this matches what you're using
    private MultipartFile file;
}