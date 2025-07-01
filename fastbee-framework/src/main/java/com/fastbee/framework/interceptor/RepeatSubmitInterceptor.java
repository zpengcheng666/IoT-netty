/*    */ package com.fastbee.framework.interceptor;
/*    */ 
/*    */ import com.alibaba.fastjson2.JSON;
/*    */ import com.fastbee.common.annotation.RepeatSubmit;
/*    */ import com.fastbee.common.core.domain.AjaxResult;
/*    */ import com.fastbee.common.utils.ServletUtils;
/*    */ import java.lang.reflect.Method;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpServletResponse;
/*    */ import org.springframework.stereotype.Component;
/*    */ import org.springframework.web.method.HandlerMethod;
/*    */ import org.springframework.web.servlet.HandlerInterceptor;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Component
/*    */ public abstract class RepeatSubmitInterceptor
/*    */   implements HandlerInterceptor
/*    */ {
/*    */   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
/* 25 */     if (handler instanceof HandlerMethod) {
/*    */       
/* 27 */       HandlerMethod handlerMethod = (HandlerMethod)handler;
/* 28 */       Method method = handlerMethod.getMethod();
/* 29 */       RepeatSubmit annotation = method.<RepeatSubmit>getAnnotation(RepeatSubmit.class);
/* 30 */       if (annotation != null)
/*    */       {
/* 32 */         if (isRepeatSubmit(request, annotation)) {
/*    */           
/* 34 */           AjaxResult ajaxResult = AjaxResult.error(annotation.message());
/* 35 */           ServletUtils.renderString(response, JSON.toJSONString(ajaxResult));
/* 36 */           return false;
/*    */         } 
/*    */       }
/* 39 */       return true;
/*    */     } 
/*    */ 
/*    */     
/* 43 */     return true;
/*    */   }
/*    */   
/*    */   public abstract boolean isRepeatSubmit(HttpServletRequest paramHttpServletRequest, RepeatSubmit paramRepeatSubmit);
/*    */ }


/* Location:              D:\repository\fastbee-framework\fastbee-framework-2.6.0.jar!\com\fastbee\framework\interceptor\RepeatSubmitInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */