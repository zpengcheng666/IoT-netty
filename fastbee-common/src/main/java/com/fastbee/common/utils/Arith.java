/*     */ package com.fastbee.common.utils;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
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
/*     */ public class Arith
/*     */ {
/*     */   private static final int al = 10;
/*     */   
/*     */   public static double add(double v1, double v2) {
/*  30 */     BigDecimal bigDecimal1 = new BigDecimal(Double.toString(v1));
/*  31 */     BigDecimal bigDecimal2 = new BigDecimal(Double.toString(v2));
/*  32 */     return bigDecimal1.add(bigDecimal2).doubleValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double sub(double v1, double v2) {
/*  43 */     BigDecimal bigDecimal1 = new BigDecimal(Double.toString(v1));
/*  44 */     BigDecimal bigDecimal2 = new BigDecimal(Double.toString(v2));
/*  45 */     return bigDecimal1.subtract(bigDecimal2).doubleValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double mul(double v1, double v2) {
/*  56 */     BigDecimal bigDecimal1 = new BigDecimal(Double.toString(v1));
/*  57 */     BigDecimal bigDecimal2 = new BigDecimal(Double.toString(v2));
/*  58 */     return bigDecimal1.multiply(bigDecimal2).doubleValue();
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
/*     */   public static double div(double v1, double v2) {
/*  70 */     return div(v1, v2, 10);
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
/*     */   public static double div(double v1, double v2, int scale) {
/*  83 */     if (scale < 0)
/*     */     {
/*  85 */       throw new IllegalArgumentException("The scale must be a positive integer or zero");
/*     */     }
/*     */     
/*  88 */     BigDecimal bigDecimal1 = new BigDecimal(Double.toString(v1));
/*  89 */     BigDecimal bigDecimal2 = new BigDecimal(Double.toString(v2));
/*  90 */     if (bigDecimal1.compareTo(BigDecimal.ZERO) == 0)
/*     */     {
/*  92 */       return BigDecimal.ZERO.doubleValue();
/*     */     }
/*  94 */     return bigDecimal1.divide(bigDecimal2, scale, RoundingMode.HALF_UP).doubleValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double round(double v, int scale) {
/* 105 */     if (scale < 0)
/*     */     {
/* 107 */       throw new IllegalArgumentException("The scale must be a positive integer or zero");
/*     */     }
/*     */     
/* 110 */     BigDecimal bigDecimal1 = new BigDecimal(Double.toString(v));
/* 111 */     BigDecimal bigDecimal2 = BigDecimal.ONE;
/* 112 */     return bigDecimal1.divide(bigDecimal2, scale, RoundingMode.HALF_UP).doubleValue();
/*     */   }
/*     */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\Arith.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */