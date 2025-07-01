/*    */ package com.fastbee.common.exception.base;
/*    */ 
/*    */ import com.fastbee.common.utils.MessageUtils;
/*    */ import com.fastbee.common.utils.StringUtils;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BaseException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long G = 1L;
/*    */   private String H;
/*    */   private String code;
/*    */   private Object[] I;
/*    */   private String J;
/*    */   
/*    */   public BaseException(String module, String code, Object[] args, String defaultMessage) {
/* 37 */     this.H = module;
/* 38 */     this.code = code;
/* 39 */     this.I = args;
/* 40 */     this.J = defaultMessage;
/*    */   }
/*    */ 
/*    */   
/*    */   public BaseException(String module, String code, Object[] args) {
/* 45 */     this(module, code, args, null);
/*    */   }
/*    */ 
/*    */   
/*    */   public BaseException(String module, String defaultMessage) {
/* 50 */     this(module, null, null, defaultMessage);
/*    */   }
/*    */ 
/*    */   
/*    */   public BaseException(String code, Object[] args) {
/* 55 */     this(null, code, args, null);
/*    */   }
/*    */ 
/*    */   
/*    */   public BaseException(String defaultMessage) {
/* 60 */     this(null, null, null, defaultMessage);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 66 */     String str = null;
/* 67 */     if (!StringUtils.isEmpty(this.code))
/*    */     {
/* 69 */       str = MessageUtils.message(this.code, this.I);
/*    */     }
/* 71 */     if (str == null)
/*    */     {
/* 73 */       str = this.J;
/*    */     }
/* 75 */     return str;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getModule() {
/* 80 */     return this.H;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCode() {
/* 85 */     return this.code;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object[] getArgs() {
/* 90 */     return this.I;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getDefaultMessage() {
/* 95 */     return this.J;
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\common\exception\base\BaseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */