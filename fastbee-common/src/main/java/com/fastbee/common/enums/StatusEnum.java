/*    */ package com.fastbee.common.enums;
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
/*    */ public enum StatusEnum
/*    */ {
/* 17 */   SUCCESS(Integer.valueOf(1), "成功"),
/* 18 */   FAIL(Integer.valueOf(0), "失败");
/*    */   StatusEnum(Integer status, String name) {
/*    */     this.status = status;
/*    */     this.name = name;
/*    */   }
/*    */   public Integer getStatus() {
/* 24 */     return this.status;
/*    */   }
/*    */   private final Integer status; private final String name;
/*    */   public String getName() {
/* 28 */     return this.name;
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\common\enums\StatusEnum.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */