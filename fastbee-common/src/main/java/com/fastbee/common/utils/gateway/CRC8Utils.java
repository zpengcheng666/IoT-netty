/*    */ package com.fastbee.common.utils.gateway;
/*    */ 
/*    */ import com.fastbee.common.utils.gateway.protocol.ByteUtils;
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
/*    */ public class CRC8Utils
/*    */ {
/*    */   public static byte calcCrc8_E5(byte[] data) {
/* 21 */     byte b = 0;
/* 22 */     for (byte b1 = 0; b1 < data.length; b1++) {
/* 23 */       b = (byte)(b ^ data[b1]);
/* 24 */       for (byte b2 = 0; b2 < 8; b2++) {
/* 25 */         if ((b & 0x80) != 0) {
/* 26 */           b = (byte)(b << 1);
/* 27 */           b = (byte)(b ^ 0xE5);
/*    */         } else {
/* 29 */           b = (byte)(b << 1);
/*    */         } 
/*    */       } 
/*    */     } 
/* 33 */     return b;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 40 */   static byte[] ba = new byte[] { 0, 94, -68, -30, 97, 63, -35, -125, -62, -100, 126, 32, -93, -3, 31, 65, -99, -61, 33, Byte.MAX_VALUE, -4, -94, 64, 30, 95, 1, -29, -67, 62, 96, -126, -36, 35, 125, -97, -63, 66, 28, -2, -96, -31, -65, 93, 3, Byte.MIN_VALUE, -34, 60, 98, -66, -32, 2, 92, -33, -127, 99, 61, 124, 34, -64, -98, 29, 67, -95, -1, 70, 24, -6, -92, 39, 121, -101, -59, -124, -38, 56, 102, -27, -69, 89, 7, -37, -123, 103, 57, -70, -28, 6, 88, 25, 71, -91, -5, 120, 38, -60, -102, 101, 59, -39, -121, 4, 90, -72, -26, -89, -7, 27, 69, -58, -104, 122, 36, -8, -90, 68, 26, -103, -57, 37, 123, 58, 100, -122, -40, 91, 5, -25, -71, -116, -46, 48, 110, -19, -77, 81, 15, 78, 16, -14, -84, 47, 113, -109, -51, 17, 79, -83, -13, 112, 46, -52, -110, -45, -115, 111, 49, -78, -20, 14, 80, -81, -15, 19, 77, -50, -112, 114, 44, 109, 51, -47, -113, 12, 82, -80, -18, 50, 108, -114, -48, 83, 13, -17, -79, -16, -82, 76, 18, -111, -49, 45, 115, -54, -108, 118, 40, -85, -11, 23, 73, 8, 86, -76, -22, 105, 55, -43, -117, 87, 9, -21, -75, 54, 104, -118, -44, -107, -53, 41, 119, -12, -86, 72, 22, -23, -73, 85, 11, -120, -42, 52, 106, 43, 117, -105, -55, 74, 20, -10, -88, 116, 42, -56, -106, 21, 75, -87, -9, -74, -24, 10, 84, -41, -119, 107, 53 };
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
/*    */   public static byte calcCrc8(byte[] data) {
/* 52 */     return calcCrc8(data, 0, data.length, (byte)0);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static byte calcCrc8(byte[] data, int offset, int len) {
/* 64 */     return calcCrc8(data, offset, len, (byte)0);
/*    */   }
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
/*    */   public static byte calcCrc8(byte[] data, int offset, int len, byte preval) {
/* 77 */     byte b = preval;
/* 78 */     for (int i = offset; i < offset + len; i++) {
/* 79 */       b = ba[0xFF & (b ^ data[i])];
/*    */     }
/* 81 */     return b;
/*    */   }
/*    */ 
/*    */   
/*    */   public static void main(String[] args) {
/* 86 */     String str = "333701120008C100";
/* 87 */     byte[] arrayOfByte = ByteUtils.hexToByte(str);
/* 88 */     byte b = calcCrc8_E5(arrayOfByte);
/* 89 */     System.out.println("" + Integer.toHexString(0xFF & b));
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\gateway\CRC8Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */