package com.setec.controller;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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

    // Helper method for file upload - FIXED FOR PRODUCTION
    private String handleFileUpload(MultipartFile file) throws Exception {
        // Use the current working directory which is writable in Render.com
        String uploadDir = Paths.get("").toAbsolutePath().toString() + "/static";
        File dir = new File(uploadDir);
        
        // Create directory if it doesn't exist
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new Exception("Failed to create upload directory: " + uploadDir);
            }
        }
        
        // Clean filename and generate unique name
        String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID() + fileExtension;
        String filePath = Paths.get(uploadDir, fileName).toString();

        // Save the file
        file.transferTo(new File(filePath));
        
        return "/static/" + fileName;
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
    @Operation(summary = "Create a new product", description = "Create a new product with image upload")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public Object postProduct(
            @Parameter(description = "Product data with image file", required = true)
            @ModelAttribute PostProductDAO product) throws Exception {
        
        String imageUrl = handleFileUpload(product.getFile());

        Product pro = new Product();
        pro.setName(product.getName());
        pro.setPrice(product.getPrice());
        pro.setQty(product.getQty());
        pro.setImageUrl(imageUrl);

        productRepo.save(pro);

        return ResponseEntity.status(201).body(pro);
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
    @Operation(summary = "Delete product by ID", description = "Delete a specific product and its image file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<?> deleteById(
            @Parameter(description = "Product ID to delete", example = "1", required = true)
            @PathVariable("id") Integer id) {
        var p = productRepo.findById(id);
        if (p.isPresent()) {
            // Delete the image file
            String imagePath = p.get().getImageUrl();
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath.startsWith("/") ? imagePath.substring(1) : imagePath);
                if (imageFile.exists()) {
                    imageFile.delete();
                }
            }
            
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
                // Delete old image
                String oldImagePath = update.getImageUrl();
                if (oldImagePath != null && !oldImagePath.isEmpty()) {
                    File oldImageFile = new File(oldImagePath.startsWith("/") ? oldImagePath.substring(1) : oldImagePath);
                    if (oldImageFile.exists()) {
                        oldImageFile.delete();
                    }
                }
                
                // Upload new image
                String newImageUrl = handleFileUpload(product.getFile());
                update.setImageUrl(newImageUrl);
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

    // Add endpoint to check file upload directory
    @GetMapping("/upload-info")
    @Operation(summary = "Upload directory info", description = "Check file upload directory status")
    public ResponseEntity<?> uploadInfo() {
        String uploadDir = Paths.get("").toAbsolutePath().toString() + "/static";
        File dir = new File(uploadDir);
        
        return ResponseEntity.ok(Map.of(
            "uploadDirectory", uploadDir,
            "directoryExists", dir.exists(),
            "directoryWritable", dir.canWrite(),
            "absolutePath", Paths.get("").toAbsolutePath().toString(),
            "freeSpace", String.format("%.2f MB", dir.getFreeSpace() / (1024.0 * 1024.0))
        ));
    }
}