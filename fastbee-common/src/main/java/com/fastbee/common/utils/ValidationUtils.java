/*    */ package com.fastbee.common.utils;
/*    */ 
/*    */ import cn.hutool.core.collection.CollUtil;
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import java.util.Set;
/*    */ import java.util.regex.Pattern;
/*    */ import javax.validation.ConstraintViolationException;
/*    */ import javax.validation.Validation;
/*    */ import javax.validation.Validator;
/*    */ import org.springframework.util.StringUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ValidationUtils
/*    */ {
/* 21 */   private static final Pattern aL = Pattern.compile("^(?:(?:\\+|00)86)?1(?:(?:3[\\d])|(?:4[0,1,4-9])|(?:5[0-3,5-9])|(?:6[2,5-7])|(?:7[0-8])|(?:8[\\d])|(?:9[0-3,5-9]))\\d{8}$");
/*    */   
/* 23 */   private static final Pattern aM = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
/*    */   
/* 25 */   private static final Pattern aN = Pattern.compile("[a-zA-Z_][\\-_.0-9_a-zA-Z$]*");
/*    */   
/* 27 */   private static final Pattern aO = Pattern.compile("^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$");
/*    */   
/*    */   public static boolean isMobile(String mobile) {
/* 30 */     return (StringUtils.hasText(mobile) && aL
/* 31 */       .matcher(mobile).matches());
/*    */   }
/*    */   
/*    */   public static boolean isURL(String url) {
/* 35 */     return (StringUtils.hasText(url) && aM
/* 36 */       .matcher(url).matches());
/*    */   }
/*    */   
/*    */   public static boolean isXmlNCName(String str) {
/* 40 */     return (StringUtils.hasText(str) && aN
/* 41 */       .matcher(str).matches());
/*    */   }
/*    */   
/*    */   public static boolean isEmail(String email) {
/* 45 */     return (StringUtils.hasText(email) && aO
/* 46 */       .matcher(email).matches());
/*    */   }
/*    */   
/*    */   public static void validate(Object object, Class<?>... groups) {
/* 50 */     Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
/* 51 */     Assert.notNull(validator);
/* 52 */     validate(validator, object, groups);
/*    */   }
/*    */   
/*    */   public static void validate(Validator validator, Object object, Class<?>... groups) {
/* 56 */     Set set = validator.validate(object, groups);
/* 57 */     if (CollUtil.isNotEmpty(set))
/* 58 */       throw new ConstraintViolationException(set); 
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\ValidationUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */