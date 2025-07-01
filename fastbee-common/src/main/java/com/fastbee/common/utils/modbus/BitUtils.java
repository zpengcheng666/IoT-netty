/*    */ package com.fastbee.common.utils.modbus;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BitUtils
/*    */ {
/*    */   public static int getBitFlag(long num, int bit) {
/* 18 */     return (int)num >> bit & 0x1;
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
/*    */   public static long updateBitValue(long num, int bit, boolean flagValue) {
/* 30 */     if (flagValue)
/*    */     {
/* 32 */       return num | (1 << bit);
/*    */     }
/*    */     
/* 35 */     return num ^ (getBitFlag(num, bit) << bit);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String toBinaryString(long num) {
/* 46 */     return Long.toBinaryString(num);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static int deter(int num, int i) {
/* 57 */     i++;
/* 58 */     return num >> i - 1 & 0x1;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static int deterHex(String hex, int i) {
/* 68 */     return deter(Integer.parseInt(hex, 16), i);
/*    */   }
/*    */   
/*    */   public static void main(String[] args) {
/* 72 */     int i = deter(7, 0);
/* 73 */     int j = deterHex("10", 4);
/* 74 */     System.out.println(i);
/* 75 */     System.out.println(j);
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\modbus\BitUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */