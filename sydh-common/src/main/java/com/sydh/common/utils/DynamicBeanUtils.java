package com.sydh.common.utils;

import java.lang.reflect.Field;
import java.util.Map;

public class DynamicBeanUtils {

    /**
     * 将Map中的数据动态设置到目标对象的属性中
     * 
     * @param target 目标对象
     * @param propertyMap 包含属性名和值的Map
     * @return 设置属性后的目标对象
     */
    public static Object getTarget(Object target, Map<String, Object> propertyMap) {
        if (target == null || propertyMap == null || propertyMap.isEmpty()) {
            return target;
        }
        
        try {
            Class<?> clazz = target.getClass();
            
            for (Map.Entry<String, Object> entry : propertyMap.entrySet()) {
                String propertyName = entry.getKey();
                Object propertyValue = entry.getValue();
                
                try {
                    // 尝试找到对应的字段
                    Field field = findField(clazz, propertyName);
                    if (field != null) {
                        field.setAccessible(true);
                        
                        // 类型转换处理
                        Object convertedValue = convertValue(propertyValue, field.getType());
                        field.set(target, convertedValue);
                    }
                } catch (Exception e) {
                    // 如果某个属性设置失败，继续处理其他属性
                    System.err.println("Failed to set property: " + propertyName + ", error: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error setting properties to target object", e);
        }
        
        return target;
    }
    
    /**
     * 查找类中的字段，包括继承的字段
     * 
     * @param clazz 目标类
     * @param fieldName 字段名
     * @return 找到的字段，如果未找到返回null
     */
    private static Field findField(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;
        
        while (currentClass != null) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                // 在当前类中未找到，尝试父类
                currentClass = currentClass.getSuperclass();
            }
        }
        
        // 尝试查找可能的匹配字段（处理data1, data2等情况）
        currentClass = clazz;
        while (currentClass != null) {
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equalsIgnoreCase(fieldName)) {
                    return field;
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        
        return null;
    }
    
    /**
     * 类型转换
     * 
     * @param value 要转换的值
     * @param targetType 目标类型
     * @return 转换后的值
     */
    private static Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        
        if (targetType.isAssignableFrom(value.getClass())) {
            return value;
        }
        
        // String类型转换
        if (targetType == String.class) {
            return value.toString();
        }
        
        // 数字类型转换
        if (targetType == Integer.class || targetType == int.class) {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            return Integer.valueOf(value.toString());
        }
        
        if (targetType == Long.class || targetType == long.class) {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            return Long.valueOf(value.toString());
        }
        
        if (targetType == Double.class || targetType == double.class) {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            return Double.valueOf(value.toString());
        }
        
        if (targetType == Float.class || targetType == float.class) {
            if (value instanceof Number) {
                return ((Number) value).floatValue();
            }
            return Float.valueOf(value.toString());
        }
        
        if (targetType == Boolean.class || targetType == boolean.class) {
            if (value instanceof Boolean) {
                return value;
            }
            return Boolean.valueOf(value.toString());
        }
        
        // 默认返回原值
        return value;
    }

}
