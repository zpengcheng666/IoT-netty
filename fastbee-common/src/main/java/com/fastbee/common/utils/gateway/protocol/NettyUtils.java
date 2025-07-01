/*    */ package com.fastbee.common.utils.gateway.protocol;
/*    */ 
/*    */ import io.netty.buffer.ByteBuf;
/*    */ import io.netty.buffer.ByteBufUtil;
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
/*    */ public class NettyUtils
/*    */ {
/*    */   public static byte[] readBytesFromByteBuf(ByteBuf buf) {
/* 18 */     return ByteBufUtil.getBytes(buf);
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\gateway\protocol\NettyUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */