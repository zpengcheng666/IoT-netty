/*    */ package com.fastbee.framework.manager;
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import org.springframework.cache.interceptor.KeyGenerator;
/*    */ import org.springframework.stereotype.Component;
/*    */ 
/*    */ @Component
/*    */ public class DeleteKeyGenerator implements KeyGenerator {
/*    */   public Object generate(Object target, Method method, Object... params) {
/* 14 */     if (params.length > 0) {
/* 15 */       Object ids = params[0];
/* 16 */       Collection<?> collection = Collections.singletonList(ids);
/* 17 */       if (collection.stream().allMatch(item -> item instanceof Long)) {
/*    */ 
/*    */ 
/*    */         
/* 21 */         String[] keys = (String[])collection.stream().map(Object::toString).toArray(x$0 -> new String[x$0]);
/*    */         
/* 23 */         List<Object> keysList = new ArrayList(keys.length);
/* 24 */         keysList.addAll(Arrays.asList((Object[])keys));
/* 25 */         return keysList;
/*    */       } 
/*    */     } 
/* 28 */     return new String[0];
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-framework\fastbee-framework-2.6.0.jar!\com\fastbee\framework\manager\DeleteKeyGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */