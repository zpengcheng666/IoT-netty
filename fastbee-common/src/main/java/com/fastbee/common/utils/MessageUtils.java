/*    */ package com.fastbee.common.utils;
/*    */ 
/*    */ import com.fastbee.common.utils.spring.SpringUtils;
/*    */ import org.springframework.context.MessageSource;
/*    */ import org.springframework.context.i18n.LocaleContextHolder;
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
/*    */ 
/*    */ public class MessageUtils
/*    */ {
/*    */   public static String message(String code, Object... args) {
/* 23 */     MessageSource messageSource = (MessageSource)SpringUtils.getBean(MessageSource.class);
/* 24 */     return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\MessageUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */