/*    */ package com.fastbee.framework.security.context;
/*    */ 
/*    */ import com.fastbee.common.core.text.Convert;
/*    */ import org.springframework.web.context.request.RequestContextHolder;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PermissionContextHolder
/*    */ {
/*    */   private static final String PERMISSION_CONTEXT_ATTRIBUTES = "PERMISSION_CONTEXT";
/*    */   
/*    */   public static void setContext(String permission) {
/* 18 */     RequestContextHolder.currentRequestAttributes().setAttribute("PERMISSION_CONTEXT", permission, 0);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getContext() {
/* 24 */     return Convert.toStr(RequestContextHolder.currentRequestAttributes().getAttribute("PERMISSION_CONTEXT", 0));
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-framework\fastbee-framework-2.6.0.jar!\com\fastbee\framework\security\context\PermissionContextHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */