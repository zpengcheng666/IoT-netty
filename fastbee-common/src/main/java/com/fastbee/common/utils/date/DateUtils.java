/*     */ package com.fastbee.common.utils.date;
/*     */ 
/*     */ import cn.hutool.core.date.LocalDateTimeUtil;
/*     */ import java.time.Duration;
/*     */ import java.time.Instant;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.ZoneId;
/*     */ import java.time.ZonedDateTime;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
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
/*     */ public class DateUtils
/*     */ {
/*     */   public static final String TIME_ZONE_DEFAULT = "GMT+8";
/*     */   public static final long SECOND_MILLIS = 1000L;
/*     */   public static final String FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = "yyyy-MM-dd HH:mm:ss";
/*     */   public static final String FORMAT_HOUR_MINUTE_SECOND = "HH:mm:ss";
/*     */   
/*     */   public static Date of(LocalDateTime date) {
/*  38 */     ZonedDateTime zonedDateTime = date.atZone(ZoneId.systemDefault());
/*     */     
/*  40 */     Instant instant = zonedDateTime.toInstant();
/*     */     
/*  42 */     return Date.from(instant);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDateTime of(Date date) {
/*  53 */     Instant instant = date.toInstant();
/*     */     
/*  55 */     return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static Date addTime(Duration duration) {
/*  60 */     return new Date(System.currentTimeMillis() + duration.toMillis());
/*     */   }
/*     */   
/*     */   public static boolean isExpired(Date time) {
/*  64 */     return (System.currentTimeMillis() > time.getTime());
/*     */   }
/*     */   
/*     */   public static boolean isExpired(LocalDateTime time) {
/*  68 */     LocalDateTime localDateTime = LocalDateTime.now();
/*  69 */     return localDateTime.isAfter(time);
/*     */   }
/*     */   
/*     */   public static long diff(Date endTime, Date startTime) {
/*  73 */     return endTime.getTime() - startTime.getTime();
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
/*     */   public static Date buildTime(int year, int mouth, int day) {
/*  85 */     return buildTime(year, mouth, day, 0, 0, 0);
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
/*     */   public static Date buildTime(int year, int mouth, int day, int hour, int minute, int second) {
/* 101 */     Calendar calendar = Calendar.getInstance();
/* 102 */     calendar.set(1, year);
/* 103 */     calendar.set(2, mouth - 1);
/* 104 */     calendar.set(5, day);
/* 105 */     calendar.set(11, hour);
/* 106 */     calendar.set(12, minute);
/* 107 */     calendar.set(13, second);
/* 108 */     calendar.set(14, 0);
/* 109 */     return calendar.getTime();
/*     */   }
/*     */   
/*     */   public static Date max(Date a, Date b) {
/* 113 */     if (a == null) {
/* 114 */       return b;
/*     */     }
/* 116 */     if (b == null) {
/* 117 */       return a;
/*     */     }
/* 119 */     return (a.compareTo(b) > 0) ? a : b;
/*     */   }
/*     */   
/*     */   public static LocalDateTime max(LocalDateTime a, LocalDateTime b) {
/* 123 */     if (a == null) {
/* 124 */       return b;
/*     */     }
/* 126 */     if (b == null) {
/* 127 */       return a;
/*     */     }
/* 129 */     return a.isAfter(b) ? a : b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Date addDate(int field, int amount) {
/* 140 */     return addDate(null, field, amount);
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
/*     */   public static Date addDate(Date date, int field, int amount) {
/* 152 */     if (amount == 0) {
/* 153 */       return date;
/*     */     }
/* 155 */     Calendar calendar = Calendar.getInstance();
/* 156 */     if (date != null) {
/* 157 */       calendar.setTime(date);
/*     */     }
/* 159 */     calendar.add(field, amount);
/* 160 */     return calendar.getTime();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isToday(LocalDateTime date) {
/* 170 */     return LocalDateTimeUtil.isSameDay(date, LocalDateTime.now());
/*     */   }
/*     */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\date\DateUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */