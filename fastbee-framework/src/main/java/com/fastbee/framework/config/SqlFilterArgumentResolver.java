/*    */ package com.fastbee.framework.config;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import com.baomidou.mybatisplus.core.metadata.OrderItem;
/*    */ import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import java.util.Collection;
/*    */ import java.util.List;
/*    */ import java.util.Optional;
/*    */ import java.util.function.Predicate;
/*    */ import java.util.stream.Collectors;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ import org.springframework.core.MethodParameter;
/*    */ import org.springframework.web.bind.support.WebDataBinderFactory;
/*    */ import org.springframework.web.context.request.NativeWebRequest;
/*    */ import org.springframework.web.method.support.HandlerMethodArgumentResolver;
/*    */ import org.springframework.web.method.support.ModelAndViewContainer;
/*    */ 
/*    */ public class SqlFilterArgumentResolver
/*    */   implements HandlerMethodArgumentResolver
/*    */ {
/* 25 */   private static final Logger log = LoggerFactory.getLogger(SqlFilterArgumentResolver.class);
/*    */ 
/*    */   
/* 28 */   private static final String[] KEYWORDS = new String[] { "master", "truncate", "insert", "select", "delete", "update", "declare", "alter", "drop", "sleep" };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean supportsParameter(MethodParameter parameter) {
/* 38 */     return parameter.getParameterType().equals(Page.class);
/*    */   }
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
/*    */   
/*    */   public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
/* 54 */     HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest(HttpServletRequest.class);
/*    */     
/* 56 */     String[] ascs = request.getParameterValues("ascs");
/* 57 */     String[] descs = request.getParameterValues("descs");
/* 58 */     String current = request.getParameter("current");
/* 59 */     String size = request.getParameter("size");
/*    */     
/* 61 */     Page<?> page = new Page();
/* 62 */     if (StrUtil.isNotBlank(current)) {
/* 63 */       page.setCurrent(Long.parseLong(current));
/*    */     }
/*    */     
/* 66 */     if (StrUtil.isNotBlank(size)) {
/* 67 */       page.setSize(Long.parseLong(size));
/*    */     }
/* 69 */     List<OrderItem> orderItemList = new ArrayList<>();
/* 70 */     Optional.<String[]>ofNullable(ascs).ifPresent(s -> orderItemList.addAll((Collection)Arrays.<String>stream(s).filter(sqlInjectPredicate()).map(OrderItem::asc).collect(Collectors.toList())));
/*    */     
/* 72 */     Optional.<String[]>ofNullable(descs).ifPresent(s -> orderItemList.addAll((Collection)Arrays.<String>stream(s).filter(sqlInjectPredicate()).map(OrderItem::desc).collect(Collectors.toList())));
/*    */     
/* 74 */     page.addOrder(orderItemList);
/* 75 */     return page;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private Predicate<String> sqlInjectPredicate() {
/* 83 */     return sql -> {
/*    */         for (String keyword : KEYWORDS) {
/*    */           if (StrUtil.containsIgnoreCase(sql, keyword))
/*    */             return false; 
/*    */         } 
/*    */         return true;
/*    */       };
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-framework\fastbee-framework-2.6.0.jar!\com\fastbee\framework\config\SqlFilterArgumentResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */