/*    */ package com.fastbee.common.utils;
/*    */ 
/*    */ import java.io.PrintWriter;
/*    */ import java.io.StringWriter;
/*    */ import org.apache.commons.lang3.exception.ExceptionUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ExceptionUtil
/*    */ {
/*    */   public static String getExceptionMessage(Throwable e) {
/* 19 */     StringWriter stringWriter = new StringWriter();
/* 20 */     e.printStackTrace(new PrintWriter(stringWriter, true));
/* 21 */     return stringWriter.toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public static String getRootErrorMessage(Exception e) {
/* 26 */     Throwable throwable = ExceptionUtils.getRootCause(e);
/* 27 */     throwable = (throwable == null) ? e : throwable;
/* 28 */     if (throwable == null)
/*    */     {
/* 30 */       return "";
/*    */     }
/* 32 */     String str = throwable.getMessage();
/* 33 */     if (str == null)
/*    */     {
/* 35 */       return "null";
/*    */     }
/* 37 */     return StringUtils.defaultString(str);
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\ExceptionUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */