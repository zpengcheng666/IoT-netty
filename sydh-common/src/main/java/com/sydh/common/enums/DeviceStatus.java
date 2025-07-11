package com.sydh.common.enums;

/**
 * 设备状态枚举
 */
public enum DeviceStatus {
    
    UNACTIVATED(1, "NOTACTIVE", "未激活"),
    FORBIDDEN(2, "DISABLE", "禁用"),
    ONLINE(3, "ONLINE", "在线"),
    OFFLINE(4, "OFFLINE", "离线");

    private final int type;
    private final String code;
    private final String description;

    DeviceStatus(int type, String code, String description) {
        this.type = type;
        this.code = code;
        this.description = description;
    }

    public int getType() {
        return this.type;
    }

    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    /**
     * 根据类型转换为对应的枚举
     *
     * @param type 类型值
     * @return 对应的枚举值，如果找不到则返回null
     */
    public static DeviceStatus convert(int type) {
        for (DeviceStatus deviceStatus : values()) {
            if (deviceStatus.type == type) {
                return deviceStatus;
            }
        }
        return null;
    }

    /**
     * 根据代码转换为对应的枚举
     *
     * @param code 状态代码
     * @return 对应的枚举值，如果找不到则返回null
     */
    public static DeviceStatus convert(String code) {
        for (DeviceStatus deviceStatus : values()) {
            if (deviceStatus.code.equals(code)) {
                return deviceStatus;
            }
        }
        return null;
    }
}
