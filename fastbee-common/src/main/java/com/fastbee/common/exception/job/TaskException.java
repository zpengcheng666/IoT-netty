/*    */ package com.fastbee.common.exception.job;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TaskException
/*    */   extends Exception
/*    */ {
/*    */   private static final long V = 1L;
/*    */   private Code W;
/*    */   
/*    */   public TaskException(String msg, Code code) {
/* 16 */     this(msg, code, null);
/*    */   }
/*    */ 
/*    */   
/*    */   public TaskException(String msg, Code code, Exception nestedEx) {
/* 21 */     super(msg, nestedEx);
/* 22 */     this.W = code;
/*    */   }
/*    */ 
/*    */   
/*    */   public Code getCode() {
/* 27 */     return this.W;
/*    */   }
/*    */   
/*    */   public enum Code
/*    */   {
/* 32 */     TASK_EXISTS, NO_TASK_EXISTS, TASK_ALREADY_STARTED, UNKNOWN, CONFIG_ERROR, TASK_NODE_NOT_AVAILABLE;
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\common\exception\job\TaskException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */