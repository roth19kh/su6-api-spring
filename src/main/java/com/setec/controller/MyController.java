// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.setec.controller;

import com.setec.dao.FileStorageService;
import com.setec.dao.PostProductDAO;
import com.setec.dao.PutProductDAO;
import com.setec.entities.Product;
import com.setec.repos.ProductRepo;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/product"})
public class MyController {
   @Autowired
   private ProductRepo productRepo;
   @Autowired
   private FileStorageService fileStorageService;

   public MyController() {
   }

   @GetMapping
   public Object getAll() {
      List<Product> products = this.productRepo.findAll();
      return products.size() == 0 ? ResponseEntity.status(404).body(Map.of("message", "product is empty")) : products;
   }

   @PostMapping(
      consumes = {"multipart/form-data"}
   )
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

   @PutMapping(
      consumes = {"multipart/form-data"}
   )
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

   @GetMapping({"/debug/files"})
   public Object debugFiles() {
      try {
         String uploadDir = System.getenv("DATABASE_URL") != null ? "/tmp/static" : "myApp/static";
         File dir = new File(uploadDir);
         Map<String, Object> result = new HashMap();
         result.put("uploadDir", uploadDir);
         result.put("exists", dir.exists());
         result.put("isDirectory", dir.isDirectory());
         if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            result.put("fileCount", files != null ? files.length : 0);
            result.put("files", files != null ? Arrays.stream(files).map((file) -> {
               return Map.of("name", file.getName(), "size", file.length(), "url", "https://product-web-api.onrender.com/static/" + file.getName());
            }).collect(Collectors.toList()) : List.of());
         }

         return result;
      } catch (Exception var5) {
         return Map.of("error", var5.getMessage());
      }
   }
}
