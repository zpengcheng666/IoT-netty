/*    */ package com.fastbee.framework.config.sharding;
/*    */ 
/*    */ import org.springframework.boot.CommandLineRunner;
/*    */ import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
/*    */ import org.springframework.stereotype.Component;
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
/*    */ 
/*    */ 
/*    */ @Component
/*    */ @ConditionalOnProperty(prefix = "spring.shardingsphere", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
/*    */ public class ShardingTablesLoadRunner
/*    */   implements CommandLineRunner
/*    */ {
/*    */   public void run(String... args) {
/* 24 */     ShardingAlgorithmTool.tableNameCacheReloadAll();
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-framework\fastbee-framework-2.6.0.jar!\com\fastbee\framework\config\sharding\ShardingTablesLoadRunner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */