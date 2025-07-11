package com.sydh.common.utils.json;


import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JsonUtils {
    private static final Logger bX = LoggerFactory.getLogger(JsonUtils.class);
    private static ObjectMapper bY = new ObjectMapper();

    public static void init(ObjectMapper objectMapper) {
        bY = objectMapper;
    }

    public static String toJsonString(Object object) {
        try {
            return bY.writeValueAsString(object);
        } catch (Throwable var2) {
            // 将Throwable转换为RuntimeException抛出
            throw new RuntimeException(var2);
        }
    }

    public static byte[] toJsonByte(Object object) {
        try {
            return bY.writeValueAsBytes(object);
        } catch (Throwable var2) {
            // 将Throwable转换为RuntimeException抛出
            throw new RuntimeException(var2);
        }
    }

    public static String toJsonPrettyString(Object object) {
        try {
            return bY.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (Throwable var2) {
            // 将Throwable转换为RuntimeException抛出
            throw new RuntimeException(var2);
        }
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        if (StrUtil.isEmpty(text)) {
            return null;
        } else {
            try {
                return bY.readValue(text, clazz);
            } catch (IOException var3) {
                bX.error("json parse err,json:{}", text, var3);
                throw new RuntimeException(var3);
            }
        }
    }

    public static <T> T parseObject(String text, Type type) {
        if (StrUtil.isEmpty(text)) {
            return null;
        } else {
            try {
                return bY.readValue(text, bY.getTypeFactory().constructType(type));
            } catch (IOException var3) {
                bX.error("json parse err,json:{}", text, var3);
                throw new RuntimeException(var3);
            }
        }
    }

    public static <T> T parseObject2(String text, Class<T> clazz) {
        return StrUtil.isEmpty(text) ? null : JSONUtil.toBean(text, clazz);
    }

    public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
        if (ArrayUtil.isEmpty(bytes)) {
            return null;
        } else {
            try {
                return bY.readValue(bytes, clazz);
            } catch (IOException var3) {
                bX.error("json parse err,json:{}", bytes, var3);
                throw new RuntimeException(var3);
            }
        }
    }

    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        try {
            return bY.readValue(text, typeReference);
        } catch (IOException var3) {
            bX.error("json parse err,json:{}", text, var3);
            throw new RuntimeException(var3);
        }
    }

    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        if (StrUtil.isEmpty(text)) {
            return new ArrayList();
        } else {
            try {
                return (List)bY.readValue(text, bY.getTypeFactory().constructCollectionType(List.class, clazz));
            } catch (IOException var3) {
                bX.error("json parse err,json:{}", text, var3);
                throw new RuntimeException(var3);
            }
        }
    }

    public static JsonNode parseTree(String text) {
        try {
            return bY.readTree(text);
        } catch (IOException var2) {
            bX.error("json parse err,json:{}", text, var2);
            throw new RuntimeException(var2);
        }
    }

    public static JsonNode parseTree(byte[] text) {
        try {
            return bY.readTree(text);
        } catch (IOException var2) {
            bX.error("json parse err,json:{}", text, var2);
            throw new RuntimeException(var2);
        }
    }

    public static boolean isJson(String text) {
        return JSONUtil.isTypeJSON(text);
    }

    private JsonUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        bY.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        bY.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
