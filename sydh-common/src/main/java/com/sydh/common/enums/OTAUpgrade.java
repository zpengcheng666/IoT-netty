package com.sydh.common.enums;

/**
 * OTA升级状态枚举
 * 
 * @author sydh
 */
public enum OTAUpgrade {
    
    AWAIT(0, "等待升级", "等待升级"),
    SEND(1, "发送中", "发送中"),
    REPLY(2, "升级中", "升级中"),
    SUCCESS(3, "成功", "升级成功"),
    FAILED(4, "失败", "升级失败"),
    FAILED_PULL(4, "失败", "拉取固件包失败"),
    FAILED_PACKAGE_SIZE(4, "失败", "获取固件包分包大小失败"),
    FAILED_PUSH(4, "失败", "推送固件包失败"),
    STOP(5, "停止", "设备重复升级停止推送"),
    STOP_OFFLINE(5, "停止", "设备离线停止推送"),
    UNKNOWN(404, "未知", "未知错误码");

    private final Integer status;
    private final String subMsg;
    private final String des;

    OTAUpgrade(Integer status, String subMsg, String des) {
        this.status = status;
        this.subMsg = subMsg;
        this.des = des;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getSubMsg() {
        return this.subMsg;
    }

    public String getDes() {
        return this.des;
    }

    /**
     * 根据状态码解析对应的枚举值
     * 
     * @param code 状态码
     * @return 对应的枚举值，如果找不到则返回 UNKNOWN
     */
    public static OTAUpgrade parse(Integer code) {
        for (OTAUpgrade otaUpgrade : values()) {
            if (otaUpgrade.status.equals(code)) {
                return otaUpgrade;
            }
        }
        return UNKNOWN;
    }
}


