/*    */ package com.fastbee.common.exception.file;
/*    */ 
/*    */ import com.fastbee.common.exception.base.BaseException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FileException
/*    */   extends BaseException
/*    */ {
/*    */   private static final long K = 1L;
/*    */   
/*    */   public FileException(String code, Object[] args) {
/* 16 */     super("file", code, args, null);
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\common\exception\file\FileException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */