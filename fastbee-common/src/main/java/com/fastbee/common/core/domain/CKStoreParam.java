/*    */ package com.fastbee.common.core.domain;
/*    */ 
/*    */ import de.schlichtherle.license.AbstractKeyStoreParam;
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CKStoreParam
/*    */   extends AbstractKeyStoreParam
/*    */ {
/*    */   private String storePath;
/*    */   private String storePwd;
/*    */   private String alias;
/*    */   private String keyPwd;
/*    */   
/*    */   public CKStoreParam(Class clazz, String resource, String alias, String storePwd, String keyPwd) {
/* 23 */     super(clazz, resource);
/* 24 */     this.storePath = resource;
/* 25 */     this.alias = alias;
/* 26 */     this.storePwd = storePwd;
/* 27 */     this.keyPwd = keyPwd;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getAlias() {
/* 32 */     return this.alias;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getStorePwd() {
/* 37 */     return this.storePwd;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getKeyPwd() {
/* 42 */     return this.keyPwd;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public InputStream getStream() throws IOException {
/* 52 */     FileInputStream fileInputStream = new FileInputStream(new File(this.storePath));
/* 53 */     if (null == fileInputStream) {
/* 54 */       throw new FileNotFoundException(this.storePath);
/*    */     }
/* 56 */     return fileInputStream;
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\common\core\domain\CKStoreParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */