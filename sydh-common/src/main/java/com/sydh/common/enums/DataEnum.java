package com.sydh.common.enums;

import java.util.Objects;

/**
 * 数据类型枚举
 */
public enum DataEnum {
    
    DECIMAL("decimal", "十进制"),
    DOUBLE("double", "双精度"),
    ENUM("enum", "枚举"),
    BOOLEAN("bool", "布尔类型"),
    INTEGER("integer", "整形"),
    OBJECT("object", "对象"),
    STRING("string", "字符串"),
    ARRAY("array", "数组");

    private final String type;
    private final String msg;

    DataEnum(String type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public String getType() {
        return this.type;
    }

    public String getMsg() {
        return this.msg;
    }

    /**
     * 根据类型字符串转换为对应的枚举
     *
     * @param type 类型字符串
     * @return 对应的枚举值，如果找不到则返回STRING
     */
    public static DataEnum convert(String type) {
        for (DataEnum dataEnum : values()) {
            if (Objects.equals(dataEnum.type, type)) {
                return dataEnum;
            }
        }
        return STRING;
    }
}
