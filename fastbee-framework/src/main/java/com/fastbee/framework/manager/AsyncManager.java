/*    */ package com.fastbee.framework.manager;
/*    */ 
/*    */ import com.fastbee.common.utils.Threads;
/*    */ import com.fastbee.common.utils.spring.SpringUtils;
/*    */ import java.util.TimerTask;
/*    */ import java.util.concurrent.ScheduledExecutorService;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AsyncManager
/*    */ {
/* 19 */   private final int OPERATE_DELAY_TIME = 10;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 24 */   private ScheduledExecutorService executor = (ScheduledExecutorService)SpringUtils.getBean("scheduledExecutorService");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 31 */   private static AsyncManager me = new AsyncManager();
/*    */ 
/*    */   
/*    */   public static AsyncManager me() {
/* 35 */     return me;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void execute(TimerTask task) {
/* 45 */     this.executor.schedule(task, 10L, TimeUnit.MILLISECONDS);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void shutdown() {
/* 53 */     Threads.shutdownAndAwaitTermination(this.executor);
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-framework\fastbee-framework-2.6.0.jar!\com\fastbee\framework\manager\AsyncManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */