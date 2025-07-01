/*     */ package com.fastbee.common.utils.wechat;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Arrays;
/*     */ import java.util.Random;
/*     */ import javax.crypto.Cipher;
/*     */ import javax.crypto.spec.IvParameterSpec;
/*     */ import javax.crypto.spec.SecretKeySpec;
/*     */ import org.apache.commons.codec.binary.Base64;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WXBizMsgCrypt
/*     */ {
/*  42 */   static Charset db = Charset.forName("utf-8");
/*  43 */   Base64 dc = new Base64();
/*     */ 
/*     */   
/*     */   byte[] dd;
/*     */ 
/*     */   
/*     */   String de;
/*     */ 
/*     */   
/*     */   String df;
/*     */ 
/*     */ 
/*     */   
/*     */   public WXBizMsgCrypt(String token, String encodingAesKey, String receiveid) throws AesException {
/*  57 */     if (encodingAesKey.length() != 43) {
/*  58 */       throw new AesException(-40004);
/*     */     }
/*     */     
/*  61 */     this.de = token;
/*  62 */     this.df = receiveid;
/*  63 */     this.dd = Base64.decodeBase64(encodingAesKey + "=");
/*     */   }
/*     */ 
/*     */   
/*     */   byte[] b(int paramInt) {
/*  68 */     byte[] arrayOfByte = new byte[4];
/*  69 */     arrayOfByte[3] = (byte)(paramInt & 0xFF);
/*  70 */     arrayOfByte[2] = (byte)(paramInt >> 8 & 0xFF);
/*  71 */     arrayOfByte[1] = (byte)(paramInt >> 16 & 0xFF);
/*  72 */     arrayOfByte[0] = (byte)(paramInt >> 24 & 0xFF);
/*  73 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   
/*     */   int f(byte[] paramArrayOfbyte) {
/*  78 */     int i = 0;
/*  79 */     for (byte b = 0; b < 4; b++) {
/*  80 */       i <<= 8;
/*  81 */       i |= paramArrayOfbyte[b] & 0xFF;
/*     */     } 
/*  83 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   String h() {
/*  88 */     String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
/*  89 */     Random random = new Random();
/*  90 */     StringBuffer stringBuffer = new StringBuffer();
/*  91 */     for (byte b = 0; b < 16; b++) {
/*  92 */       int i = random.nextInt(str.length());
/*  93 */       stringBuffer.append(str.charAt(i));
/*     */     } 
/*  95 */     return stringBuffer.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String encrypt(String randomStr, String text) throws AesException {
/* 106 */     a a = new a();
/* 107 */     byte[] arrayOfByte1 = randomStr.getBytes(db);
/* 108 */     byte[] arrayOfByte2 = text.getBytes(db);
/* 109 */     byte[] arrayOfByte3 = b(arrayOfByte2.length);
/* 110 */     byte[] arrayOfByte4 = this.df.getBytes(db);
/*     */ 
/*     */     
/* 113 */     a.g(arrayOfByte1);
/* 114 */     a.g(arrayOfByte3);
/* 115 */     a.g(arrayOfByte2);
/* 116 */     a.g(arrayOfByte4);
/*     */ 
/*     */     
/* 119 */     byte[] arrayOfByte5 = b.c(a.size());
/* 120 */     a.g(arrayOfByte5);
/*     */ 
/*     */     
/* 123 */     byte[] arrayOfByte6 = a.i();
/*     */ 
/*     */     
/*     */     try {
/* 127 */       Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
/* 128 */       SecretKeySpec secretKeySpec = new SecretKeySpec(this.dd, "AES");
/* 129 */       IvParameterSpec ivParameterSpec = new IvParameterSpec(this.dd, 0, 16);
/* 130 */       cipher.init(1, secretKeySpec, ivParameterSpec);
/*     */ 
/*     */       
/* 133 */       byte[] arrayOfByte = cipher.doFinal(arrayOfByte6);
/*     */ 
/*     */       
/* 136 */       return this.dc.encodeToString(arrayOfByte);
/*     */     
/*     */     }
/* 139 */     catch (Exception exception) {
/* 140 */       exception.printStackTrace();
/* 141 */       throw new AesException(-40006);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String q(String paramString) throws AesException {
/*     */     byte[] arrayOfByte;
/*     */     String str1;
/*     */     String str2;
/*     */     try {
/* 156 */       Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
/* 157 */       SecretKeySpec secretKeySpec = new SecretKeySpec(this.dd, "AES");
/* 158 */       IvParameterSpec ivParameterSpec = new IvParameterSpec(Arrays.copyOfRange(this.dd, 0, 16));
/* 159 */       cipher.init(2, secretKeySpec, ivParameterSpec);
/*     */ 
/*     */       
/* 162 */       byte[] arrayOfByte1 = Base64.decodeBase64(paramString);
/*     */ 
/*     */       
/* 165 */       arrayOfByte = cipher.doFinal(arrayOfByte1);
/* 166 */     } catch (Exception exception) {
/* 167 */       exception.printStackTrace();
/* 168 */       throw new AesException(-40007);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 174 */       byte[] arrayOfByte1 = b.h(arrayOfByte);
/*     */ 
/*     */       
/* 177 */       byte[] arrayOfByte2 = Arrays.copyOfRange(arrayOfByte1, 16, 20);
/*     */       
/* 179 */       int i = f(arrayOfByte2);
/*     */       
/* 181 */       str1 = new String(Arrays.copyOfRange(arrayOfByte1, 20, 20 + i), db);
/* 182 */       str2 = new String(Arrays.copyOfRange(arrayOfByte1, 20 + i, arrayOfByte1.length), db);
/*     */     }
/* 184 */     catch (Exception exception) {
/* 185 */       exception.printStackTrace();
/* 186 */       throw new AesException(-40008);
/*     */     } 
/*     */ 
/*     */     
/* 190 */     if (!str2.equals(this.df)) {
/* 191 */       throw new AesException(-40005);
/*     */     }
/* 193 */     return str1;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String EncryptMsg(String replyMsg, String timeStamp, String nonce) throws AesException {
/* 214 */     String str1 = encrypt(h(), replyMsg);
/*     */ 
/*     */     
/* 217 */     if (timeStamp == "") {
/* 218 */       timeStamp = Long.toString(System.currentTimeMillis());
/*     */     }
/*     */     
/* 221 */     String str2 = SHA1.getSHA1(this.de, timeStamp, nonce, str1);
/*     */ 
/*     */ 
/*     */     
/* 225 */     return c.a(str1, str2, timeStamp, nonce);
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
/*     */   public String DecryptMsg(String msgSignature, String timeStamp, String nonce, String postData) throws AesException {
/* 250 */     Object[] arrayOfObject = c.r(postData);
/*     */ 
/*     */     
/* 253 */     String str = SHA1.getSHA1(this.de, timeStamp, nonce, arrayOfObject[1].toString());
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 258 */     if (!str.equals(msgSignature)) {
/* 259 */       throw new AesException(-40001);
/*     */     }
/*     */ 
/*     */     
/* 263 */     return q(arrayOfObject[1].toString());
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
/*     */ 
/*     */ 
/*     */   
/*     */   public String VerifyURL(String msgSignature, String timeStamp, String nonce, String echoStr) throws AesException {
/* 279 */     String str = SHA1.getSHA1(this.de, timeStamp, nonce, echoStr);
/*     */     
/* 281 */     if (!str.equals(msgSignature)) {
/* 282 */       throw new AesException(-40001);
/*     */     }
/*     */     
/* 285 */     return q(echoStr);
/*     */   }
/*     */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\wechat\WXBizMsgCrypt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */