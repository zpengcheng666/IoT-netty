/*     */ package com.fastbee.common.utils.gateway;
/*     */ 
/*     */ import com.fastbee.common.utils.CaculateUtils;
/*     */ import com.fastbee.common.utils.gateway.protocol.ByteUtils;
/*     */ import org.apache.commons.lang3.ArrayUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CRC16Utils
/*     */ {
/*  11 */   private static int aW = 255;
/*     */   
/*  13 */   private static int aX = 1;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int aY = 4;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int aZ = 255;
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getCRC(byte[] bytes) {
/*  26 */     return getCRC(bytes, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getCRC(byte[] bytes, boolean lb) {
/*  35 */     int i = 65535;
/*  36 */     char c = 'ꀁ';
/*     */ 
/*     */     
/*  39 */     for (byte b = 0; b < bytes.length; b++) {
/*  40 */       i ^= bytes[b] & 0xFF;
/*  41 */       for (byte b1 = 0; b1 < 8; b1++) {
/*  42 */         if ((i & 0x1) != 0) {
/*  43 */           i >>= 1;
/*  44 */           i ^= c;
/*     */         } else {
/*  46 */           i >>= 1;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  52 */     String str = Integer.toHexString(i).toUpperCase();
/*  53 */     if (str.length() != 4) {
/*  54 */       StringBuffer stringBuffer = new StringBuffer("0000");
/*  55 */       str = stringBuffer.replace(4 - str.length(), 4, str).toString();
/*     */     } 
/*     */     
/*  58 */     if (lb) {
/*  59 */       str = str.substring(2, 4) + str.substring(0, 2);
/*     */     }
/*     */     
/*  62 */     return str;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] getCrc16Byte(byte[] bytes) {
/*  74 */     int i = 65535;
/*     */     
/*  76 */     char c = 'ꀁ';
/*  77 */     for (byte b : bytes) {
/*  78 */       i ^= b & aW;
/*  79 */       for (byte b1 = 0; b1 < 8; b1++) {
/*  80 */         if ((i & aX) != 0) {
/*  81 */           i >>= 1;
/*  82 */           i ^= c;
/*     */         } else {
/*  84 */           i >>= 1;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/*  89 */     return new byte[] { (byte)(i & 0xFF), (byte)(i >> 8 & 0xFF) };
/*     */   }
/*     */   
/*     */   public static byte[] AddCRC(byte[] source) {
/*  93 */     byte[] arrayOfByte1 = new byte[source.length + 2];
/*  94 */     byte[] arrayOfByte2 = getCrc16Byte(source);
/*  95 */     System.arraycopy(source, 0, arrayOfByte1, 0, source.length);
/*  96 */     System.arraycopy(arrayOfByte2, 0, arrayOfByte1, arrayOfByte1.length - 2, 2);
/*  97 */     return arrayOfByte1;
/*     */   }
/*     */   
/*     */   public static byte[] AddPakCRC(byte[] source) {
/* 101 */     byte[] arrayOfByte1 = ArrayUtils.subarray(source, 11, source.length);
/* 102 */     byte[] arrayOfByte2 = new byte[source.length + 2];
/* 103 */     byte[] arrayOfByte3 = getCrc16Byte(arrayOfByte1);
/* 104 */     System.arraycopy(source, 0, arrayOfByte2, 0, source.length);
/* 105 */     System.arraycopy(arrayOfByte3, 0, arrayOfByte2, arrayOfByte2.length - 2, 2);
/* 106 */     return arrayOfByte2;
/*     */   }
/*     */   
/*     */   public static byte[] CRC(byte[] source) {
/* 110 */     source[2] = (byte)(source[2] * 2);
/* 111 */     byte[] arrayOfByte1 = new byte[source.length + 2];
/* 112 */     byte[] arrayOfByte2 = getCrc16Byte(source);
/* 113 */     System.arraycopy(source, 0, arrayOfByte1, 0, source.length);
/* 114 */     System.arraycopy(arrayOfByte2, 0, arrayOfByte1, arrayOfByte1.length - 2, 2);
/* 115 */     return arrayOfByte1;
/*     */   }
/*     */   
/*     */   public static byte CRC8(byte[] buffer) {
/* 119 */     int i = 255;
/* 120 */     for (byte b = 0; b < buffer.length; b++) {
/* 121 */       i ^= buffer[b] & 0xFF;
/* 122 */       for (byte b1 = 0; b1 < 8; b1++) {
/* 123 */         if ((i & 0x1) != 0) {
/* 124 */           i >>= 1;
/* 125 */           i ^= 0xB8;
/*     */         } else {
/* 127 */           i >>= 1;
/*     */         } 
/*     */       } 
/*     */     } 
/* 131 */     return (byte)i;
/*     */   }
/*     */ 
/*     */   
/*     */   public static void main(String[] args) throws Exception {
/* 136 */     String str1 = "0103028000";
/* 137 */     byte[] arrayOfByte1 = ByteUtils.hexToByte(str1);
/* 138 */     String str2 = getCRC(arrayOfByte1);
/* 139 */     System.out.println(str2);
/* 140 */     String str3 = "680868333701120008C100";
/* 141 */     byte[] arrayOfByte2 = ByteUtils.hexToByte(str3);
/* 142 */     byte b = CRC8(arrayOfByte2);
/* 143 */     System.out.println(b);
/* 144 */     float f = CaculateUtils.toFloat32_CDAB(arrayOfByte1).floatValue();
/* 145 */     System.out.println(f);
/*     */   }
/*     */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\gateway\CRC16Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */