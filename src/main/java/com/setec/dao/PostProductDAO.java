package com.setec.dao;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostProductDAO {
    private String name;
    private Double price;
    private Integer qty;
    private MultipartFile file; // Still accept file, but we'll convert to base64
}