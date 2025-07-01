/*    */ package com.fastbee.common.utils.wechat;
/*    */ 
/*    */ import java.nio.charset.Charset;
/*    */ import java.util.Arrays;
/*    */ 
/*    */ 
/*    */ 
/*    */ class b
/*    */ {
/* 10 */   static Charset db = Charset.forName("utf-8");
/* 11 */   static int dh = 32;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static byte[] c(int paramInt) {
/* 21 */     int i = dh - paramInt % dh;
/* 22 */     if (i == 0) {
/* 23 */       i = dh;
/*    */     }
/*    */     
/* 26 */     char c = d(i);
/* 27 */     String str = new String();
/* 28 */     for (byte b1 = 0; b1 < i; b1++) {
/* 29 */       str = str + c;
/*    */     }
/* 31 */     return str.getBytes(db);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static byte[] h(byte[] paramArrayOfbyte) {
/* 41 */     byte b1 = paramArrayOfbyte[paramArrayOfbyte.length - 1];
/* 42 */     if (b1 < 1 || b1 > 32) {
/* 43 */       b1 = 0;
/*    */     }
/* 45 */     return Arrays.copyOfRange(paramArrayOfbyte, 0, paramArrayOfbyte.length - b1);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static char d(int paramInt) {
/* 55 */     byte b1 = (byte)(paramInt & 0xFF);
/* 56 */     return (char)b1;
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\wechat\b.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */