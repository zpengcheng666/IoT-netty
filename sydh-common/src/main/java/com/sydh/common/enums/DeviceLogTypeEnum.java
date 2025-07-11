package com.sydh.common.enums;

/**
 * 设备日志类型枚举
 */
public enum DeviceLogTypeEnum {
    
    ATTRIBUTE_REPORT(1, "属性上报"),
    INVOKE_FUNCTION(2, "调用功能"),
    EVENT_REPORT(3, "事件上报"),
    DEVICE_UPDATE(4, "设备升级"),
    DEVICE_ONLINE(5, "设备上线"),
    DEVICE_OFFLINE(6, "设备离线"),
    SCENE_VARIABLE_REPORT(7, "场景录入、运算变量上报下发");

    private final Integer type;
    private final String desc;

    DeviceLogTypeEnum(Integer type, String desc) {
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
        for (DeviceLogTypeEnum deviceLogTypeEnum : values()) {
            if (deviceLogTypeEnum.getType().equals(type)) {
                return deviceLogTypeEnum.getDesc();
            }
        }
        return "";
    }
}
