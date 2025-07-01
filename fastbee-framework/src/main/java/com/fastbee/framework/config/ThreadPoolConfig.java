/*    */ package com.fastbee.framework.config;
/*    */ 
/*    */ import com.fastbee.common.utils.Threads;
/*    */ import java.util.concurrent.RejectedExecutionHandler;
/*    */ import java.util.concurrent.ScheduledExecutorService;
/*    */ import java.util.concurrent.ScheduledThreadPoolExecutor;
/*    */ import java.util.concurrent.ThreadFactory;
/*    */ import java.util.concurrent.ThreadPoolExecutor;
/*    */ import org.apache.commons.lang3.concurrent.BasicThreadFactory;
/*    */ import org.springframework.context.annotation.Bean;
/*    */ import org.springframework.context.annotation.Configuration;
/*    */ import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Configuration
/*    */ public class ThreadPoolConfig
/*    */ {
/* 21 */   private int corePoolSize = 50;
/*    */ 
/*    */   
/* 24 */   private int maxPoolSize = 200;
/*    */ 
/*    */   
/* 27 */   private int queueCapacity = 1000;
/*    */ 
/*    */   
/* 30 */   private int keepAliveSeconds = 300;
/*    */ 
/*    */   
/*    */   @Bean(name = {"threadPoolTaskExecutor"})
/*    */   public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
/* 35 */     ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
/* 36 */     executor.setMaxPoolSize(this.maxPoolSize);
/* 37 */     executor.setCorePoolSize(this.corePoolSize);
/* 38 */     executor.setQueueCapacity(this.queueCapacity);
/* 39 */     executor.setKeepAliveSeconds(this.keepAliveSeconds);
/*    */     
/* 41 */     executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
/* 42 */     return executor;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Bean(name = {"scheduledExecutorService"})
/*    */   protected ScheduledExecutorService scheduledExecutorService() {
/* 51 */     return new ScheduledThreadPoolExecutor(this.corePoolSize, (ThreadFactory)(new BasicThreadFactory.Builder())
/* 52 */         .namingPattern("schedule-pool-%d").daemon(true).build(), new ThreadPoolExecutor.CallerRunsPolicy())
/*    */       {
/*    */ 
/*    */         
/*    */         protected void afterExecute(Runnable r, Throwable t)
/*    */         {
/* 58 */           super.afterExecute(r, t);
/* 59 */           Threads.printException(r, t);
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-framework\fastbee-framework-2.6.0.jar!\com\fastbee\framework\config\ThreadPoolConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */