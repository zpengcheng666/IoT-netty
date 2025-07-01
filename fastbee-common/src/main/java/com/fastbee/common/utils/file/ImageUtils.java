/*    */ package com.fastbee.common.utils.file;
/*    */ 
/*    */ import com.fastbee.common.config.RuoYiConfig;
/*    */ import com.fastbee.common.utils.StringUtils;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
/*    */ import java.net.URLConnection;
/*    */ import java.util.Arrays;
/*    */ import org.apache.poi.util.IOUtils;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ImageUtils
/*    */ {
/* 23 */   private static final Logger aV = LoggerFactory.getLogger(ImageUtils.class);
/*    */ 
/*    */   
/*    */   public static byte[] getImage(String imagePath) {
/* 27 */     InputStream inputStream = getFile(imagePath);
/*    */     
/*    */     try {
/* 30 */       return IOUtils.toByteArray(inputStream);
/*    */     }
/* 32 */     catch (Exception exception) {
/*    */       
/* 34 */       aV.error("图片加载异常 {}", exception);
/* 35 */       return null;
/*    */     }
/*    */     finally {
/*    */       
/* 39 */       IOUtils.closeQuietly(inputStream);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static InputStream getFile(String imagePath) {
/*    */     try {
/* 47 */       byte[] arrayOfByte = readFile(imagePath);
/* 48 */       arrayOfByte = Arrays.copyOf(arrayOfByte, arrayOfByte.length);
/* 49 */       return new ByteArrayInputStream(arrayOfByte);
/*    */     }
/* 51 */     catch (Exception exception) {
/*    */       
/* 53 */       aV.error("获取图片异常 {}", exception);
/*    */       
/* 55 */       return null;
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static byte[] readFile(String url) {
/* 66 */     InputStream inputStream = null;
/*    */     
/*    */     try {
/* 69 */       if (url.startsWith("http")) {
/*    */ 
/*    */         
/* 72 */         URL uRL = new URL(url);
/* 73 */         URLConnection uRLConnection = uRL.openConnection();
/* 74 */         uRLConnection.setConnectTimeout(30000);
/* 75 */         uRLConnection.setReadTimeout(60000);
/* 76 */         uRLConnection.setDoInput(true);
/* 77 */         inputStream = uRLConnection.getInputStream();
/*    */       
/*    */       }
/*    */       else {
/*    */         
/* 82 */         String str1 = RuoYiConfig.getProfile();
/* 83 */         String str2 = str1 + StringUtils.substringAfter(url, "/profile");
/* 84 */         inputStream = new FileInputStream(str2);
/*    */       } 
/* 86 */       return IOUtils.toByteArray(inputStream);
/*    */     }
/* 88 */     catch (Exception exception) {
/*    */       
/* 90 */       aV.error("获取文件路径异常 {}", exception);
/* 91 */       return null;
/*    */     }
/*    */     finally {
/*    */       
/* 95 */       IOUtils.closeQuietly(inputStream);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\file\ImageUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */