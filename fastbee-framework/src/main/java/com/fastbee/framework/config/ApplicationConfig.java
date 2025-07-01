/*    */ package com.fastbee.framework.config;
/*    */ 
/*    */ import java.util.TimeZone;
/*    */ import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
/*    */ import org.springframework.context.annotation.Bean;
/*    */ import org.springframework.context.annotation.Configuration;
/*    */ import org.springframework.context.annotation.EnableAspectJAutoProxy;
/*    */ import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
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
/*    */ @Configuration
/*    */ @EnableAspectJAutoProxy(exposeProxy = true)
/*    */ public class ApplicationConfig
/*    */ {
/*    */   @Bean
/*    */   public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization() {
/* 25 */     return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.timeZone(TimeZone.getDefault());
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-framework\fastbee-framework-2.6.0.jar!\com\fastbee\framework\config\ApplicationConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */