/*    */ package com.fastbee.common.utils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LogUtils
/*    */ {
/*    */   public static String getBlock(Object msg) {
/* 12 */     if (msg == null)
/*    */     {
/* 14 */       msg = "";
/*    */     }
/* 16 */     return "[" + msg.toString() + "]";
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\LogUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */