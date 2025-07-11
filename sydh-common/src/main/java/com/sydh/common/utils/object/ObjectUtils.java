package com.sydh.common.utils.object;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Consumer;


public class ObjectUtils {
    public static <T> T cloneIgnoreId(T object, Consumer<T> consumer) {
        Object object1 = ObjectUtil.clone(object);

        Field field = ReflectUtil.getField(object.getClass(), "id");
        if (field != null) {
            ReflectUtil.setFieldValue(object1, field, null);
        }

        if (object1 != null) {
            consumer.accept((T) object1);
        }
        return (T) object1;
    }

    public static <T extends Comparable<T>> T max(T obj1, T obj2) {
        if (obj1 == null) {
            return obj2;
        }
        if (obj2 == null) {
            return obj1;
        }
        return (obj1.compareTo(obj2) > 0) ? obj1 : obj2;
    }

    @SafeVarargs
    public static <T> T defaultIfNull(T... array) {
        for (T t : array) {
            if (t != null) {
                return t;
            }
        }
        return null;
    }

    @SafeVarargs
    public static <T> boolean equalsAny(T obj, T... array) {
        return Arrays.<T>asList(array).contains(obj);
    }
}
