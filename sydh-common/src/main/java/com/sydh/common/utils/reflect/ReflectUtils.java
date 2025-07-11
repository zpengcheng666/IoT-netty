package com.sydh.common.utils.reflect;

import com.sydh.common.core.text.Convert;
import com.sydh.common.utils.DateUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.poi.ss.usermodel.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReflectUtils {
    private static final String cw = "set";
    private static final String cx = "get";
    private static final String cy = "$$";
    private static Logger aw = LoggerFactory.getLogger(ReflectUtils.class);


    public static <E> E invokeGetter(Object obj, String propertyName) {
        Object object = obj;
        for (String str1 : StringUtils.split(propertyName, ".")) {

            String str2 = "get" + StringUtils.capitalize(str1);
            object = invokeMethod(object, str2, new Class[0], new Object[0]);
        }
        return (E) object;
    }


    public static <E> void invokeSetter(Object obj, String propertyName, E value) {
        Object object = obj;
        String[] arrayOfString = StringUtils.split(propertyName, ".");
        for (byte b = 0; b < arrayOfString.length; b++) {

            if (b < arrayOfString.length - 1) {

                String str = "get" + StringUtils.capitalize(arrayOfString[b]);
                object = invokeMethod(object, str, new Class[0], new Object[0]);
            } else {

                String str = "set" + StringUtils.capitalize(arrayOfString[b]);
                invokeMethodByName(object, str, new Object[]{value});
            }
        }
    }


    public static <E> E getFieldValue(Object obj, String fieldName) {
        Field field = getAccessibleField(obj, fieldName);
        if (field == null) {

            aw.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + fieldName + "] 字段 ");
            return null;
        }
        Object object = null;

        try {
            object = field.get(obj);
        } catch (IllegalAccessException illegalAccessException) {

            aw.error("不可能抛出的异常{}", illegalAccessException.getMessage());
        }
        return (E) object;
    }


    public static <E> void setFieldValue(Object obj, String fieldName, E value) {
        Field field = getAccessibleField(obj, fieldName);
        if (field == null) {


            aw.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + fieldName + "] 字段 ");

            return;
        }
        try {
            field.set(obj, value);
        } catch (IllegalAccessException illegalAccessException) {

            aw.error("不可能抛出的异常: {}", illegalAccessException.getMessage());
        }
    }


    public static <E> E invokeMethod(Object obj, String methodName, Class<?>[] parameterTypes, Object[] args) {
        if (obj == null || methodName == null) {
            return null;
        }
        Method method = getAccessibleMethod(obj, methodName, parameterTypes);
        if (method == null) {

            aw.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + methodName + "] 方法 ");
            return null;
        }

        try {
            return (E) method.invoke(obj, args);
        } catch (Exception exception) {

            String str = "method: " + method + ", obj: " + obj + ", args: " + args + "";
            throw convertReflectionExceptionToUnchecked(str, exception);
        }
    }


    public static <E> E invokeMethodByName(Object obj, String methodName, Object[] args) {
        Method method = getAccessibleMethodByName(obj, methodName, args.length);
        if (method == null) {


            aw.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + methodName + "] 方法 ");
            return null;
        }


        try {
            Class[] arrayOfClass = method.getParameterTypes();
            for (byte b = 0; b < arrayOfClass.length; b++) {

                if (args[b] != null && !args[b].getClass().equals(arrayOfClass[b])) {
                    if (arrayOfClass[b] == String.class) {

                        args[b] = Convert.toStr(args[b]);
                        if (StringUtils.endsWith((String) args[b], ".0")) {
                            args[b] = StringUtils.substringBefore((String) args[b], ".0");
                        }
                    } else if (arrayOfClass[b] == Integer.class) {

                        args[b] = Convert.toInt(args[b]);
                    } else if (arrayOfClass[b] == Long.class) {

                        args[b] = Convert.toLong(args[b]);
                    } else if (arrayOfClass[b] == Double.class) {

                        args[b] = Convert.toDouble(args[b]);
                    } else if (arrayOfClass[b] == Float.class) {

                        args[b] = Convert.toFloat(args[b]);
                    } else if (arrayOfClass[b] == Date.class) {

                        if (args[b] instanceof String) {
                            args[b] = DateUtils.parseDate(args[b]);
                        } else {
                            args[b] = DateUtil.getJavaDate(((Double) args[b]).doubleValue());
                        }

                    } else if (arrayOfClass[b] == boolean.class || arrayOfClass[b] == Boolean.class) {

                        args[b] = Convert.toBool(args[b]);
                    }
                }
            }
            return (E) method.invoke(obj, args);
        } catch (Exception exception) {

            String str = "method: " + method + ", obj: " + obj + ", args: " + args + "";
            throw convertReflectionExceptionToUnchecked(str, exception);
        }
    }


    public static Field getAccessibleField(Object obj, String fieldName) {
        if (obj == null) {
            return null;
        }
        Validate.notBlank(fieldName, "fieldName can't be blank", new Object[0]);
        for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {


            try {
                Field field = clazz.getDeclaredField(fieldName);
                makeAccessible(field);
                return field;
            } catch (NoSuchFieldException noSuchFieldException) {
            }
        }


        return null;
    }


    public static Method getAccessibleMethod(Object obj, String methodName, Class<?>... parameterTypes) {
        if (obj == null) {
            return null;
        }
        Validate.notBlank(methodName, "methodName can't be blank", new Object[0]);
        for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {


            try {
                Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
                makeAccessible(method);
                return method;
            } catch (NoSuchMethodException noSuchMethodException) {
            }
        }


        return null;
    }


    public static Method getAccessibleMethodByName(Object obj, String methodName, int argsNum) {
        if (obj == null) {
            return null;
        }
        Validate.notBlank(methodName, "methodName can't be blank", new Object[0]);
        for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {

            Method[] arrayOfMethod = clazz.getDeclaredMethods();
            for (Method method : arrayOfMethod) {

                if (method.getName().equals(methodName) && (method.getParameterTypes()).length == argsNum) {

                    makeAccessible(method);
                    return method;
                }
            }
        }
        return null;
    }


    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) &&
                !method.isAccessible()) {
            method.setAccessible(true);
        }
    }


    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
                Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }


    public static <T> Class<T> getClassGenricType(Class clazz) {
        return getClassGenricType(clazz, 0);
    }


    public static Class getClassGenricType(Class clazz, int index) {
        Type type = clazz.getGenericSuperclass();

        if (!(type instanceof ParameterizedType)) {

            aw.debug(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }

        Type[] arrayOfType = ((ParameterizedType) type).getActualTypeArguments();

        if (index >= arrayOfType.length || index < 0) {

            aw.debug("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: " + arrayOfType.length);

            return Object.class;
        }
        if (!(arrayOfType[index] instanceof Class)) {

            aw.debug(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }

        return (Class) arrayOfType[index];
    }


    public static Class<?> getUserClass(Object instance) {
        if (instance == null) {
            throw new RuntimeException("Instance must not be null");
        }
        Class<?> clazz = instance.getClass();
        if (clazz != null && clazz.getName().contains("$$")) {

            Class<?> clazz1 = clazz.getSuperclass();
            if (clazz1 != null && !Object.class.equals(clazz1)) {
                return clazz1;
            }
        }
        return clazz;
    }


    public static RuntimeException convertReflectionExceptionToUnchecked(String msg, Exception e) {
        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException || e instanceof NoSuchMethodException) {

            return new IllegalArgumentException(msg, e);
        }
        if (e instanceof InvocationTargetException) {
            return new RuntimeException(msg, ((InvocationTargetException) e).getTargetException());
        }
        return new RuntimeException(msg, e);
    }
}
