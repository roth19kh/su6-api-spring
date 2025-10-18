package com.setec.controller;

import java.util.List;
import java.util.Map;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.setec.config.MyConfig;
import com.setec.dao.PostProductDAO;
import com.setec.dao.PutProductDAO;
import com.setec.entities.Product;
import com.setec.repos.ProductRepo;

@RestController
@RequestMapping("/api/product")
public class MyController {

    private final MyConfig myConfig;

    @Autowired
    private ProductRepo productRepo;

    MyController(MyConfig myConfig) {
        this.myConfig = myConfig;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        var products = productRepo.findAll();
        if (products.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Product is Empty"));
        }
        return ResponseEntity.ok(products);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object postProduct(@ModelAttribute PostProductDAO product) throws Exception {
        String imageBase64 = null;
        
        // Convert image to base64 instead of saving to file system
        if (product.getFile() != null && !product.getFile().isEmpty()) {
            byte[] imageBytes = product.getFile().getBytes();
            imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
        }

        Product pro = new Product();
        pro.setName(product.getName());
        pro.setPrice(product.getPrice());
        pro.setQty(product.getQty());
        pro.setImageData(imageBase64); // Store base64 in database
        pro.setImageUrl("#"); // Placeholder

        productRepo.save(pro);

        return ResponseEntity.status(201).body(pro);
    }

    // Get product by ID
    @GetMapping("{id}")
    public Object getById(@PathVariable("id") Integer id) {
        var pro = productRepo.findById(id);
        if (pro.isPresent()) {
            return pro.get();
        }
        return ResponseEntity.status(404)
                .body(Map.of("Message", "Product id= " + id + " not found"));
    }

    // Get product by name
    @GetMapping("name/{name}")
    public Object getByName(@PathVariable("name") String name) {
        List<Product> pro = productRepo.findByName(name);
        if (pro.size() > 0) {
            return pro;
        }
        return ResponseEntity.status(404)
                .body(Map.of("message", "Product name " + name + " not found"));
    }

    // Delete product by ID
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") Integer id) {
        var p = productRepo.findById(id);
        if (p.isPresent()) {
            // No need to delete file since we're using base64 storage
            productRepo.delete(p.get());
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(Map.of("Message", "Product id = " + id + " has been deleted"));
        }
        return ResponseEntity.status(404)
                .body(Map.of("Message", "Product id = " + id + " not found"));
    }
    
    // Update product
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object putProduct(@ModelAttribute PutProductDAO product) throws Exception {
        Integer id = product.getId();
        var p = productRepo.findById(id);
        if(p.isPresent()) {
            var update = p.get();
            update.setName(product.getName());
            update.setPrice(product.getPrice());
            update.setQty(product.getQyt());

            // Handle image update with base64
            if (product.getFile() != null && !product.getFile().isEmpty()) {
                byte[] imageBytes = product.getFile().getBytes();
                String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
                update.setImageData(imageBase64);
            }
            
            productRepo.save(update);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(Map.of("message","Product id = "+id+" update successful",
                            "product",update));
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("Message","Product id = "+id+" not found"));
    }

    // Get product image as base64
    @GetMapping("{id}/image")
    public ResponseEntity<?> getProductImage(@PathVariable("id") Integer id) {
        var pro = productRepo.findById(id);
        if (pro.isPresent() && pro.get().getImageData() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                        "imageData", pro.get().getImageData(),
                        "message", "Base64 image data"
                    ));
        }
        return ResponseEntity.status(404)
                .body(Map.of("message", "Image not found for product id: " + id));
    }

    // Get product image as actual image file
    @GetMapping(value = "{id}/image-file", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> getProductImageFile(@PathVariable("id") Integer id) {
        var pro = productRepo.findById(id);
        if (pro.isPresent() && pro.get().getImageData() != null) {
            try {
                byte[] imageBytes = Base64.getDecoder().decode(pro.get().getImageData());
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // or detect type
                        .body(imageBytes);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}