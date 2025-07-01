/*    */ package com.fastbee.common.utils;
/*    */ 
/*    */ import java.io.PrintWriter;
/*    */ import java.io.StringWriter;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ExceptionUtils
/*    */ {
/*    */   public static Throwable getThrowable(HttpServletRequest request) {
/* 14 */     Throwable throwable = null;
/* 15 */     if (request.getAttribute("exception") != null) {
/* 16 */       throwable = (Throwable)request.getAttribute("exception");
/* 17 */     } else if (request.getAttribute("javax.servlet.error.exception") != null) {
/* 18 */       throwable = (Throwable)request.getAttribute("javax.servlet.error.exception");
/*    */     } 
/*    */     
/* 21 */     return throwable;
/*    */   }
/*    */   
/*    */   public static String getStackTraceAsString(Throwable e) {
/* 25 */     if (e == null) {
/* 26 */       return "";
/*    */     }
/* 28 */     StringWriter stringWriter = new StringWriter();
/* 29 */     e.printStackTrace(new PrintWriter(stringWriter));
/* 30 */     return stringWriter.toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public static boolean isCausedBy(Exception ex, Class... causeExceptionClasses) {
/* 35 */     for (Throwable throwable = ex.getCause(); throwable != null; throwable = throwable.getCause()) {
/* 36 */       Class[] arrayOfClass = causeExceptionClasses;
/* 37 */       int i = causeExceptionClasses.length;
/*    */       
/* 39 */       for (byte b = 0; b < i; b++) {
/* 40 */         Class clazz = arrayOfClass[b];
/* 41 */         if (clazz.isInstance(throwable)) {
/* 42 */           return true;
/*    */         }
/*    */       } 
/*    */     } 
/*    */     
/* 47 */     return false;
/*    */   }
/*    */   
/*    */   public static RuntimeException unchecked(Exception e) {
/* 51 */     return (e instanceof RuntimeException) ? (RuntimeException)e : new RuntimeException(e);
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\ExceptionUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */