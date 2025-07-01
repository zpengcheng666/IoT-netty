/*    */ package com.fastbee.common.utils.object;
/*    */ 
/*    */ import cn.hutool.core.util.ObjectUtil;
/*    */ import cn.hutool.core.util.ReflectUtil;
/*    */ import java.lang.reflect.Field;
/*    */ import java.util.Arrays;
/*    */ import java.util.function.Consumer;
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
/*    */ public class ObjectUtils
/*    */ {
/*    */   public static <T> T cloneIgnoreId(T object, Consumer<T> consumer) {
/* 25 */     Object object1 = ObjectUtil.clone(object);
/*    */     
/* 27 */     Field field = ReflectUtil.getField(object.getClass(), "id");
/* 28 */     if (field != null) {
/* 29 */       ReflectUtil.setFieldValue(object1, field, null);
/*    */     }
/*    */     
/* 32 */     if (object1 != null) {
/* 33 */       consumer.accept((T)object1);
/*    */     }
/* 35 */     return (T)object1;
/*    */   }
/*    */   
/*    */   public static <T extends Comparable<T>> T max(T obj1, T obj2) {
/* 39 */     if (obj1 == null) {
/* 40 */       return obj2;
/*    */     }
/* 42 */     if (obj2 == null) {
/* 43 */       return obj1;
/*    */     }
/* 45 */     return (obj1.compareTo(obj2) > 0) ? obj1 : obj2;
/*    */   }
/*    */   
/*    */   @SafeVarargs
/*    */   public static <T> T defaultIfNull(T... array) {
/* 50 */     for (T t : array) {
/* 51 */       if (t != null) {
/* 52 */         return t;
/*    */       }
/*    */     } 
/* 55 */     return null;
/*    */   }
/*    */   
/*    */   @SafeVarargs
/*    */   public static <T> boolean equalsAny(T obj, T... array) {
/* 60 */     return Arrays.<T>asList(array).contains(obj);
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\object\ObjectUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */