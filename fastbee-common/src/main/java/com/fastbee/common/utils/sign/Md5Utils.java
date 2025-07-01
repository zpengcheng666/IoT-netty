/*    */ package com.fastbee.common.utils.sign;
/*    */ 
/*    */ import java.nio.charset.StandardCharsets;
/*    */ import java.security.MessageDigest;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Md5Utils
/*    */ {
/* 15 */   private static final Logger cJ = LoggerFactory.getLogger(Md5Utils.class);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static byte[] p(String paramString) {
/*    */     try {
/* 22 */       MessageDigest messageDigest = MessageDigest.getInstance("MD5");
/* 23 */       messageDigest.reset();
/* 24 */       messageDigest.update(paramString.getBytes("UTF-8"));
/* 25 */       return messageDigest.digest();
/*    */     
/*    */     }
/* 28 */     catch (Exception exception) {
/*    */       
/* 30 */       cJ.error("MD5 Error...", exception);
/*    */       
/* 32 */       return null;
/*    */     } 
/*    */   }
/*    */   
/*    */   private static final String d(byte[] paramArrayOfbyte) {
/* 37 */     if (paramArrayOfbyte == null)
/*    */     {
/* 39 */       return null;
/*    */     }
/* 41 */     StringBuffer stringBuffer = new StringBuffer(paramArrayOfbyte.length * 2);
/*    */ 
/*    */     
/* 44 */     for (byte b = 0; b < paramArrayOfbyte.length; b++) {
/*    */       
/* 46 */       if ((paramArrayOfbyte[b] & 0xFF) < 16)
/*    */       {
/* 48 */         stringBuffer.append("0");
/*    */       }
/* 50 */       stringBuffer.append(Long.toString((paramArrayOfbyte[b] & 0xFF), 16));
/*    */     } 
/* 52 */     return stringBuffer.toString();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static String hash(String s) {
/*    */     try {
/* 59 */       return new String(d(p(s)).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
/*    */     }
/* 61 */     catch (Exception exception) {
/*    */       
/* 63 */       cJ.error("not supported charset...{}", exception);
/* 64 */       return s;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\sign\Md5Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */