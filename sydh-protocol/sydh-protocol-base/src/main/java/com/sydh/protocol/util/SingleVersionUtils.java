package com.sydh.protocol.util;

import com.sydh.protocol.base.annotation.Column;
import com.sydh.protocol.base.model.ActiveModel;
import com.sydh.protocol.base.model.ModelRegistry;
import com.sydh.protocol.base.struc.BaseStructure;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 单版本加载
 * @author bill
 */
public abstract class SingleVersionUtils {
    private static final Map<String, ActiveModel> CACHE = new WeakHashMap<>();

    public static <T> ActiveModel<T> getActiveModel(Class<T> typeClass) {
        return getActiveModel(CACHE, typeClass);
    }

    public static <T> ActiveModel<T> getActiveModel(Map<String, ActiveModel> root, Class<T> typeClass) {
        ActiveModel<T> schema = root.get(typeClass.getName());
        //不支持循环引用
        if (schema != null) return schema;

        List<Field> fs = findFields(typeClass);
        if (fs.isEmpty()) return null;

        List<BaseStructure> fieldList = findFields(root, fs);
        BaseStructure[] fields = fieldList.toArray(new BaseStructure[fieldList.size()]);
        Arrays.sort(fields);

        schema = new ActiveModel(typeClass, 0, fields);
        root.put(typeClass.getName(), schema);
        return schema;
    }

    private static List<Field> findFields(Class typeClass) {
        Field[] fields = typeClass.getDeclaredFields();
        List<Field> result = new ArrayList<>(fields.length);

        for (Field f : fields) {
            if (f.isAnnotationPresent(Column.class)) {
                result.add(f);
            }
        }
        return result;
    }

    private static List<BaseStructure> findFields(Map<String, ActiveModel> root, List<Field> fs) {
        int size = fs.size();
        List<BaseStructure> fields = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            Field f = fs.get(i);
            Column column = f.getDeclaredAnnotation(Column.class);
            if (column != null) {
                f.setAccessible(true);
                fillField(root, fields, column, f, i);
            }
        }
        return fields;
    }

    private static void fillField(Map<String, ActiveModel> root, List<BaseStructure> fields, Column column, Field f, int position) {
        BaseStructure BaseStructure = ModelRegistry.get(column, f);
        if (BaseStructure != null) {
            fields.add(BaseStructure.init(column, f, position));
        } else {
            ActiveModel schema = getActiveModel(root, ClassUtils.getGenericType(f));
            BaseStructure = ModelRegistry.get(column, f, schema);
            fields.add(BaseStructure.init(column, f, position));
        }
    }
}