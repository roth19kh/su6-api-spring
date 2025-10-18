package com.setec.dao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
   public FileStorageService() {
   }

   public String getUploadDir() {
      return "/tmp/myApp/static";
   }

   public String storeFile(MultipartFile file) throws IOException {
      String uploadDir = this.getUploadDir();
      File dir = new File(uploadDir);
      if (!dir.exists()) {
         boolean created = dir.mkdirs();
         System.out.println("📁 Directory created: " + created + " at " + dir.getAbsolutePath());
      }

      String originalFileName = file.getOriginalFilename();
      String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
      String fileName = UUID.randomUUID() + extension;
      Path filePath = Paths.get(uploadDir, fileName);
      file.transferTo(filePath.toFile());
      System.out.println("💾 File saved: " + filePath.toAbsolutePath());
      return fileName;
   }

   public boolean deleteFile(String imageUrl) {
      try {
         String uploadDir = this.getUploadDir();
         String fileName = imageUrl.replace("/static/", "");
         Path filePath = Paths.get(uploadDir, fileName);
         boolean deleted = filePath.toFile().delete();
         System.out.println("🗑️ File deleted: " + deleted + " - " + fileName);
         return deleted;
      } catch (Exception var6) {
         System.out.println("❌ Error deleting file: " + var6.getMessage());
         return false;
      }
   }
}