/*     */ package com.fastbee.common.utils;
/*     */ 
/*     */ import com.fastbee.common.exception.ServiceException;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Stack;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CaculateVariableAndNumberUtils
/*     */ {
/*     */   private static final String ar = "+-,*/,(),%";
/*  25 */   private static final Map<String, Integer> as = new HashMap<String, Integer>()
/*     */     {
/*     */     
/*     */     };
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
/*     */   public static BigDecimal execute(String exeStr, Map<String, String> replaceMap) {
/*  42 */     List<String> list = suffixHandle(exeStr);
/*  43 */     System.out.println("计算结果： " + list);
/*  44 */     ArrayList<String> arrayList = new ArrayList();
/*  45 */     for (String str1 : list) {
/*  46 */       String str2 = replaceMap.get(str1);
/*  47 */       if (StringUtils.isNotEmpty(str2)) {
/*  48 */         arrayList.add(str2); continue;
/*     */       } 
/*  50 */       arrayList.add(str1);
/*     */     } 
/*     */     
/*  53 */     return caculateAnalyse(arrayList);
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
/*     */   public static BigDecimal caculateAnalyse(List<String> suffixList) {
/*  65 */     BigDecimal bigDecimal1 = BigDecimal.ZERO;
/*  66 */     BigDecimal bigDecimal2 = BigDecimal.ZERO;
/*     */     
/*  68 */     Stack<BigDecimal> stack = new Stack();
/*  69 */     if (suffixList.size() > 1) {
/*     */       
/*  71 */       for (byte b = 0; b < suffixList.size(); b++) {
/*  72 */         String str = suffixList.get(b);
/*  73 */         if ("+-,*/,(),%".contains(str)) {
/*  74 */           bigDecimal2 = stack.pop();
/*  75 */           bigDecimal1 = stack.pop();
/*  76 */           bigDecimal1 = caculate(bigDecimal1, bigDecimal2, str.toCharArray()[0]);
/*  77 */           stack.push(bigDecimal1);
/*     */         }
/*  79 */         else if (isNumber(suffixList.get(b))) {
/*  80 */           stack.push(new BigDecimal(suffixList.get(b)));
/*     */         } else {
/*  82 */           throw new RuntimeException("公式异常！");
/*     */         }
/*     */       
/*     */       } 
/*  86 */     } else if (suffixList.size() == 1) {
/*  87 */       String str = suffixList.get(0);
/*  88 */       if (isNumber(str)) {
/*  89 */         bigDecimal1 = BigDecimal.valueOf(Double.parseDouble(str));
/*     */       } else {
/*  91 */         throw new RuntimeException("公式异常！");
/*     */       } 
/*     */     } 
/*  94 */     return bigDecimal1;
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
/*     */   public static BigDecimal caculate(BigDecimal a, BigDecimal b, char symbol) {
/*     */     int i;
/* 107 */     switch (symbol) {
/*     */       case '+':
/* 109 */         return a.add(b).stripTrailingZeros();
/*     */       
/*     */       case '-':
/* 112 */         return a.subtract(b).stripTrailingZeros();
/*     */       case '*':
/* 114 */         return a.multiply(b);
/*     */       case '%':
/*     */       case '/':
/* 117 */         i = a(a, b);
/* 118 */         return a.divide(b, i, 4);
/*     */     } 
/* 120 */     throw new RuntimeException("操作符号异常！");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static int a(BigDecimal paramBigDecimal1, BigDecimal paramBigDecimal2) {
/* 126 */     String str1 = paramBigDecimal1.toString();
/* 127 */     String str2 = paramBigDecimal2.toString();
/* 128 */     int i = 0;
/* 129 */     int j = 0;
/* 130 */     if (str1.contains(".")) {
/* 131 */       i = str1.split("\\.")[1].length();
/*     */     }
/* 133 */     if (str2.contains(".")) {
/* 134 */       j = str2.split("\\.")[1].length();
/*     */     }
/* 136 */     if (i == 0 && j == 0) {
/* 137 */       return 2;
/*     */     }
/* 139 */     return Math.max(i, j);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<String> suffixHandle(String exeStr) {
/* 147 */     StringBuilder stringBuilder = new StringBuilder();
/* 148 */     Stack<Character> stack = new Stack();
/* 149 */     char[] arrayOfChar = exeStr.toCharArray();
/* 150 */     ArrayList<String> arrayList = new ArrayList();
/* 151 */     for (char c : arrayOfChar) {
/*     */       
/* 153 */       if ("+-,*/,(),%".indexOf(c) > -1) {
/*     */         
/* 155 */         if (stringBuilder.length() > 0) {
/*     */           
/* 157 */           String str = stringBuilder.toString();
/*     */           
/* 159 */           if (!isVariableAndNumber(str)) {
/* 160 */             throw new RuntimeException(stringBuilder.append("  格式不对").toString());
/*     */           }
/*     */ 
/*     */           
/* 164 */           arrayList.add(str);
/*     */           
/* 166 */           stringBuilder.delete(0, stringBuilder.length());
/*     */         } 
/* 168 */         if (!stack.isEmpty()) {
/*     */ 
/*     */           
/* 171 */           if (c == '(') {
/* 172 */             stack.push(Character.valueOf(c));
/*     */ 
/*     */           
/*     */           }
/* 176 */           else if (c == ')') {
/* 177 */             boolean bool = false;
/* 178 */             while (!stack.isEmpty()) {
/* 179 */               char c1 = ((Character)stack.peek()).charValue();
/* 180 */               if (c1 == '(' && !bool) {
/* 181 */                 stack.pop();
/* 182 */                 bool = true; continue;
/* 183 */               }  if (!bool) {
/* 184 */                 arrayList.add(String.valueOf(stack.pop()));
/*     */               
/*     */               }
/*     */             
/*     */             }
/*     */           
/*     */           }
/*     */           else {
/*     */             
/* 193 */             int i = stack.size();
/* 194 */             while (i > 0) {
/* 195 */               char c1 = ((Character)stack.peek()).charValue();
/* 196 */               if (compare(c1, c) > 0) {
/* 197 */                 arrayList.add(String.valueOf(stack.pop()));
/*     */               }
/* 199 */               i--;
/*     */             } 
/* 201 */             stack.push(Character.valueOf(c));
/*     */           } 
/*     */         } else {
/* 204 */           stack.push(Character.valueOf(c));
/*     */         } 
/*     */       } else {
/* 207 */         stringBuilder.append(c);
/*     */       } 
/*     */     } 
/* 210 */     if (stringBuilder.length() > 0) {
/* 211 */       arrayList.add(stringBuilder.toString());
/*     */     }
/* 213 */     while (!stack.isEmpty()) {
/* 214 */       arrayList.add(String.valueOf(stack.pop()));
/*     */     }
/* 216 */     return arrayList;
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
/*     */   public static int compare(char a, char b) {
/* 228 */     String str1 = String.valueOf(a);
/* 229 */     String str2 = String.valueOf(b);
/* 230 */     Integer integer1 = as.get(str1);
/* 231 */     Integer integer2 = as.get(str2);
/* 232 */     if (null != integer1 && null != integer2) {
/* 233 */       if (integer1.intValue() <= integer2.intValue()) {
/* 234 */         return 1;
/*     */       }
/* 236 */       return -1;
/*     */     } 
/*     */     
/* 239 */     return 0;
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
/*     */   public static boolean isNumber(String str) {
/* 251 */     Pattern pattern = Pattern.compile("[-+]?\\d+(?:\\.\\d+)?");
/* 252 */     Matcher matcher = pattern.matcher(str);
/* 253 */     return matcher.matches();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isVariable(String str) {
/* 263 */     Pattern pattern = Pattern.compile("^[A-Z]+$");
/* 264 */     Matcher matcher = pattern.matcher(str);
/* 265 */     return matcher.matches();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isVariableAndNumber(String str) {
/* 275 */     Pattern pattern = Pattern.compile("[A-Z]|-?\\d+(\\.\\d+)?");
/* 276 */     Matcher matcher = pattern.matcher(str);
/* 277 */     return matcher.matches();
/*     */   }
/*     */   
/*     */   public static String caculateReplace(String str, Map<String, String> map) {
/* 281 */     for (Map.Entry<String, String> entry : map.entrySet()) {
/* 282 */       str = str.replaceAll((String)entry.getKey(), (entry.getValue() == null) ? "1" : (String)entry.getValue());
/*     */     }
/* 284 */     return str;
/*     */   }
/*     */   
/*     */   public static String toFloat(byte[] bytes) throws IOException {
/* 288 */     ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
/* 289 */     DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
/*     */     try {
/* 291 */       float f = dataInputStream.readFloat();
/* 292 */       return String.format("%.6f", new Object[] { Float.valueOf(f) });
/* 293 */     } catch (Exception exception) {
/* 294 */       throw new ServiceException("modbus16转浮点数错误");
/*     */     } finally {
/*     */       
/* 297 */       dataInputStream.close();
/* 298 */       byteArrayInputStream.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toUnSign16(long value) {
/* 308 */     long l = value & 0xFFFFL;
/* 309 */     return l + "";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toSign32_CDAB(long value) {
/* 318 */     byte[] arrayOfByte = intToBytes2((int)value);
/* 319 */     return bytesToInt2(arrayOfByte) + "";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toUnSign32_ABCD(long value) {
/* 328 */     return Integer.toUnsignedString((int)value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toUnSign32_CDAB(long value) {
/* 337 */     byte[] arrayOfByte = intToBytes2((int)value);
/* 338 */     int i = bytesToInt2(arrayOfByte);
/* 339 */     return Integer.toUnsignedString(i);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static float toFloat32_ABCD(byte[] bytes) {
/* 348 */     int i = bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | bytes[3] & 0xFF;
/* 349 */     return Float.intBitsToFloat(i);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Float toFloat32_CDAB(byte[] bytes) {
/* 358 */     int i = (bytes[2] & 0xFF) << 24 | (bytes[3] & 0xFF) << 16 | (bytes[0] & 0xFF) << 8 | bytes[1] & 0xFF;
/* 359 */     return Float.valueOf(Float.intBitsToFloat(i));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int bytesToInt2(byte[] src) {
/* 366 */     return (src[2] & 0xFF) << 24 | (src[3] & 0xFF) << 16 | (src[0] & 0xFF) << 8 | src[1] & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] intToBytes2(int value) {
/* 373 */     byte[] arrayOfByte = new byte[4];
/* 374 */     arrayOfByte[0] = (byte)(value >> 24 & 0xFF);
/* 375 */     arrayOfByte[1] = (byte)(value >> 16 & 0xFF);
/* 376 */     arrayOfByte[2] = (byte)(value >> 8 & 0xFF);
/* 377 */     arrayOfByte[3] = (byte)(value & 0xFF);
/* 378 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   
/*     */   public static String subHexValue(String hexString) {
/* 383 */     String str = hexString.substring(4, 6);
/* 384 */     int i = Integer.parseInt(str);
/* 385 */     return hexString.substring(6, 6 + i * 2);
/*     */   }
/*     */ 
/*     */   
/*     */   public static void main(String[] args) throws IOException {
/* 390 */     String str1 = "A/B*C";
/* 391 */     String str2 = "E-((A+B)-(C+D))%10";
/* 392 */     String str3 = "A-B-C*(D-E)+10*5";
/* 393 */     String str4 = "A-B-C*(D+E)-(A+B)+(2+3)";
/* 394 */     String str5 = "A-(A-(B-C)*(D+E))%10+B";
/* 395 */     String str6 = "A-(B+C)*D+10";
/* 396 */     String str7 = "1+2*3-2+2*(1-2+3*4+5-6/2+(2-1)+3*4-2)%10";
/*     */ 
/*     */     
/* 399 */     boolean bool = isNumber("-10");
/* 400 */     System.out.println(bool);
/*     */     
/* 402 */     HashMap<Object, Object> hashMap = new HashMap<>();
/* 403 */     hashMap.put("A", "1");
/* 404 */     hashMap.put("B", "2");
/* 405 */     hashMap.put("C", "3");
/* 406 */     hashMap.put("D", "4");
/* 407 */     hashMap.put("E", "10");
/* 408 */     BigDecimal bigDecimal = execute(str7, (Map)hashMap);
/* 409 */     System.out.println(bigDecimal);
/*     */   }
/*     */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\CaculateVariableAndNumberUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */