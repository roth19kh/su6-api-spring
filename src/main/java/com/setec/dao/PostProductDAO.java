package com.setec.dao;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "Data transfer object for creating a product")
public class PostProductDAO {
    
    private String name;
    
    private Double price;
    
    private Integer qty;
    
    private MultipartFile file;
}