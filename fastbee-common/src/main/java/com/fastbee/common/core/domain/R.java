/*     */ package com.fastbee.common.core.domain;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class R<T>
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   public static final int SUCCESS = 200;
/*     */   public static final int FAIL = 500;
/*     */   private int code;
/*     */   private String msg;
/*     */   private T data;
/*     */   
/*     */   public static <T> R<T> ok() {
/*  29 */     return restResult(null, 200, "操作成功");
/*     */   }
/*     */ 
/*     */   
/*     */   public static <T> R<T> ok(T data) {
/*  34 */     return restResult(data, 200, "操作成功");
/*     */   }
/*     */ 
/*     */   
/*     */   public static <T> R<T> ok(T data, String msg) {
/*  39 */     return restResult(data, 200, msg);
/*     */   }
/*     */ 
/*     */   
/*     */   public static <T> R<T> fail() {
/*  44 */     return restResult(null, 500, "操作失败");
/*     */   }
/*     */ 
/*     */   
/*     */   public static <T> R<T> fail(String msg) {
/*  49 */     return restResult(null, 500, msg);
/*     */   }
/*     */ 
/*     */   
/*     */   public static <T> R<T> fail(T data) {
/*  54 */     return restResult(data, 500, "操作失败");
/*     */   }
/*     */ 
/*     */   
/*     */   public static <T> R<T> fail(T data, String msg) {
/*  59 */     return restResult(data, 500, msg);
/*     */   }
/*     */ 
/*     */   
/*     */   public static <T> R<T> fail(int code, String msg) {
/*  64 */     return restResult(null, code, msg);
/*     */   }
/*     */ 
/*     */   
/*     */   private static <T> R<T> restResult(T data, int code, String msg) {
/*  69 */     R<T> r = new R();
/*  70 */     r.setCode(code);
/*  71 */     r.setData(data);
/*  72 */     r.setMsg(msg);
/*  73 */     return r;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCode() {
/*  78 */     return this.code;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCode(int code) {
/*  83 */     this.code = code;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMsg() {
/*  88 */     return this.msg;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMsg(String msg) {
/*  93 */     this.msg = msg;
/*     */   }
/*     */ 
/*     */   
/*     */   public T getData() {
/*  98 */     return this.data;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setData(T data) {
/* 103 */     this.data = data;
/*     */   }
/*     */ 
/*     */   
/*     */   public static <T> Boolean isError(R<T> ret) {
/* 108 */     return Boolean.valueOf(!isSuccess(ret).booleanValue());
/*     */   }
/*     */ 
/*     */   
/*     */   public static <T> Boolean isSuccess(R<T> ret) {
/* 113 */     return Boolean.valueOf((200 == ret.getCode()));
/*     */   }
/*     */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\common\core\domain\R.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */