package com.sydh.common.enums;

/**
 * 设备分配类型枚举
 */
public enum DeviceDistributeTypeEnum {

    SELECT(1, "选择分配"),
    IMPORT(2, "导入分配");

    private final Integer type;
    private final String desc;

    DeviceDistributeTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return this.type;
    }

    public String getDesc() {
        return this.desc;
    }

    /**
     * 根据类型获取描述
     *
     * @param type 类型值
     * @return 对应的描述，如果找不到则返回空字符串
     */
    public static String getDesc(Integer type) {
        for (DeviceDistributeTypeEnum deviceDistributeTypeEnum : values()) {
            if (deviceDistributeTypeEnum.getType().equals(type)) {
                return deviceDistributeTypeEnum.getDesc();
            }
        }
        return "";
    }
}
