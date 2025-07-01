/*    */ package com.fastbee.common.exception.file;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FileSizeLimitExceededException
/*    */   extends FileException
/*    */ {
/*    */   private static final long M = 1L;
/*    */   
/*    */   public FileSizeLimitExceededException(long defaultMaxSize) {
/* 14 */     super("upload.exceed.maxSize", new Object[] { Long.valueOf(defaultMaxSize) });
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\common\exception\file\FileSizeLimitExceededException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */