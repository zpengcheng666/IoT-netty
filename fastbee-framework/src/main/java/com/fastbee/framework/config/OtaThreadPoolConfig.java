/*    */ package com.fastbee.framework.config;
/*    */ 
/*    */ import java.util.concurrent.ThreadPoolExecutor;
/*    */ import org.springframework.context.annotation.Bean;
/*    */ import org.springframework.context.annotation.Configuration;
/*    */ import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Configuration
/*    */ public class OtaThreadPoolConfig
/*    */ {
/* 14 */   private int corePoolSize = 10;
/*    */   
/* 16 */   private int maxPoolSize = 20;
/*    */   
/* 18 */   private int queueCapacity = 10000;
/*    */   
/* 20 */   private int keepAliveSeconds = 30;
/*    */   
/*    */   private static final String threadNamePrefix = "ota-";
/*    */ 
/*    */   
/*    */   @Bean(name = {"otaThreadPoolTaskExecutor"})
/*    */   public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
/* 27 */     ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
/* 28 */     executor.setMaxPoolSize(this.maxPoolSize);
/* 29 */     executor.setCorePoolSize(this.corePoolSize);
/* 30 */     executor.setQueueCapacity(this.queueCapacity);
/* 31 */     executor.setKeepAliveSeconds(this.keepAliveSeconds);
/* 32 */     executor.setThreadNamePrefix("ota-");
/*    */     
/* 34 */     executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
/*    */     
/* 36 */     executor.initialize();
/* 37 */     return executor;
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-framework\fastbee-framework-2.6.0.jar!\com\fastbee\framework\config\OtaThreadPoolConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */