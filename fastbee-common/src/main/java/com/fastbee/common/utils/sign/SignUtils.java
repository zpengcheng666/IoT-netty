/*    */ package com.fastbee.common.utils.sign;
/*    */ 
/*    */ import java.security.MessageDigest;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ import java.util.Arrays;
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
/*    */ public class SignUtils
/*    */ {
/*    */   public static boolean checkSignature(String token, String signature, String timestamp, String nonce) {
/* 20 */     String[] arrayOfString = { token, timestamp, nonce };
/* 21 */     Arrays.sort((Object[])arrayOfString);
/*    */ 
/*    */     
/* 24 */     StringBuilder stringBuilder = new StringBuilder();
/* 25 */     for (byte b = 0; b < arrayOfString.length; b++) {
/* 26 */       stringBuilder.append(arrayOfString[b]);
/*    */     }
/* 28 */     MessageDigest messageDigest = null;
/* 29 */     String str = null;
/*    */     try {
/* 31 */       messageDigest = MessageDigest.getInstance("SHA-1");
/*    */       
/* 33 */       byte[] arrayOfByte = messageDigest.digest(stringBuilder.toString().getBytes());
/* 34 */       str = e(arrayOfByte);
/* 35 */     } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
/* 36 */       noSuchAlgorithmException.printStackTrace();
/*    */     } 
/*    */     
/* 39 */     stringBuilder = null;
/*    */     
/* 41 */     return (str != null) ? str.equals(signature.toUpperCase()) : false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static String e(byte[] paramArrayOfbyte) {
/* 48 */     String str = "";
/* 49 */     for (byte b = 0; b < paramArrayOfbyte.length; b++) {
/* 50 */       str = str + a(paramArrayOfbyte[b]);
/*    */     }
/* 52 */     return str;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static String a(byte paramByte) {
/* 59 */     char[] arrayOfChar1 = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/* 60 */     char[] arrayOfChar2 = new char[2];
/* 61 */     arrayOfChar2[0] = arrayOfChar1[paramByte >>> 4 & 0xF];
/* 62 */     arrayOfChar2[1] = arrayOfChar1[paramByte & 0xF];
/* 63 */     return new String(arrayOfChar2);
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\sign\SignUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */