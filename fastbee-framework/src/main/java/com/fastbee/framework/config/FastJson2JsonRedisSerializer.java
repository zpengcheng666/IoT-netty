/*    */ package com.fastbee.framework.config;
/*    */ 
/*    */ import com.alibaba.fastjson2.JSON;
/*    */ import com.alibaba.fastjson2.JSONReader;
/*    */ import com.alibaba.fastjson2.JSONWriter;
/*    */ import java.nio.charset.Charset;
/*    */ import org.springframework.data.redis.serializer.RedisSerializer;
/*    */ import org.springframework.data.redis.serializer.SerializationException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FastJson2JsonRedisSerializer<T>
/*    */   implements RedisSerializer<T>
/*    */ {
/* 17 */   public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
/*    */ 
/*    */   
/*    */   private Class<T> clazz;
/*    */ 
/*    */   
/*    */   public FastJson2JsonRedisSerializer(Class<T> clazz) {
/* 24 */     this.clazz = clazz;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public byte[] serialize(T t) throws SerializationException {
/* 30 */     if (t == null)
/*    */     {
/* 32 */       return new byte[0];
/*    */     }
/* 34 */     return JSON.toJSONString(t, new JSONWriter.Feature[] { JSONWriter.Feature.WriteClassName }).getBytes(DEFAULT_CHARSET);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public T deserialize(byte[] bytes) throws SerializationException {
/* 40 */     if (bytes == null || bytes.length <= 0)
/*    */     {
/* 42 */       return null;
/*    */     }
/* 44 */     String str = new String(bytes, DEFAULT_CHARSET);
/*    */     
/* 46 */     return (T)JSON.parseObject(str, this.clazz, new JSONReader.Feature[] { JSONReader.Feature.SupportAutoType });
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-framework\fastbee-framework-2.6.0.jar!\com\fastbee\framework\config\FastJson2JsonRedisSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */