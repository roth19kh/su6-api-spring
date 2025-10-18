package com.setec.controller;

import java.util.List;
import java.util.Map;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.setec.config.MyConfig;
import com.setec.dao.PostProductDAO;
import com.setec.dao.PutProductDAO;
import com.setec.entities.Product;
import com.setec.repos.ProductRepo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/product")
@Tag(name = "Product API", description = "CRUD operations for product management")
public class MyController {

    private final MyConfig myConfig;

    @Autowired
    private ProductRepo productRepo;

    MyController(MyConfig myConfig) {
        this.myConfig = myConfig;
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve a list of all products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No products found")
    })
    public ResponseEntity<?> getAll() {
        var products = productRepo.findAll();
        if (products.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Product is Empty"));
        }
        return ResponseEntity.ok(products);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new product", description = "Create a new product with image upload as base64")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public Object postProduct(
            @Parameter(description = "Product data with image file", required = true)
            @ModelAttribute PostProductDAO product) throws Exception {
        
        String imageBase64 = null;
        if (product.getFile() != null && !product.getFile().isEmpty()) {
            // Convert image to base64
            byte[] imageBytes = product.getFile().getBytes();
            imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
        }

        Product pro = new Product();
        pro.setName(product.getName());
        pro.setPrice(product.getPrice());
        pro.setQty(product.getQty());
        pro.setImageData(imageBase64);
        pro.setImageUrl("#"); // Placeholder since we're using base64

        productRepo.save(pro);

        return ResponseEntity.status(201).body(Map.of(
            "message", "Product created successfully with base64 image storage",
            "product", pro
        ));
    }

    @GetMapping("{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public Object getById(
            @Parameter(description = "Product ID", example = "1", required = true)
            @PathVariable("id") Integer id) {
        var pro = productRepo.findById(id);
        if (pro.isPresent()) {
            return pro.get();
        }
        return ResponseEntity.status(404)
                .body(Map.of("Message", "Product id= " + id + " not found"));
    }

    @GetMapping("name/{name}")
    @Operation(summary = "Get products by name", description = "Search products by name (partial match)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products found"),
        @ApiResponse(responseCode = "404", description = "No products found with that name")
    })
    public Object getByName(
            @Parameter(description = "Product name to search for", example = "laptop", required = true)
            @PathVariable("name") String name) {
        List<Product> pro = productRepo.findByName(name);
        if (pro.size() > 0) {
            return pro;
        }
        return ResponseEntity.status(404)
                .body(Map.of("message", "Product name " + name + " not found"));
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete product by ID", description = "Delete a specific product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<?> deleteById(
            @Parameter(description = "Product ID to delete", example = "1", required = true)
            @PathVariable("id") Integer id) {
        var p = productRepo.findById(id);
        if (p.isPresent()) {
            productRepo.delete(p.get());
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(Map.of("Message", "Product id = " + id + " has been deleted"));
        }
        return ResponseEntity.status(404)
                .body(Map.of("Message", "Product id = " + id + " not found"));
    }
    
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update product", description = "Update an existing product with optional image change")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Product updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public Object putProduct(
            @Parameter(description = "Product data for update", required = true)
            @ModelAttribute PutProductDAO product) throws Exception {
        Integer id = product.getId();
        var p = productRepo.findById(id);
        if (p.isPresent()) {
            var update = p.get();
            update.setName(product.getName());
            update.setPrice(product.getPrice());
            update.setQty(product.getQyt()); 

            if (product.getFile() != null && !product.getFile().isEmpty()) {
                // Convert new image to base64
                byte[] imageBytes = product.getFile().getBytes();
                String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
                update.setImageData(imageBase64);
            }

            productRepo.save(update);

            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(Map.of(
                            "message", "Product id = " + id + " update successful",
                            "product", update
                    ));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Product id = " + id + " not found"));
    }

    // Get product image as base64
    @GetMapping("{id}/image")
    @Operation(summary = "Get product image", description = "Get product image as base64 data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image found"),
        @ApiResponse(responseCode = "404", description = "Image not found")
    })
    public ResponseEntity<?> getProductImage(
            @Parameter(description = "Product ID", example = "1", required = true)
            @PathVariable("id") Integer id) {
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

    // Create product without image (for testing)
    @PostMapping("/no-image")
    @Operation(summary = "Create product without image", description = "Create a new product without image upload")
    public ResponseEntity<?> createProductWithoutImage(@RequestBody Map<String, Object> productData) {
        Product pro = new Product();
        pro.setName((String) productData.get("name"));
        pro.setPrice(Double.parseDouble(productData.get("price").toString()));
        pro.setQty(Integer.parseInt(productData.get("qty").toString()));
        pro.setImageUrl("#");
        pro.setImageData(null);

        productRepo.save(pro);

        return ResponseEntity.status(201).body(Map.of(
            "message", "Product created successfully without image",
            "product", pro
        ));
    }
}