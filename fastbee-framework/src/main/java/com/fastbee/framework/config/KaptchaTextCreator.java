/*    */ package com.fastbee.framework.config;
/*    */ 
/*    */ import com.google.code.kaptcha.text.impl.DefaultTextCreator;
/*    */ import java.util.Random;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class KaptchaTextCreator
/*    */   extends DefaultTextCreator
/*    */ {
/* 13 */   private static final String[] CNUMBERS = "0,1,2,3,4,5,6,7,8,9,10".split(",");
/*    */ 
/*    */ 
/*    */   
/*    */   public String getText() {
/* 18 */     Integer result = Integer.valueOf(0);
/* 19 */     Random random = new Random();
/* 20 */     int x = random.nextInt(10);
/* 21 */     int y = random.nextInt(10);
/* 22 */     StringBuilder suChinese = new StringBuilder();
/* 23 */     int randomoperands = random.nextInt(3);
/* 24 */     if (randomoperands == 0) {
/*    */       
/* 26 */       result = Integer.valueOf(x * y);
/* 27 */       suChinese.append(CNUMBERS[x]);
/* 28 */       suChinese.append("*");
/* 29 */       suChinese.append(CNUMBERS[y]);
/*    */     }
/* 31 */     else if (randomoperands == 1) {
/*    */       
/* 33 */       if (x != 0 && y % x == 0)
/*    */       {
/* 35 */         result = Integer.valueOf(y / x);
/* 36 */         suChinese.append(CNUMBERS[y]);
/* 37 */         suChinese.append("/");
/* 38 */         suChinese.append(CNUMBERS[x]);
/*    */       }
/*    */       else
/*    */       {
/* 42 */         result = Integer.valueOf(x + y);
/* 43 */         suChinese.append(CNUMBERS[x]);
/* 44 */         suChinese.append("+");
/* 45 */         suChinese.append(CNUMBERS[y]);
/*    */       
/*    */       }
/*    */     
/*    */     }
/* 50 */     else if (x >= y) {
/*    */       
/* 52 */       result = Integer.valueOf(x - y);
/* 53 */       suChinese.append(CNUMBERS[x]);
/* 54 */       suChinese.append("-");
/* 55 */       suChinese.append(CNUMBERS[y]);
/*    */     }
/*    */     else {
/*    */       
/* 59 */       result = Integer.valueOf(y - x);
/* 60 */       suChinese.append(CNUMBERS[y]);
/* 61 */       suChinese.append("-");
/* 62 */       suChinese.append(CNUMBERS[x]);
/*    */     } 
/*    */     
/* 65 */     suChinese.append("=?@" + result);
/* 66 */     return suChinese.toString();
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-framework\fastbee-framework-2.6.0.jar!\com\fastbee\framework\config\KaptchaTextCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */