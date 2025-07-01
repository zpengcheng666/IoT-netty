/*    */ package com.fastbee.common.enums;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum UserStatus
/*    */ {
/* 10 */   OK(Integer.valueOf(0), "正常"), DISABLE(Integer.valueOf(1), "停用"), DELETED(Integer.valueOf(2), "删除");
/*    */   
/*    */   private final String info;
/*    */   
/*    */   private final Integer code;
/*    */   
/*    */   UserStatus(Integer code, String info) {
/* 17 */     this.code = code;
/* 18 */     this.info = info;
/*    */   }
/*    */ 
/*    */   
/*    */   public Integer getCode() {
/* 23 */     return this.code;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getInfo() {
/* 28 */     return this.info;
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\common\enums\UserStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */