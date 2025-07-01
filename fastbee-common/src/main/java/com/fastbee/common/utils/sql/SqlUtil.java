/*    */ package com.fastbee.common.utils.sql;
/*    */ 
/*    */ import com.fastbee.common.exception.UtilException;
/*    */ import com.fastbee.common.utils.StringUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SqlUtil
/*    */ {
/* 16 */   public static String SQL_REGEX = "and |extractvalue|updatexml|exec |insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |or |+|user()";
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 21 */   public static String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String escapeOrderBySql(String value) {
/* 28 */     if (StringUtils.isNotEmpty(value) && !isValidOrderBySql(value))
/*    */     {
/* 30 */       throw new UtilException("参数不符合规范，不能进行查询");
/*    */     }
/* 32 */     return value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isValidOrderBySql(String value) {
/* 40 */     return value.matches(SQL_PATTERN);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void filterKeyword(String value) {
/* 48 */     if (StringUtils.isEmpty(value)) {
/*    */       return;
/*    */     }
/*    */     
/* 52 */     String[] arrayOfString = StringUtils.split(SQL_REGEX, "\\|");
/* 53 */     for (String str : arrayOfString) {
/*    */       
/* 55 */       if (StringUtils.indexOfIgnoreCase(value, str) > -1)
/*    */       {
/* 57 */         throw new UtilException("参数存在SQL注入风险");
/*    */       }
/*    */     } 
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\sql\SqlUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */