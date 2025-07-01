/*    */ package com.fastbee.framework.datasource;
/*    */ 
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DynamicDataSourceContextHolder
/*    */ {
/* 13 */   public static final Logger log = LoggerFactory.getLogger(DynamicDataSourceContextHolder.class);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 19 */   private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void setDataSourceType(String dsType) {
/* 26 */     log.info("切换到{}数据源", dsType);
/* 27 */     CONTEXT_HOLDER.set(dsType);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getDataSourceType() {
/* 35 */     return CONTEXT_HOLDER.get();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void clearDataSourceType() {
/* 43 */     CONTEXT_HOLDER.remove();
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-framework\fastbee-framework-2.6.0.jar!\com\fastbee\framework\datasource\DynamicDataSourceContextHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */