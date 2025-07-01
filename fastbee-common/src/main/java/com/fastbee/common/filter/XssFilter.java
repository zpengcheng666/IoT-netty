/*    */ package com.fastbee.common.filter;
/*    */ 
/*    */ import com.fastbee.common.enums.HttpMethod;
/*    */ import com.fastbee.common.utils.StringUtils;
/*    */ import java.io.IOException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import javax.servlet.Filter;
/*    */ import javax.servlet.FilterChain;
/*    */ import javax.servlet.FilterConfig;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.ServletRequest;
/*    */ import javax.servlet.ServletResponse;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpServletResponse;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class XssFilter
/*    */   implements Filter
/*    */ {
/* 27 */   public List<String> excludes = new ArrayList<>();
/*    */ 
/*    */ 
/*    */   
/*    */   public void init(FilterConfig filterConfig) throws ServletException {
/* 32 */     String str = filterConfig.getInitParameter("excludes");
/* 33 */     if (StringUtils.isNotEmpty(str)) {
/*    */       
/* 35 */       String[] arrayOfString = str.split(",");
/* 36 */       for (byte b = 0; arrayOfString != null && b < arrayOfString.length; b++)
/*    */       {
/* 38 */         this.excludes.add(arrayOfString[b]);
/*    */       }
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
/* 47 */     HttpServletRequest httpServletRequest = (HttpServletRequest)request;
/* 48 */     HttpServletResponse httpServletResponse = (HttpServletResponse)response;
/* 49 */     if (a(httpServletRequest, httpServletResponse)) {
/*    */       
/* 51 */       chain.doFilter(request, response);
/*    */       return;
/*    */     } 
/* 54 */     XssHttpServletRequestWrapper xssHttpServletRequestWrapper = new XssHttpServletRequestWrapper((HttpServletRequest)request);
/* 55 */     chain.doFilter((ServletRequest)xssHttpServletRequestWrapper, response);
/*    */   }
/*    */ 
/*    */   
/*    */   private boolean a(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse) {
/* 60 */     String str1 = paramHttpServletRequest.getServletPath();
/* 61 */     String str2 = paramHttpServletRequest.getMethod();
/*    */     
/* 63 */     if (str2 == null || HttpMethod.GET.matches(str2) || HttpMethod.DELETE.matches(str2))
/*    */     {
/* 65 */       return true;
/*    */     }
/* 67 */     return StringUtils.matches(str1, this.excludes);
/*    */   }
/*    */   
/*    */   public void destroy() {}
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\common\filter\XssFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */