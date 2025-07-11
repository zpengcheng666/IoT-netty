package com.sydh.common.utils.bean;

import org.springframework.beans.BeansException;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean 工具类，提供属性复制和 setter/getter 方法获取功能
 */
public class BeanUtils {

    private static final Logger log = LoggerFactory.getLogger(BeanUtils.class);

    private static final int MAX_METHOD_NAME_LENGTH = 3;
    private static final Pattern GETTER_PATTERN = Pattern.compile("get([A-Z][a-zA-Z0-9]*)");
    private static final Pattern SETTER_PATTERN = Pattern.compile("set([A-Z][a-zA-Z0-9]*)");

    /**
     * 复制 bean 属性
     *
     * @param src  源对象
     * @param dest 目标对象
     */
    public static void copyBeanProp(Object src, Object dest) {
        try {
            copyProperties(src, dest);
        } catch (BeansException e) {
            log.error("Bean 属性复制失败", e);
            throw e;
        }
    }

    /**
     * 获取所有 setter 方法
     *
     * @param obj 对象实例
     * @return setter 方法列表
     */
    public static List<Method> getSetterMethods(Object obj) {
        if (obj == null) {
            return new ArrayList<>();
        }

        List<Method> setters = new ArrayList<>();
        Method[] methods = obj.getClass().getMethods();

        for (Method method : methods) {
            Matcher matcher = SETTER_PATTERN.matcher(method.getName());
            if (matcher.matches() && method.getParameterTypes().length == 1) {
                setters.add(method);
            }
        }

        return setters;
    }

    /**
     * 获取所有 getter 方法
     *
     * @param obj 对象实例
     * @return getter 方法列表
     */
    public static List<Method> getGetterMethods(Object obj) {
        if (obj == null) {
            return new ArrayList<>();
        }

        List<Method> getters = new ArrayList<>();
        Method[] methods = obj.getClass().getMethods();

        for (Method method : methods) {
            Matcher matcher = GETTER_PATTERN.matcher(method.getName());
            if (matcher.matches() && method.getParameterTypes().length == 0 && !method.getReturnType().equals(void.class)) {
                getters.add(method);
            }
        }

        return getters;
    }

    /**
     * 判断两个方法名是否属于同一属性（忽略 get/set 前缀）
     *
     * @param method1 方法名1
     * @param method2 方法名2
     * @return 是否匹配
     */
    public static boolean isMethodPropEquals(String method1, String method2) {
        if (method1 == null || method2 == null || method1.length() <= MAX_METHOD_NAME_LENGTH || method2.length() <= MAX_METHOD_NAME_LENGTH) {
            return false;
        }
        return method1.substring(MAX_METHOD_NAME_LENGTH).equals(method2.substring(MAX_METHOD_NAME_LENGTH));
    }

    // 可选：将 copyProperties 提取为独立方法
    public static void copyProperties(Object source, Object target) throws BeansException {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        org.springframework.beans.BeanUtils.copyProperties(target, source);
    }
}
