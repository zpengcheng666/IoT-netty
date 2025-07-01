/*    */ package com.fastbee.common.utils.wechat;
/*    */ 
/*    */ import java.security.MessageDigest;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SHA1
/*    */ {
/*    */   public static String getSHA1(String token, String timestamp, String nonce, String encrypt) throws AesException {
/*    */     try {
/* 29 */       String[] arrayOfString = { token, timestamp, nonce, encrypt };
/* 30 */       StringBuffer stringBuffer1 = new StringBuffer();
/*    */       
/* 32 */       Arrays.sort((Object[])arrayOfString);
/* 33 */       for (byte b1 = 0; b1 < 4; b1++) {
/* 34 */         stringBuffer1.append(arrayOfString[b1]);
/*    */       }
/* 36 */       String str1 = stringBuffer1.toString();
/*    */       
/* 38 */       MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
/* 39 */       messageDigest.update(str1.getBytes());
/* 40 */       byte[] arrayOfByte = messageDigest.digest();
/*    */       
/* 42 */       StringBuffer stringBuffer2 = new StringBuffer();
/* 43 */       String str2 = "";
/* 44 */       for (byte b2 = 0; b2 < arrayOfByte.length; b2++) {
/* 45 */         str2 = Integer.toHexString(arrayOfByte[b2] & 0xFF);
/* 46 */         if (str2.length() < 2) {
/* 47 */           stringBuffer2.append(0);
/*    */         }
/* 49 */         stringBuffer2.append(str2);
/*    */       } 
/* 51 */       return stringBuffer2.toString();
/* 52 */     } catch (Exception exception) {
/* 53 */       exception.printStackTrace();
/* 54 */       throw new AesException(-40003);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\wechat\SHA1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */