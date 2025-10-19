// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.setec.dao;

import lombok.Generated;
import org.springframework.web.multipart.MultipartFile;

public class PostProductDAO {
   private String name;
   private double price;
   private int qty;
   private MultipartFile file;

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public double getPrice() {
      return this.price;
   }

   @Generated
   public int getQty() {
      return this.qty;
   }

   @Generated
   public MultipartFile getFile() {
      return this.file;
   }

   @Generated
   public void setName(String name) {
      this.name = name;
   }

   @Generated
   public void setPrice(double price) {
      this.price = price;
   }

   @Generated
   public void setQty(int qty) {
      this.qty = qty;
   }

   @Generated
   public void setFile(MultipartFile file) {
      this.file = file;
   }

   @Generated
   public PostProductDAO(String name, double price, int qty, MultipartFile file) {
      this.name = name;
      this.price = price;
      this.qty = qty;
      this.file = file;
   }

   @Generated
   public PostProductDAO() {
   }
}
