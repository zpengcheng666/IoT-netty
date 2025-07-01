/*    */ package com.fastbee.framework.handler;
/*    */ 
/*    */ import com.fastbee.common.utils.StringUtils;
/*    */ import org.redisson.api.NameMapper;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class KeyPrefixHandler
/*    */   implements NameMapper
/*    */ {
/*    */   private final String keyPrefix;
/*    */   
/*    */   public KeyPrefixHandler(String keyPrefix) {
/* 19 */     this.keyPrefix = StringUtils.isBlank(keyPrefix) ? "" : (keyPrefix + ":");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String map(String name) {
/* 27 */     if (StringUtils.isBlank(name)) {
/* 28 */       return null;
/*    */     }
/* 30 */     if (StringUtils.isNotBlank(this.keyPrefix) && !name.startsWith(this.keyPrefix)) {
/* 31 */       return this.keyPrefix + name;
/*    */     }
/* 33 */     return name;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String unmap(String name) {
/* 41 */     if (StringUtils.isBlank(name)) {
/* 42 */       return null;
/*    */     }
/* 44 */     if (StringUtils.isNotBlank(this.keyPrefix) && name.startsWith(this.keyPrefix)) {
/* 45 */       return name.substring(this.keyPrefix.length());
/*    */     }
/* 47 */     return name;
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-framework\fastbee-framework-2.6.0.jar!\com\fastbee\framework\handler\KeyPrefixHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */