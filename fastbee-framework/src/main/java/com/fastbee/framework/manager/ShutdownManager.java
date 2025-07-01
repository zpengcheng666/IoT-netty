/*    */ package com.fastbee.framework.manager;
/*    */ 
/*    */ import javax.annotation.PreDestroy;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ import org.springframework.stereotype.Component;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Component
/*    */ public class ShutdownManager
/*    */ {
/* 16 */   private static final Logger logger = LoggerFactory.getLogger("sys-user");
/*    */ 
/*    */   
/*    */   @PreDestroy
/*    */   public void destroy() {
/* 21 */     shutdownAsyncManager();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void shutdownAsyncManager() {
/*    */     try {
/* 31 */       logger.info("====关闭后台任务任务线程池====");
/* 32 */       AsyncManager.me().shutdown();
/*    */     }
/* 34 */     catch (Exception e) {
/*    */       
/* 36 */       logger.error(e.getMessage(), e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-framework\fastbee-framework-2.6.0.jar!\com\fastbee\framework\manager\ShutdownManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */