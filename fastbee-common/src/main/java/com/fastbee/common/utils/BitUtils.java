/*     */ package com.fastbee.common.utils;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BitUtils
/*     */ {
/*     */   public static int getBitFlag(long num, int bit) {
/*  20 */     return (int)num >> bit & 0x1;
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
/*     */   public static long updateBitValue(long num, int bit, boolean flagValue) {
/*  32 */     if (flagValue)
/*     */     {
/*  34 */       return num | (1 << bit);
/*     */     }
/*     */     
/*  37 */     return num ^ (getBitFlag(num, bit) << bit);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toBinaryString(long num) {
/*  48 */     return Long.toBinaryString(num);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int deter(int num, int i) {
/*  59 */     return num >> i - 1 & 0x1;
/*     */   }
/*     */   
/*     */   public static String bin2hex(String input) {
/*  63 */     StringBuilder stringBuilder = new StringBuilder();
/*  64 */     int i = input.length();
/*  65 */     System.out.println("原数据长度：" + (i / 8) + "字节");
/*     */     
/*  67 */     for (byte b = 0; b < i / 4; b++) {
/*     */       
/*  69 */       String str1 = input.substring(b * 4, (b + 1) * 4);
/*  70 */       int j = Integer.parseInt(str1, 2);
/*  71 */       String str2 = Integer.toHexString(j).toUpperCase();
/*  72 */       stringBuilder.append(str2);
/*     */     } 
/*     */     
/*  75 */     return stringBuilder.toString();
/*     */   }
/*     */   public static int bin2Dec(String binaryString) {
/*  78 */     int i = 0;
/*  79 */     for (byte b = 0; b < binaryString.length(); b++) {
/*  80 */       char c = binaryString.charAt(b);
/*  81 */       if (c > '2' || c < '0')
/*  82 */         throw new NumberFormatException(String.valueOf(b)); 
/*  83 */       i = i * 2 + binaryString.charAt(b) - 48;
/*     */     } 
/*  85 */     return i;
/*     */   }
/*     */   
/*     */   public static int[] string2Ins(String input) {
/*  89 */     StringBuilder stringBuilder = new StringBuilder(input);
/*  90 */     int i = stringBuilder.length() % 8;
/*  91 */     if (i > 0)
/*  92 */       for (byte b1 = 0; b1 < 8 - i; b1++)
/*  93 */         stringBuilder.append("0");  
/*  94 */     int[] arrayOfInt = new int[stringBuilder.length() / 8];
/*     */ 
/*     */     
/*  97 */     for (byte b = 0; b < arrayOfInt.length; b++) {
/*  98 */       arrayOfInt[b] = Integer.parseInt(stringBuilder.substring(b * 8, b * 8 + 8), 2);
/*     */     }
/* 100 */     return arrayOfInt;
/*     */   }
/*     */   public static byte[] string2bytes(String input) {
/* 103 */     StringBuilder stringBuilder = new StringBuilder(input);
/* 104 */     int i = stringBuilder.length() % 8;
/* 105 */     if (i > 0)
/* 106 */       for (byte b1 = 0; b1 < 8 - i; b1++)
/* 107 */         stringBuilder.insert(0, "0");  
/* 108 */     byte[] arrayOfByte = new byte[stringBuilder.length() / 8];
/*     */ 
/*     */     
/* 111 */     for (byte b = 0; b < arrayOfByte.length; b++) {
/* 112 */       arrayOfByte[b] = (byte)Integer.parseInt(stringBuilder.substring(b * 8, b * 8 + 8), 2);
/*     */     }
/* 114 */     return arrayOfByte;
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
/*     */   private static String a(byte[] paramArrayOfbyte, Object paramObject, Object... paramVarArgs) {
/* 126 */     if (paramArrayOfbyte != null && paramArrayOfbyte.length > 0 && paramObject != null) {
/*     */       try {
/*     */         byte[] arrayOfByte;
/* 129 */         int i = Integer.parseInt(paramObject.toString());
/* 130 */         if (paramVarArgs != null && paramVarArgs.length > 0) {
/* 131 */           int j = Integer.parseInt(paramVarArgs[0].toString());
/* 132 */           if (i >= j || j <= 0) {
/* 133 */             arrayOfByte = Arrays.copyOfRange(paramArrayOfbyte, i, i + 1);
/*     */           } else {
/* 135 */             arrayOfByte = Arrays.copyOfRange(paramArrayOfbyte, i, j + 1);
/*     */           } 
/*     */         } else {
/* 138 */           arrayOfByte = Arrays.copyOfRange(paramArrayOfbyte, i, i + 1);
/*     */         } 
/* 140 */         if (arrayOfByte != null && arrayOfByte.length > 0) {
/* 141 */           long l = 0L;
/* 142 */           byte b = -1;
/* 143 */           for (int j = arrayOfByte.length - 1; j >= 0; j--, b++) {
/* 144 */             byte b1 = arrayOfByte[j];
/* 145 */             if (b1 < 0) {
/* 146 */               b1 += 256;
/*     */             }
/* 148 */             l += Math.round(b1 * Math.pow(16.0D, (2 * b + 2)));
/*     */           } 
/* 150 */           return (new Long(l)).toString();
/*     */         } 
/* 152 */       } catch (Exception exception) {
/* 153 */         exception.printStackTrace();
/*     */       } 
/*     */     }
/* 156 */     return null;
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
/*     */   
/*     */   private static byte[] a(Integer paramInteger, int... paramVarArgs) {
/* 169 */     return a(Integer.toHexString(paramInteger.intValue()), paramVarArgs);
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
/*     */   private static byte[] a(String paramString, int... paramVarArgs) {
/* 181 */     paramString = paramString.toLowerCase();
/* 182 */     if (paramString.length() % 2 != 0) {
/* 183 */       paramString = "0" + paramString;
/*     */     }
/* 185 */     int i = paramString.length() / 2;
/* 186 */     if (i < 1) {
/* 187 */       i = 1;
/*     */     }
/* 189 */     int j = i;
/* 190 */     if (paramVarArgs != null && paramVarArgs.length > 0 && paramVarArgs[0] >= i) {
/* 191 */       j = paramVarArgs[0];
/*     */     }
/* 193 */     byte[] arrayOfByte = new byte[j];
/* 194 */     byte b1 = 0;
/* 195 */     for (byte b2 = 0; b2 < j; b2++) {
/* 196 */       if (b2 < j - i) {
/* 197 */         arrayOfByte[b2] = 0;
/*     */       } else {
/* 199 */         byte b = (byte)(Character.digit(paramString.charAt(b1), 16) & 0xFF);
/* 200 */         if (b1 + 1 < paramString.length()) {
/* 201 */           byte b3 = (byte)(Character.digit(paramString.charAt(b1 + 1), 16) & 0xFF);
/* 202 */           arrayOfByte[b2] = (byte)(b << 4 | b3);
/*     */         } else {
/* 204 */           arrayOfByte[b2] = b;
/*     */         } 
/* 206 */         b1 += 2;
/*     */       } 
/*     */     } 
/* 209 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static byte[] a(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
/* 219 */     if (paramArrayOfbyte1 == null) {
/* 220 */       return paramArrayOfbyte2;
/*     */     }
/* 222 */     if (paramArrayOfbyte2 == null) {
/* 223 */       return paramArrayOfbyte1;
/*     */     }
/* 225 */     return a(new byte[][] { paramArrayOfbyte1, paramArrayOfbyte2 });
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
/*     */   private static byte[] a(byte[]... paramVarArgs) {
/* 237 */     if (paramVarArgs != null && paramVarArgs.length > 0) {
/* 238 */       int i = 0;
/* 239 */       for (byte b1 = 0; b1 < paramVarArgs.length; b1++) {
/* 240 */         i += (paramVarArgs[b1]).length;
/*     */       }
/* 242 */       byte[] arrayOfByte = new byte[i];
/* 243 */       byte b2 = 0;
/* 244 */       for (byte b3 = 0; b3 < paramVarArgs.length; b3++) {
/* 245 */         byte[] arrayOfByte1 = paramVarArgs[b3];
/* 246 */         for (byte b = 0; b < arrayOfByte1.length; b++) {
/* 247 */           arrayOfByte[b2++] = arrayOfByte1[b];
/*     */         }
/*     */       } 
/* 250 */       return arrayOfByte;
/*     */     } 
/* 252 */     return null;
/*     */   }
/*     */   
/*     */   public static byte[] hexStringToByteArray(String s) {
/* 256 */     int i = s.length();
/* 257 */     byte[] arrayOfByte = new byte[i / 2];
/* 258 */     for (byte b = 0; b < i; b += 2) {
/* 259 */       arrayOfByte[b / 2] = 
/* 260 */         (byte)((Character.digit(s.charAt(b), 16) << 4) + Character.digit(s.charAt(b + 1), 16));
/*     */     }
/* 262 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void main(String[] args) {
/* 269 */     String str = bin2hex("1111111000000000");
/* 270 */     int i = bin2Dec("1111111000000000");
/* 271 */     byte[] arrayOfByte = string2bytes("111111000000000");
/* 272 */     System.out.println(str);
/* 273 */     System.out.println(i);
/*     */   }
/*     */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\BitUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */