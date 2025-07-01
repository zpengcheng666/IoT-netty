/*    */ package com.fastbee.common.utils;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.UnsupportedEncodingException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Md5Utils
/*    */ {
/*    */   private static final String aF = "MD5";
/*    */   private static final String aG = "UTF-8";
/*    */   
/*    */   public static String md5(String input) {
/* 17 */     return md5(input, 1);
/*    */   }
/*    */   
/*    */   public static String md5(String input, int iterations) {
/*    */     try {
/* 22 */       return EncodeUtils.encodeHex(DigestUtils.digest(input.getBytes("UTF-8"), "MD5", (byte[])null, iterations));
/* 23 */     } catch (UnsupportedEncodingException unsupportedEncodingException) {
/* 24 */       return "";
/*    */     } 
/*    */   }
/*    */   
/*    */   public static byte[] md5(byte[] input) {
/* 29 */     return md5(input, 1);
/*    */   }
/*    */   
/*    */   public static byte[] md5(byte[] input, int iterations) {
/* 33 */     return DigestUtils.digest(input, "MD5", (byte[])null, iterations);
/*    */   }
/*    */   
/*    */   public static byte[] md5(InputStream input) throws IOException {
/* 37 */     return DigestUtils.digest(input, "MD5");
/*    */   }
/*    */   
/*    */   public static boolean isMd5(String str) {
/* 41 */     byte b1 = 0;
/* 42 */     for (byte b2 = 0; b2 < str.length(); b2++) {
/* 43 */       switch (str.charAt(b2)) {
/*    */         case '0':
/*    */         case '1':
/*    */         case '2':
/*    */         case '3':
/*    */         case '4':
/*    */         case '5':
/*    */         case '6':
/*    */         case '7':
/*    */         case '8':
/*    */         case '9':
/*    */         case 'A':
/*    */         case 'B':
/*    */         case 'C':
/*    */         case 'D':
/*    */         case 'E':
/*    */         case 'F':
/*    */         case 'a':
/*    */         case 'b':
/*    */         case 'c':
/*    */         case 'd':
/*    */         case 'e':
/*    */         case 'f':
/* 66 */           b1++;
/* 67 */           if (32 <= b1) return true; 
/*    */           break;
/*    */         case '/':
/* 70 */           if (b2 + 10 < str.length()) {
/* 71 */             char c1 = str.charAt(b2 + 1);
/* 72 */             char c2 = str.charAt(b2 + 8);
/* 73 */             if ('/' == c2 && ('s' == c1 || 'S' == c1)) return true; 
/*    */           } 
/*    */         default:
/* 76 */           b1 = 0;
/*    */           break;
/*    */       } 
/*    */     } 
/* 80 */     return false;
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\Md5Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */