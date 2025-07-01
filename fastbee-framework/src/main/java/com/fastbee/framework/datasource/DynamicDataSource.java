/*    */ package com.fastbee.framework.datasource;
/*    */ 
/*    */ import java.util.Map;
/*    */ import javax.sql.DataSource;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DynamicDataSource
/*    */   extends AbstractRoutingDataSource
/*    */ {
/* 14 */   private static final Logger log = LoggerFactory.getLogger(DynamicDataSource.class);
/*    */ 
/*    */ 
/*    */   
/*    */   public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources) {
/* 19 */     setDefaultTargetDataSource(defaultTargetDataSource);
/* 20 */     setTargetDataSources(targetDataSources);
/* 21 */     afterPropertiesSet();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected Object determineCurrentLookupKey() {
/* 28 */     return DynamicDataSourceContextHolder.getDataSourceType();
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-framework\fastbee-framework-2.6.0.jar!\com\fastbee\framework\datasource\DynamicDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */