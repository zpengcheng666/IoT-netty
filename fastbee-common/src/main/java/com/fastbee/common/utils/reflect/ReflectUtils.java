/*     */ package com.fastbee.common.utils.reflect;
/*     */ 
/*     */ import com.fastbee.common.core.text.Convert;
/*     */ import com.fastbee.common.utils.DateUtils;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Date;
/*     */ import org.apache.commons.lang3.StringUtils;
/*     */ import org.apache.commons.lang3.Validate;
/*     */ import org.apache.poi.ss.usermodel.DateUtil;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
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
/*     */ public class ReflectUtils
/*     */ {
/*     */   private static final String cw = "set";
/*     */   private static final String cx = "get";
/*     */   private static final String cy = "$$";
/*  32 */   private static Logger aw = LoggerFactory.getLogger(ReflectUtils.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E> E invokeGetter(Object obj, String propertyName) {
/*  41 */     Object object = obj;
/*  42 */     for (String str1 : StringUtils.split(propertyName, ".")) {
/*     */       
/*  44 */       String str2 = "get" + StringUtils.capitalize(str1);
/*  45 */       object = invokeMethod(object, str2, new Class[0], new Object[0]);
/*     */     } 
/*  47 */     return (E)object;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E> void invokeSetter(Object obj, String propertyName, E value) {
/*  56 */     Object object = obj;
/*  57 */     String[] arrayOfString = StringUtils.split(propertyName, ".");
/*  58 */     for (byte b = 0; b < arrayOfString.length; b++) {
/*     */       
/*  60 */       if (b < arrayOfString.length - 1) {
/*     */         
/*  62 */         String str = "get" + StringUtils.capitalize(arrayOfString[b]);
/*  63 */         object = invokeMethod(object, str, new Class[0], new Object[0]);
/*     */       }
/*     */       else {
/*     */         
/*  67 */         String str = "set" + StringUtils.capitalize(arrayOfString[b]);
/*  68 */         invokeMethodByName(object, str, new Object[] { value });
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E> E getFieldValue(Object obj, String fieldName) {
/*  79 */     Field field = getAccessibleField(obj, fieldName);
/*  80 */     if (field == null) {
/*     */       
/*  82 */       aw.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + fieldName + "] 字段 ");
/*  83 */       return null;
/*     */     } 
/*  85 */     Object object = null;
/*     */     
/*     */     try {
/*  88 */       object = field.get(obj);
/*     */     }
/*  90 */     catch (IllegalAccessException illegalAccessException) {
/*     */       
/*  92 */       aw.error("不可能抛出的异常{}", illegalAccessException.getMessage());
/*     */     } 
/*  94 */     return (E)object;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E> void setFieldValue(Object obj, String fieldName, E value) {
/* 102 */     Field field = getAccessibleField(obj, fieldName);
/* 103 */     if (field == null) {
/*     */ 
/*     */       
/* 106 */       aw.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + fieldName + "] 字段 ");
/*     */       
/*     */       return;
/*     */     } 
/*     */     try {
/* 111 */       field.set(obj, value);
/*     */     }
/* 113 */     catch (IllegalAccessException illegalAccessException) {
/*     */       
/* 115 */       aw.error("不可能抛出的异常: {}", illegalAccessException.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E> E invokeMethod(Object obj, String methodName, Class<?>[] parameterTypes, Object[] args) {
/* 128 */     if (obj == null || methodName == null)
/*     */     {
/* 130 */       return null;
/*     */     }
/* 132 */     Method method = getAccessibleMethod(obj, methodName, parameterTypes);
/* 133 */     if (method == null) {
/*     */       
/* 135 */       aw.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + methodName + "] 方法 ");
/* 136 */       return null;
/*     */     } 
/*     */     
/*     */     try {
/* 140 */       return (E)method.invoke(obj, args);
/*     */     }
/* 142 */     catch (Exception exception) {
/*     */       
/* 144 */       String str = "method: " + method + ", obj: " + obj + ", args: " + args + "";
/* 145 */       throw convertReflectionExceptionToUnchecked(str, exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E> E invokeMethodByName(Object obj, String methodName, Object[] args) {
/* 157 */     Method method = getAccessibleMethodByName(obj, methodName, args.length);
/* 158 */     if (method == null) {
/*     */ 
/*     */       
/* 161 */       aw.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + methodName + "] 方法 ");
/* 162 */       return null;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 167 */       Class[] arrayOfClass = method.getParameterTypes();
/* 168 */       for (byte b = 0; b < arrayOfClass.length; b++) {
/*     */         
/* 170 */         if (args[b] != null && !args[b].getClass().equals(arrayOfClass[b]))
/*     */         {
/* 172 */           if (arrayOfClass[b] == String.class) {
/*     */             
/* 174 */             args[b] = Convert.toStr(args[b]);
/* 175 */             if (StringUtils.endsWith((String)args[b], ".0"))
/*     */             {
/* 177 */               args[b] = StringUtils.substringBefore((String)args[b], ".0");
/*     */             }
/*     */           }
/* 180 */           else if (arrayOfClass[b] == Integer.class) {
/*     */             
/* 182 */             args[b] = Convert.toInt(args[b]);
/*     */           }
/* 184 */           else if (arrayOfClass[b] == Long.class) {
/*     */             
/* 186 */             args[b] = Convert.toLong(args[b]);
/*     */           }
/* 188 */           else if (arrayOfClass[b] == Double.class) {
/*     */             
/* 190 */             args[b] = Convert.toDouble(args[b]);
/*     */           }
/* 192 */           else if (arrayOfClass[b] == Float.class) {
/*     */             
/* 194 */             args[b] = Convert.toFloat(args[b]);
/*     */           }
/* 196 */           else if (arrayOfClass[b] == Date.class) {
/*     */             
/* 198 */             if (args[b] instanceof String)
/*     */             {
/* 200 */               args[b] = DateUtils.parseDate(args[b]);
/*     */             }
/*     */             else
/*     */             {
/* 204 */               args[b] = DateUtil.getJavaDate(((Double)args[b]).doubleValue());
/*     */             }
/*     */           
/* 207 */           } else if (arrayOfClass[b] == boolean.class || arrayOfClass[b] == Boolean.class) {
/*     */             
/* 209 */             args[b] = Convert.toBool(args[b]);
/*     */           } 
/*     */         }
/*     */       } 
/* 213 */       return (E)method.invoke(obj, args);
/*     */     }
/* 215 */     catch (Exception exception) {
/*     */       
/* 217 */       String str = "method: " + method + ", obj: " + obj + ", args: " + args + "";
/* 218 */       throw convertReflectionExceptionToUnchecked(str, exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Field getAccessibleField(Object obj, String fieldName) {
/* 229 */     if (obj == null)
/*     */     {
/* 231 */       return null;
/*     */     }
/* 233 */     Validate.notBlank(fieldName, "fieldName can't be blank", new Object[0]);
/* 234 */     for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
/*     */ 
/*     */       
/*     */       try {
/* 238 */         Field field = clazz.getDeclaredField(fieldName);
/* 239 */         makeAccessible(field);
/* 240 */         return field;
/*     */       }
/* 242 */       catch (NoSuchFieldException noSuchFieldException) {}
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 247 */     return null;
/*     */   }
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
/*     */   public static Method getAccessibleMethod(Object obj, String methodName, Class<?>... parameterTypes) {
/* 260 */     if (obj == null)
/*     */     {
/* 262 */       return null;
/*     */     }
/* 264 */     Validate.notBlank(methodName, "methodName can't be blank", new Object[0]);
/* 265 */     for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
/*     */ 
/*     */       
/*     */       try {
/* 269 */         Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
/* 270 */         makeAccessible(method);
/* 271 */         return method;
/*     */       }
/* 273 */       catch (NoSuchMethodException noSuchMethodException) {}
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 278 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Method getAccessibleMethodByName(Object obj, String methodName, int argsNum) {
/* 290 */     if (obj == null)
/*     */     {
/* 292 */       return null;
/*     */     }
/* 294 */     Validate.notBlank(methodName, "methodName can't be blank", new Object[0]);
/* 295 */     for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
/*     */       
/* 297 */       Method[] arrayOfMethod = clazz.getDeclaredMethods();
/* 298 */       for (Method method : arrayOfMethod) {
/*     */         
/* 300 */         if (method.getName().equals(methodName) && (method.getParameterTypes()).length == argsNum) {
/*     */           
/* 302 */           makeAccessible(method);
/* 303 */           return method;
/*     */         } 
/*     */       } 
/*     */     } 
/* 307 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void makeAccessible(Method method) {
/* 315 */     if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && 
/* 316 */       !method.isAccessible())
/*     */     {
/* 318 */       method.setAccessible(true);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void makeAccessible(Field field) {
/* 327 */     if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || 
/* 328 */       Modifier.isFinal(field.getModifiers())) && !field.isAccessible())
/*     */     {
/* 330 */       field.setAccessible(true);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> Class<T> getClassGenricType(Class clazz) {
/* 341 */     return getClassGenricType(clazz, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Class getClassGenricType(Class clazz, int index) {
/* 350 */     Type type = clazz.getGenericSuperclass();
/*     */     
/* 352 */     if (!(type instanceof ParameterizedType)) {
/*     */       
/* 354 */       aw.debug(clazz.getSimpleName() + "'s superclass not ParameterizedType");
/* 355 */       return Object.class;
/*     */     } 
/*     */     
/* 358 */     Type[] arrayOfType = ((ParameterizedType)type).getActualTypeArguments();
/*     */     
/* 360 */     if (index >= arrayOfType.length || index < 0) {
/*     */       
/* 362 */       aw.debug("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: " + arrayOfType.length);
/*     */       
/* 364 */       return Object.class;
/*     */     } 
/* 366 */     if (!(arrayOfType[index] instanceof Class)) {
/*     */       
/* 368 */       aw.debug(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
/* 369 */       return Object.class;
/*     */     } 
/*     */     
/* 372 */     return (Class)arrayOfType[index];
/*     */   }
/*     */ 
/*     */   
/*     */   public static Class<?> getUserClass(Object instance) {
/* 377 */     if (instance == null)
/*     */     {
/* 379 */       throw new RuntimeException("Instance must not be null");
/*     */     }
/* 381 */     Class<?> clazz = instance.getClass();
/* 382 */     if (clazz != null && clazz.getName().contains("$$")) {
/*     */       
/* 384 */       Class<?> clazz1 = clazz.getSuperclass();
/* 385 */       if (clazz1 != null && !Object.class.equals(clazz1))
/*     */       {
/* 387 */         return clazz1;
/*     */       }
/*     */     } 
/* 390 */     return clazz;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RuntimeException convertReflectionExceptionToUnchecked(String msg, Exception e) {
/* 399 */     if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException || e instanceof NoSuchMethodException)
/*     */     {
/*     */       
/* 402 */       return new IllegalArgumentException(msg, e);
/*     */     }
/* 404 */     if (e instanceof InvocationTargetException)
/*     */     {
/* 406 */       return new RuntimeException(msg, ((InvocationTargetException)e).getTargetException());
/*     */     }
/* 408 */     return new RuntimeException(msg, e);
/*     */   }
/*     */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\reflect\ReflectUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */