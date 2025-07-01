/*    */ package com.fastbee.common.utils;
/*    */ 
/*    */ import com.fastbee.common.utils.uuid.IdUtils;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.security.GeneralSecurityException;
/*    */ import java.security.MessageDigest;
/*    */ import java.security.SecureRandom;
/*    */ import org.apache.commons.lang3.Validate;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DigestUtils
/*    */ {
/* 15 */   private static SecureRandom au = new SecureRandom();
/* 16 */   private static IdUtils av = new IdUtils(0L, 0L);
/*    */   
/*    */   public static String getId() {
/* 19 */     return String.valueOf(Math.abs(au.nextLong()));
/*    */   }
/*    */   
/*    */   public static String nextId() {
/* 23 */     return String.valueOf(av.nextId());
/*    */   }
/*    */ 
/*    */   
/*    */   public static byte[] genSalt(int numBytes) {
/* 28 */     Validate.isTrue((numBytes > 0), "numBytes argument must be a positive integer (1 or larger)", numBytes);
/* 29 */     byte[] arrayOfByte = new byte[numBytes];
/* 30 */     au.nextBytes(arrayOfByte);
/* 31 */     return arrayOfByte;
/*    */   }
/*    */   
/*    */   public static byte[] digest(byte[] input, String algorithm, byte[] salt, int iterations) {
/*    */     try {
/* 36 */       MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
/* 37 */       if (salt != null) {
/* 38 */         messageDigest.update(salt);
/*    */       }
/*    */       
/* 41 */       byte[] arrayOfByte = messageDigest.digest(input);
/*    */       
/* 43 */       for (byte b = 1; b < iterations; b++) {
/* 44 */         messageDigest.reset();
/* 45 */         arrayOfByte = messageDigest.digest(arrayOfByte);
/*    */       } 
/*    */       
/* 48 */       return arrayOfByte;
/* 49 */     } catch (GeneralSecurityException generalSecurityException) {
/* 50 */       throw ExceptionUtils.unchecked(generalSecurityException);
/*    */     } 
/*    */   }
/*    */   
/*    */   public static byte[] digest(InputStream input, String algorithm) throws IOException {
/*    */     try {
/* 56 */       MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
/* 57 */       char c = ' ';
/* 58 */       byte[] arrayOfByte = new byte[c];
/*    */       int i;
/* 60 */       for (i = input.read(arrayOfByte, 0, c); i > -1; i = input.read(arrayOfByte, 0, c)) {
/* 61 */         messageDigest.update(arrayOfByte, 0, i);
/*    */       }
/*    */       
/* 64 */       return messageDigest.digest();
/* 65 */     } catch (GeneralSecurityException generalSecurityException) {
/* 66 */       throw ExceptionUtils.unchecked(generalSecurityException);
/*    */     } 
/*    */   }
/*    */   
/*    */   public static void main(String[] args) {
/* 71 */     for (byte b = 0; b < 10; b++)
/* 72 */       System.out.println(nextId()); 
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\DigestUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */