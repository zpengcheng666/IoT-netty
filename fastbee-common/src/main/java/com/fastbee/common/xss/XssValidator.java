/*    */ package com.fastbee.common.xss;
/*    */ 
/*    */ import com.fastbee.common.utils.StringUtils;
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ import javax.validation.ConstraintValidator;
/*    */ import javax.validation.ConstraintValidatorContext;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class XssValidator
/*    */   implements ConstraintValidator<Xss, String>
/*    */ {
/*    */   private static final String di = "<(\\S*?)[^>]*>.*?|<.*? />";
/*    */   
/*    */   public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
/* 21 */     if (StringUtils.isBlank(value))
/*    */     {
/* 23 */       return true;
/*    */     }
/* 25 */     return !containsHtml(value);
/*    */   }
/*    */ 
/*    */   
/*    */   public static boolean containsHtml(String value) {
/* 30 */     Pattern pattern = Pattern.compile("<(\\S*?)[^>]*>.*?|<.*? />");
/* 31 */     Matcher matcher = pattern.matcher(value);
/* 32 */     return matcher.matches();
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\common\xss\XssValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */