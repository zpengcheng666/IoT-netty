/*    */ package com.fastbee.common.enums;
/*    */ 
/*    */ import com.fastbee.common.core.text.IntArrayValuable;
/*    */ import java.util.Arrays;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum CommonStatusEnum
/*    */   implements IntArrayValuable
/*    */ {
/* 17 */   ENABLE(Integer.valueOf(0), "开启"),
/* 18 */   DISABLE(Integer.valueOf(1), "关闭");
/*    */   CommonStatusEnum(Integer status, String name) { this.status = status;
/* 20 */     this.name = name; } public static final int[] ARRAYS; static { ARRAYS = Arrays.<CommonStatusEnum>stream(values()).mapToInt(CommonStatusEnum::getStatus).toArray(); }
/*    */   
/*    */   private final Integer status; private final String name;
/*    */   
/*    */   public Integer getStatus() {
/* 25 */     return this.status;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 29 */     return this.name;
/*    */   }
/*    */   
/*    */   public int[] array() {
/* 33 */     return ARRAYS;
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\common\enums\CommonStatusEnum.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */