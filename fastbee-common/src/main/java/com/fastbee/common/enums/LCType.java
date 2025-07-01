/*    */ package com.fastbee.common.enums;
/*    */ 
/*    */ public enum LCType {
/*  4 */   AGENT(Long.valueOf(1L)),
/*  5 */   ENTERPRISE(Long.valueOf(2L)),
/*  6 */   PERSON(Long.valueOf(3L)),
/*  7 */   TRIAL(Long.valueOf(4L));
/*    */   
/*    */   private Long type;
/*    */   
/*    */   LCType(Long type) {
/* 12 */     this.type = type;
/*    */   }
/*    */   
/*    */   public Long getType() {
/* 16 */     return this.type;
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\common\enums\LCType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */