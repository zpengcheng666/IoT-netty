package com.sydh.common.enums;

/**
 * 物模型类型枚举
 * 
 * @author sydh
 */
public enum ThingsModelType {
    
    PROP(1, "PROPERTY", "属性", "properties"),
    SERVICE(2, "FUNCTION", "服务", "functions"),
    EVENT(3, "EVENT", "事件", "events");

    private final int code;
    private final String type;
    private final String name;
    private final String list;

    ThingsModelType(int code, String type, String name, String list) {
        this.code = code;
        this.type = type;
        this.name = name;
        this.list = list;
    }

    public int getCode() {
        return this.code;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public String getList() {
        return this.list;
    }

    /**
     * 根据代码获取对应的枚举值
     * 
     * @param code 代码
     * @return 对应的枚举值，如果找不到则返回 PROP
     */
    public static ThingsModelType getType(int code) {
        for (ThingsModelType thingsModelType : values()) {
            if (thingsModelType.code == code) {
                return thingsModelType;
            }
        }
        return PROP;
    }

    /**
     * 根据类型获取对应的枚举值
     * 
     * @param type 类型
     * @return 对应的枚举值，如果找不到则返回 PROP
     */
    public static ThingsModelType getType(String type) {
        for (ThingsModelType thingsModelType : values()) {
            if (thingsModelType.type.equals(type)) {
                return thingsModelType;
            }
        }
        return PROP;
    }

    /**
     * 根据代码获取对应的列表名称
     * 
     * @param code 代码
     * @return 对应的列表名称，如果找不到则返回 properties
     */
    public static String getName(int code) {
        for (ThingsModelType thingsModelType : values()) {
            if (thingsModelType.code == code) {
                return thingsModelType.list;
            }
        }
        return PROP.list;
    }
}


