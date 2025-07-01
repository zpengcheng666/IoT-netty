/*    */ package com.fastbee.common.utils.date;
/*    */ 
/*    */ import cn.hutool.core.date.LocalDateTimeUtil;
/*    */ import java.time.Duration;
/*    */ import java.time.LocalDateTime;
/*    */ import java.time.format.DateTimeFormatter;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LocalDateTimeUtils
/*    */ {
/* 16 */   public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 21 */   public static LocalDateTime EMPTY = buildTime(1970, 1, 1);
/*    */   
/*    */   public static LocalDateTime addTime(Duration duration) {
/* 24 */     return LocalDateTime.now().plus(duration);
/*    */   }
/*    */   
/*    */   public static boolean beforeNow(LocalDateTime date) {
/* 28 */     return date.isBefore(LocalDateTime.now());
/*    */   }
/*    */   
/*    */   public static boolean afterNow(LocalDateTime date) {
/* 32 */     return date.isAfter(LocalDateTime.now());
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
/*    */   public static LocalDateTime buildTime(int year, int mouth, int day) {
/* 44 */     return LocalDateTime.of(year, mouth, day, 0, 0, 0);
/*    */   }
/*    */ 
/*    */   
/*    */   public static LocalDateTime[] buildBetweenTime(int year1, int mouth1, int day1, int year2, int mouth2, int day2) {
/* 49 */     return new LocalDateTime[] { buildTime(year1, mouth1, day1), buildTime(year2, mouth2, day2) };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isBetween(LocalDateTime startTime, LocalDateTime endTime) {
/* 60 */     if (startTime == null || endTime == null) {
/* 61 */       return false;
/*    */     }
/* 63 */     return LocalDateTimeUtil.isIn(LocalDateTime.now(), startTime, endTime);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String localDateTimeToStr(LocalDateTime localDateTime, String format) {
/* 73 */     DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
/* 74 */     return localDateTime.format(dateTimeFormatter);
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\date\LocalDateTimeUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */