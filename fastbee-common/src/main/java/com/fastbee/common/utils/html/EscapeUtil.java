/*     */ package com.fastbee.common.utils.html;
/*     */ 
/*     */ import com.fastbee.common.utils.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class EscapeUtil
/*     */ {
/*     */   public static final String RE_HTML_MARK = "(<[^<]*?>)|(<[\\s]*?/[^<]*?>)|(<[^<]*?/[\\s]*?>)";
/*  14 */   private static final char[][] bg = new char[64][];
/*     */ 
/*     */   
/*     */   static {
/*  18 */     for (byte b = 0; b < 64; b++) {
/*     */       
/*  20 */       (new char[1])[0] = (char)b; bg[b] = new char[1];
/*     */     } 
/*     */ 
/*     */     
/*  24 */     bg[39] = "&#039;".toCharArray();
/*  25 */     bg[34] = "&#34;".toCharArray();
/*  26 */     bg[38] = "&#38;".toCharArray();
/*  27 */     bg[60] = "&#60;".toCharArray();
/*  28 */     bg[62] = "&#62;".toCharArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String escape(String text) {
/*  39 */     return c(text);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String unescape(String content) {
/*  50 */     return decode(content);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String clean(String content) {
/*  61 */     return (new HTMLFilter()).filter(content);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String c(String paramString) {
/*  72 */     if (StringUtils.isEmpty(paramString))
/*     */     {
/*  74 */       return "";
/*     */     }
/*     */     
/*  77 */     StringBuilder stringBuilder = new StringBuilder(paramString.length() * 6);
/*     */     
/*  79 */     for (byte b = 0; b < paramString.length(); b++) {
/*     */       
/*  81 */       char c = paramString.charAt(b);
/*  82 */       if (c < 'Ā') {
/*     */         
/*  84 */         stringBuilder.append("%");
/*  85 */         if (c < '\020')
/*     */         {
/*  87 */           stringBuilder.append("0");
/*     */         }
/*  89 */         stringBuilder.append(Integer.toString(c, 16));
/*     */       }
/*     */       else {
/*     */         
/*  93 */         stringBuilder.append("%u");
/*  94 */         if (c <= '࿿')
/*     */         {
/*     */           
/*  97 */           stringBuilder.append("0");
/*     */         }
/*  99 */         stringBuilder.append(Integer.toString(c, 16));
/*     */       } 
/*     */     } 
/* 102 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String decode(String content) {
/* 113 */     if (StringUtils.isEmpty(content))
/*     */     {
/* 115 */       return content;
/*     */     }
/*     */     
/* 118 */     StringBuilder stringBuilder = new StringBuilder(content.length());
/* 119 */     int i = 0, j = 0;
/*     */     
/* 121 */     while (i < content.length()) {
/*     */       
/* 123 */       j = content.indexOf("%", i);
/* 124 */       if (j == i) {
/*     */         
/* 126 */         if (content.charAt(j + 1) == 'u') {
/*     */           
/* 128 */           char c1 = (char)Integer.parseInt(content.substring(j + 2, j + 6), 16);
/* 129 */           stringBuilder.append(c1);
/* 130 */           i = j + 6;
/*     */           
/*     */           continue;
/*     */         } 
/* 134 */         char c = (char)Integer.parseInt(content.substring(j + 1, j + 3), 16);
/* 135 */         stringBuilder.append(c);
/* 136 */         i = j + 3;
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 141 */       if (j == -1) {
/*     */         
/* 143 */         stringBuilder.append(content.substring(i));
/* 144 */         i = content.length();
/*     */         
/*     */         continue;
/*     */       } 
/* 148 */       stringBuilder.append(content.substring(i, j));
/* 149 */       i = j;
/*     */     } 
/*     */ 
/*     */     
/* 153 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public static void main(String[] args) {
/* 158 */     String str1 = "<script>alert(1);</script>";
/* 159 */     String str2 = escape(str1);
/*     */ 
/*     */ 
/*     */     
/* 163 */     System.out.println("clean: " + clean(str1));
/* 164 */     System.out.println("escape: " + str2);
/* 165 */     System.out.println("unescape: " + unescape(str2));
/*     */   }
/*     */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\html\EscapeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */