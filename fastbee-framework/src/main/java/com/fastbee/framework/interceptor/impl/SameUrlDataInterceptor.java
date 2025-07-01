/*     */ package com.fastbee.framework.interceptor.impl;
/*     */ 
/*     */ import com.alibaba.fastjson2.JSON;
/*     */ import com.fastbee.common.annotation.RepeatSubmit;
/*     */ import com.fastbee.common.core.redis.RedisCache;
/*     */ import com.fastbee.common.filter.RepeatedlyRequestWrapper;
/*     */ import com.fastbee.common.utils.StringUtils;
/*     */ import com.fastbee.common.utils.http.HttpHelper;
/*     */ import com.fastbee.framework.interceptor.RepeatSubmitInterceptor;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import org.springframework.beans.factory.annotation.Autowired;
/*     */ import org.springframework.beans.factory.annotation.Value;
/*     */ import org.springframework.stereotype.Component;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Component
/*     */ public class SameUrlDataInterceptor
/*     */   extends RepeatSubmitInterceptor
/*     */ {
/*  28 */   public final String REPEAT_PARAMS = "repeatParams";
/*     */   
/*  30 */   public final String REPEAT_TIME = "repeatTime";
/*     */ 
/*     */   
/*     */   @Value("${token.header}")
/*     */   private String header;
/*     */ 
/*     */   
/*     */   @Autowired
/*     */   private RedisCache redisCache;
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit annotation) {
/*  43 */     String nowParams = "";
/*  44 */     if (request instanceof RepeatedlyRequestWrapper) {
/*     */       
/*  46 */       RepeatedlyRequestWrapper repeatedlyRequest = (RepeatedlyRequestWrapper)request;
/*  47 */       nowParams = HttpHelper.getBodyString((ServletRequest)repeatedlyRequest);
/*     */     } 
/*     */ 
/*     */     
/*  51 */     if (StringUtils.isEmpty(nowParams))
/*     */     {
/*  53 */       nowParams = JSON.toJSONString(request.getParameterMap());
/*     */     }
/*  55 */     Map<String, Object> nowDataMap = new HashMap<>();
/*  56 */     nowDataMap.put("repeatParams", nowParams);
/*  57 */     nowDataMap.put("repeatTime", Long.valueOf(System.currentTimeMillis()));
/*     */ 
/*     */     
/*  60 */     String url = request.getRequestURI();
/*     */ 
/*     */     
/*  63 */     String submitKey = StringUtils.trimToEmpty(request.getHeader(this.header));
/*     */ 
/*     */     
/*  66 */     String cacheRepeatKey = "repeat_submit:" + url + submitKey;
/*     */     
/*  68 */     Object sessionObj = this.redisCache.getCacheObject(cacheRepeatKey);
/*  69 */     if (sessionObj != null) {
/*     */       
/*  71 */       Map<String, Object> sessionMap = (Map<String, Object>)sessionObj;
/*  72 */       if (sessionMap.containsKey(url)) {
/*     */         
/*  74 */         Map<String, Object> preDataMap = (Map<String, Object>)sessionMap.get(url);
/*  75 */         if (compareParams(nowDataMap, preDataMap) && compareTime(nowDataMap, preDataMap, annotation.interval()))
/*     */         {
/*  77 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/*  81 */     Map<String, Object> cacheMap = new HashMap<>();
/*  82 */     cacheMap.put(url, nowDataMap);
/*  83 */     this.redisCache.setCacheObject(cacheRepeatKey, cacheMap, Integer.valueOf(annotation.interval()), TimeUnit.MILLISECONDS);
/*  84 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean compareParams(Map<String, Object> nowMap, Map<String, Object> preMap) {
/*  92 */     String nowParams = (String)nowMap.get("repeatParams");
/*  93 */     String preParams = (String)preMap.get("repeatParams");
/*  94 */     return nowParams.equals(preParams);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean compareTime(Map<String, Object> nowMap, Map<String, Object> preMap, int interval) {
/* 102 */     long time1 = ((Long)nowMap.get("repeatTime")).longValue();
/* 103 */     long time2 = ((Long)preMap.get("repeatTime")).longValue();
/* 104 */     if (time1 - time2 < interval)
/*     */     {
/* 106 */       return true;
/*     */     }
/* 108 */     return false;
/*     */   }
/*     */ }


/* Location:              D:\repository\fastbee-framework\fastbee-framework-2.6.0.jar!\com\fastbee\framework\interceptor\impl\SameUrlDataInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */