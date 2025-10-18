package com.setec.controller;

import com.setec.dao.FileStorageService;
import com.setec.dao.PostProductDAO;
import com.setec.dao.PutProductDAO;
import com.setec.entities.Product;
import com.setec.repos.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/product")
public class MyController {
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public Object getAll() {
        List<Product> products = this.productRepo.findAll();
        return products.size() == 0 ? ResponseEntity.status(404).body(Map.of("message", "product is empty")) : products;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public Object postProduct(@ModelAttribute PostProductDAO product) throws Exception {
        String fileName = this.fileStorageService.storeFile(product.getFile());
        Product pro = new Product();
        pro.setName(product.getName());
        pro.setPrice(product.getPrice());
        pro.setQty(product.getQty());
        pro.setImageUrl("/static/" + fileName);
        this.productRepo.save(pro);
        return ResponseEntity.status(201).body(pro);
    }

    @GetMapping({"{id}", "id/{id}"})
    public Object getById(@PathVariable("id") Integer id) {
        Optional<Product> pro = this.productRepo.findById(id);
        return pro.isPresent() ? pro.get() : ResponseEntity.status(404).body(Map.of("message", "Product id = " + String.valueOf(id) + " not found"));
    }

    @GetMapping({"name/{name}"})
    public Object getByName(@PathVariable("name") String name) {
        List<Product> pro = this.productRepo.findByName(name);
        return pro.size() > 0 ? pro : ResponseEntity.status(404).body(Map.of("message", "Product name = " + name + " not found"));
    }

    @DeleteMapping({"{id}", "id/{id}"})
    public Object deleteById(@PathVariable("id") Integer id) {
        Optional<Product> p = this.productRepo.findById(id);
        if (p.isPresent()) {
            this.fileStorageService.deleteFile(((Product)p.get()).getImageUrl());
            this.productRepo.delete((Product)p.get());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("message", "Product id = " + String.valueOf(id) + " has been deleted"));
        } else {
            return ResponseEntity.status(404).body(Map.of("message", "Product id = " + String.valueOf(id) + " not found"));
        }
    }

    @PutMapping(consumes = {"multipart/form-data"})
    public Object putProduct(@ModelAttribute PutProductDAO product) throws Exception {
        Integer id = product.getId();
        Optional<Product> p = this.productRepo.findById(id);
        if (p.isPresent()) {
            Product update = (Product)p.get();
            update.setName(product.getName());
            update.setPrice(product.getPrice());
            update.setQty(product.getQyt());
            if (product.getFile() != null) {
                this.fileStorageService.deleteFile(update.getImageUrl());
                String fileName = this.fileStorageService.storeFile(product.getFile());
                update.setImageUrl("/static/" + fileName);
            }
            this.productRepo.save(update);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("message", "Product id = " + String.valueOf(id) + " update successful ", "product", update));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Product id = " + String.valueOf(id) + " not found"));
        }
    }

    // ========== DEBUG ENDPOINTS ==========

    @GetMapping("/debug-static-setup")
    public Object debugStaticSetup() {
        Map<String, Object> result = new HashMap<>();
        
        // Check directory
        String uploadDir = "/tmp/myApp/static";
        File dir = new File(uploadDir);
        
        result.put("uploadDir", uploadDir);
        result.put("dirExists", dir.exists());
        result.put("dirAbsolutePath", dir.getAbsolutePath());
        result.put("dirCanRead", dir.canRead());
        result.put("dirCanWrite", dir.canWrite());
        
        // Create directory if it doesn't exist
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            result.put("dirCreated", created);
            result.put("dirExistsAfterCreate", dir.exists());
        }
        
        // List files
        if (dir.exists()) {
            File[] files = dir.listFiles();
            result.put("fileCount", files != null ? files.length : 0);
            
            if (files != null && files.length > 0) {
                List<Map<String, Object>> fileDetails = new ArrayList<>();
                for (int i = 0; i < Math.min(files.length, 5); i++) {
                    File file = files[i];
                    Map<String, Object> fileInfo = new HashMap<>();
                    fileInfo.put("name", file.getName());
                    fileInfo.put("size", file.length());
                    fileInfo.put("exists", file.exists());
                    fileInfo.put("canRead", file.canRead());
                    fileInfo.put("path", file.getAbsolutePath());
                    fileInfo.put("testUrl", "https://su6-api-spring.onrender.com/static/" + file.getName());
                    fileDetails.add(fileInfo);
                }
                result.put("sampleFiles", fileDetails);
            }
        }
        
        // Test file creation
        try {
            File testFile = new File(dir, "test.txt");
            Files.write(testFile.toPath(), "Test content".getBytes());
            boolean testFileExists = testFile.exists();
            result.put("testFileCreated", testFileExists);
            if (testFileExists) {
                testFile.delete();
            }
        } catch (Exception e) {
            result.put("testFileError", e.getMessage());
        }
        
        return result;
    }

    @GetMapping("/test-file-access/{filename}")
    public ResponseEntity<Object> testFileAccess(@PathVariable String filename) {
        try {
            String uploadDir = "/tmp/myApp/static";
            Path filePath = Paths.get(uploadDir, filename);
            File file = filePath.toFile();
            
            Map<String, Object> response = new HashMap<>();
            response.put("filename", filename);
            response.put("filePath", filePath.toString());
            response.put("fileExists", file.exists());
            response.put("fileSize", file.exists() ? file.length() : 0);
            response.put("canRead", file.canRead());
            response.put("canWrite", file.canWrite());
            response.put("isFile", file.isFile());
            response.put("absolutePath", file.getAbsolutePath());
            response.put("directUrl", "https://su6-api-spring.onrender.com/static/" + filename);
            
            if (file.exists()) {
                response.put("status", "SUCCESS");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "FILE_NOT_FOUND");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping(value = "/serve-image/{filename:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        try {
            String uploadDir = "/tmp/myApp/static";
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok().body(resource);
            } else {
                System.out.println("❌ Image not found: " + filePath.toString());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.out.println("❌ Error serving image: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/debug/files")
    public Object debugFiles() {
        try {
            String uploadDir = "/tmp/myApp/static";
            File dir = new File(uploadDir);
            Map<String, Object> result = new HashMap();
            result.put("uploadDir", uploadDir);
            result.put("exists", dir.exists());
            result.put("isDirectory", dir.isDirectory());
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles();
                result.put("fileCount", files != null ? files.length : 0);
                result.put("files", files != null ? Arrays.stream(files).map((file) -> {
                    return Map.of("name", file.getName(), "size", file.length(), "url", "https://su6-api-spring.onrender.com/static/" + file.getName());
                }).collect(Collectors.toList()) : List.of());
            }
            return result;
        } catch (Exception var5) {
            return Map.of("error", var5.getMessage());
        }
    }
}