/*    */ package com.fastbee.common.exception.file;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import org.apache.commons.fileupload.FileUploadException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class InvalidExtensionException
/*    */   extends FileUploadException
/*    */ {
/*    */   private static final long N = 1L;
/*    */   private String[] O;
/*    */   private String P;
/*    */   private String Q;
/*    */   
/*    */   public InvalidExtensionException(String[] allowedExtension, String extension, String filename) {
/* 21 */     super("文件[" + filename + "]后缀[" + extension + "]不正确，请上传" + Arrays.toString((Object[])allowedExtension) + "格式");
/* 22 */     this.O = allowedExtension;
/* 23 */     this.P = extension;
/* 24 */     this.Q = filename;
/*    */   }
/*    */ 
/*    */   
/*    */   public String[] getAllowedExtension() {
/* 29 */     return this.O;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getExtension() {
/* 34 */     return this.P;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getFilename() {
/* 39 */     return this.Q;
/*    */   }
/*    */   
/*    */   public static class InvalidImageExtensionException
/*    */     extends InvalidExtensionException
/*    */   {
/*    */     private static final long S = 1L;
/*    */     
/*    */     public InvalidImageExtensionException(String[] allowedExtension, String extension, String filename) {
/* 48 */       super(allowedExtension, extension, filename);
/*    */     }
/*    */   }
/*    */   
/*    */   public static class InvalidFlashExtensionException
/*    */     extends InvalidExtensionException
/*    */   {
/*    */     private static final long R = 1L;
/*    */     
/*    */     public InvalidFlashExtensionException(String[] allowedExtension, String extension, String filename) {
/* 58 */       super(allowedExtension, extension, filename);
/*    */     }
/*    */   }
/*    */   
/*    */   public static class InvalidMediaExtensionException
/*    */     extends InvalidExtensionException
/*    */   {
/*    */     private static final long T = 1L;
/*    */     
/*    */     public InvalidMediaExtensionException(String[] allowedExtension, String extension, String filename) {
/* 68 */       super(allowedExtension, extension, filename);
/*    */     }
/*    */   }
/*    */   
/*    */   public static class InvalidVideoExtensionException
/*    */     extends InvalidExtensionException
/*    */   {
/*    */     private static final long U = 1L;
/*    */     
/*    */     public InvalidVideoExtensionException(String[] allowedExtension, String extension, String filename) {
/* 78 */       super(allowedExtension, extension, filename);
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\common\exception\file\InvalidExtensionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */