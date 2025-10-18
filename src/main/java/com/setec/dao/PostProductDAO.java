package com.setec.dao;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostProductDAO {
    private String name;
    private Double price;
    private Integer qty;  // Fixed typo: qyt → qty
    private MultipartFile file;
}