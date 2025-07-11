package com.sydh.common.enums;

/**
 * 设备记录类型枚举
 */
public enum DeviceRecordTypeEnum {

    IMPORT(1, "导入记录"),
    RECOVERY(2, "回收记录"),
    ASSIGNMENT(3, "分配记录"),
    ASSIGNMENT_DETAIL(4, "分配详细记录");

    private final Integer type;
    private final String desc;

    DeviceRecordTypeEnum(Integer type, String desc) {
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
        for (DeviceRecordTypeEnum deviceRecordTypeEnum : values()) {
            if (deviceRecordTypeEnum.getType().equals(type)) {
                return deviceRecordTypeEnum.getDesc();
            }
        }
        return "";
    }
}
