/*    */ package com.fastbee.common.utils.file;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MimeTypeUtils
/*    */ {
/*    */   public static final String IMAGE_PNG = "image/png";
/*    */   public static final String IMAGE_JPG = "image/jpg";
/*    */   public static final String IMAGE_JPEG = "image/jpeg";
/*    */   public static final String IMAGE_BMP = "image/bmp";
/*    */   public static final String IMAGE_GIF = "image/gif";
/* 20 */   public static final String[] IMAGE_EXTENSION = new String[] { "bmp", "gif", "jpg", "jpeg", "png" };
/*    */   
/* 22 */   public static final String[] FLASH_EXTENSION = new String[] { "swf", "flv" };
/*    */   
/* 24 */   public static final String[] MEDIA_EXTENSION = new String[] { "swf", "flv", "mp3", "wav", "wma", "wmv", "mid", "avi", "mpg", "asf", "rm", "rmvb" };
/*    */ 
/*    */   
/* 27 */   public static final String[] VIDEO_EXTENSION = new String[] { "mp4", "avi", "rmvb" };
/*    */   
/* 29 */   public static final String[] DEFAULT_ALLOWED_EXTENSION = new String[] { "bmp", "gif", "jpg", "jpeg", "png", "svg", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt", "rar", "zip", "gz", "bz2", "mp4", "avi", "rmvb", "pdf" };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getExtension(String prefix) {
/* 43 */     switch (prefix) {
/*    */       
/*    */       case "image/png":
/* 46 */         return "png";
/*    */       case "image/jpg":
/* 48 */         return "jpg";
/*    */       case "image/jpeg":
/* 50 */         return "jpeg";
/*    */       case "image/bmp":
/* 52 */         return "bmp";
/*    */       case "image/gif":
/* 54 */         return "gif";
/*    */     } 
/* 56 */     return "";
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\file\MimeTypeUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */