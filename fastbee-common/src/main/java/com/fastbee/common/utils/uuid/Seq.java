/*    */ package com.fastbee.common.utils.uuid;
/*    */ 
/*    */ import com.fastbee.common.utils.DateUtils;
/*    */ import com.fastbee.common.utils.StringUtils;
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Seq
/*    */ {
/*    */   public static final String commSeqType = "COMMON";
/*    */   public static final String uploadSeqType = "UPLOAD";
/* 19 */   private static AtomicInteger cT = new AtomicInteger(1);
/*    */ 
/*    */   
/* 22 */   private static AtomicInteger cU = new AtomicInteger(1);
/*    */ 
/*    */   
/* 25 */   private static String cV = "A";
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getId() {
/* 34 */     return getId("COMMON");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getId(String type) {
/* 44 */     AtomicInteger atomicInteger = cT;
/* 45 */     if ("UPLOAD".equals(type))
/*    */     {
/* 47 */       atomicInteger = cU;
/*    */     }
/* 49 */     return getId(atomicInteger, 3);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getId(AtomicInteger atomicInt, int length) {
/* 61 */     String str = DateUtils.dateTimeNow();
/* 62 */     str = str + cV;
/* 63 */     str = str + a(atomicInt, length);
/* 64 */     return str;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static synchronized String a(AtomicInteger paramAtomicInteger, int paramInt) {
/* 75 */     int i = paramAtomicInteger.getAndIncrement();
/*    */ 
/*    */     
/* 78 */     int j = (int)Math.pow(10.0D, paramInt);
/* 79 */     if (paramAtomicInteger.get() >= j)
/*    */     {
/* 81 */       paramAtomicInteger.set(1);
/*    */     }
/*    */     
/* 84 */     return StringUtils.padl(Integer.valueOf(i), paramInt);
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\util\\uuid\Seq.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */