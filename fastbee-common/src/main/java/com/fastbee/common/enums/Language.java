/*    */ package com.fastbee.common.enums;
/*    */ 
/*    */ public enum Language {
/*  4 */   ZH_CN("zh-CN"),
/*  5 */   EN("en-US"),
/*  6 */   DEFAULT("default");
/*    */   
/*    */   private String value;
/*    */   
/*    */   Language(String value) {
/* 11 */     this.value = value;
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 15 */     return this.value;
/*    */   }
/*    */   
/*    */   public static String matches(String language) {
/* 19 */     if (language.equals("zh")) {
/* 20 */       return ZH_CN.value;
/*    */     }
/* 22 */     return EN.value;
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\common\enums\Language.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */