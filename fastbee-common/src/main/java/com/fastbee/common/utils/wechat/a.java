/*    */ package com.fastbee.common.utils.wechat;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ class a {
/*  6 */   ArrayList<Byte> dg = new ArrayList<>();
/*    */   
/*    */   public byte[] i() {
/*  9 */     byte[] arrayOfByte = new byte[this.dg.size()];
/* 10 */     for (byte b = 0; b < this.dg.size(); b++) {
/* 11 */       arrayOfByte[b] = ((Byte)this.dg.get(b)).byteValue();
/*    */     }
/* 13 */     return arrayOfByte;
/*    */   }
/*    */   
/*    */   public a g(byte[] paramArrayOfbyte) {
/* 17 */     for (byte b : paramArrayOfbyte) {
/* 18 */       this.dg.add(Byte.valueOf(b));
/*    */     }
/* 20 */     return this;
/*    */   }
/*    */   
/*    */   public int size() {
/* 24 */     return this.dg.size();
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\wechat\a.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */