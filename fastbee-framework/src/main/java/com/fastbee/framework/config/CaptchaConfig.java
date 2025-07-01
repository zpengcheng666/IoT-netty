/*    */ package com.fastbee.framework.config;
/*    */ 
/*    */ import com.google.code.kaptcha.impl.DefaultKaptcha;
/*    */ import com.google.code.kaptcha.util.Config;
/*    */ import java.util.Properties;
/*    */ import org.springframework.context.annotation.Bean;
/*    */ import org.springframework.context.annotation.Configuration;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Configuration
/*    */ public class CaptchaConfig
/*    */ {
/*    */   @Bean(name = {"captchaProducer"})
/*    */   public DefaultKaptcha getKaptchaBean() {
/* 21 */     DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
/* 22 */     Properties properties = new Properties();
/*    */     
/* 24 */     properties.setProperty("kaptcha.border", "yes");
/*    */     
/* 26 */     properties.setProperty("kaptcha.textproducer.font.color", "black");
/*    */     
/* 28 */     properties.setProperty("kaptcha.image.width", "160");
/*    */     
/* 30 */     properties.setProperty("kaptcha.image.height", "60");
/*    */     
/* 32 */     properties.setProperty("kaptcha.textproducer.font.size", "38");
/*    */     
/* 34 */     properties.setProperty("kaptcha.session.key", "kaptchaCode");
/*    */     
/* 36 */     properties.setProperty("kaptcha.textproducer.char.length", "4");
/*    */     
/* 38 */     properties.setProperty("kaptcha.textproducer.font.names", "Arial,Courier");
/*    */     
/* 40 */     properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.ShadowGimpy");
/* 41 */     Config config = new Config(properties);
/* 42 */     defaultKaptcha.setConfig(config);
/* 43 */     return defaultKaptcha;
/*    */   }
/*    */ 
/*    */   
/*    */   @Bean(name = {"captchaProducerMath"})
/*    */   public DefaultKaptcha getKaptchaBeanMath() {
/* 49 */     DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
/* 50 */     Properties properties = new Properties();
/*    */     
/* 52 */     properties.setProperty("kaptcha.border", "yes");
/*    */     
/* 54 */     properties.setProperty("kaptcha.border.color", "105,179,90");
/*    */     
/* 56 */     properties.setProperty("kaptcha.textproducer.font.color", "blue");
/*    */     
/* 58 */     properties.setProperty("kaptcha.image.width", "160");
/*    */     
/* 60 */     properties.setProperty("kaptcha.image.height", "60");
/*    */     
/* 62 */     properties.setProperty("kaptcha.textproducer.font.size", "35");
/*    */     
/* 64 */     properties.setProperty("kaptcha.session.key", "kaptchaCodeMath");
/*    */     
/* 66 */     properties.setProperty("kaptcha.textproducer.impl", "com.fastbee.framework.config.KaptchaTextCreator");
/*    */     
/* 68 */     properties.setProperty("kaptcha.textproducer.char.space", "3");
/*    */     
/* 70 */     properties.setProperty("kaptcha.textproducer.char.length", "6");
/*    */     
/* 72 */     properties.setProperty("kaptcha.textproducer.font.names", "Arial,Courier");
/*    */     
/* 74 */     properties.setProperty("kaptcha.noise.color", "white");
/*    */     
/* 76 */     properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
/*    */     
/* 78 */     properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.ShadowGimpy");
/* 79 */     Config config = new Config(properties);
/* 80 */     defaultKaptcha.setConfig(config);
/* 81 */     return defaultKaptcha;
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-framework\fastbee-framework-2.6.0.jar!\com\fastbee\framework\config\CaptchaConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */