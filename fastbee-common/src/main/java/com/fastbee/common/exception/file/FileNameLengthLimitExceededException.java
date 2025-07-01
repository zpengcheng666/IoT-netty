/*    */ package com.fastbee.common.exception.file;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FileNameLengthLimitExceededException
/*    */   extends FileException
/*    */ {
/*    */   private static final long L = 1L;
/*    */   
/*    */   public FileNameLengthLimitExceededException(int defaultFileNameLength) {
/* 14 */     super("upload.filename.exceed.length", new Object[] { Integer.valueOf(defaultFileNameLength) });
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\common\exception\file\FileNameLengthLimitExceededException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */