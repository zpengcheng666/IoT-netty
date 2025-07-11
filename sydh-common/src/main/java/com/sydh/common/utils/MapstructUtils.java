/*    */ package com.sydh.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.sydh.common.utils.spring.SpringUtils;
import io.github.linpeilie.Converter;
import java.util.List;
import java.util.Map;

public class MapstructUtils {
    private static final Converter aE = (Converter)SpringUtils.getBean(Converter.class);

    public static <T, V> V convert(T source, Class<V> desc) {
        if (ObjectUtil.isNull(source)) {
            return null;
        } else {
            return ObjectUtil.isNull(desc) ? null : aE.convert(source, desc);
        }
    }

    public static <T, V> V convert(T source, V desc) {
        if (ObjectUtil.isNull(source)) {
            return null;
        } else {
            return ObjectUtil.isNull(desc) ? null : aE.convert(source, desc);
        }
    }

    public static <T, V> List<V> convert(List<T> sourceList, Class<V> desc) {
        if (ObjectUtil.isNull(sourceList)) {
            return null;
        } else {
            return (List)(CollUtil.isEmpty(sourceList) ? CollUtil.newArrayList(new Object[0]) : aE.convert(sourceList, desc));
        }
    }

    public static <T> T convert(Map<String, Object> map, Class<T> beanClass) {
        if (MapUtil.isEmpty(map)) {
            return null;
        } else {
            return ObjectUtil.isNull(beanClass) ? null : aE.convert(map, beanClass);
        }
    }

    private MapstructUtils() {
    }
}