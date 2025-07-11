package com.sydh.common.enums;

/**
 * 推送类型枚举
 * 
 * @author sydh
 */
public enum PushType {
    
    WECHAT_SERVER_PUSH("wechat_server_push", "微信小程序服务号推送");

    private final String serviceCode;
    private final String desc;

    PushType(String serviceCode, String desc) {
        this.serviceCode = serviceCode;
        this.desc = desc;
    }

    public String getServiceCode() {
        return this.serviceCode;
    }

    public String getDesc() {
        return this.desc;
    }

    /**
     * 根据服务代码获取对应的枚举值
     * 
     * @param serviceCode 服务代码
     * @return 对应的枚举值，如果找不到则返回 null
     */
    public static PushType getByServiceCode(String serviceCode) {
        for (PushType pushType : values()) {
            if (pushType.serviceCode.equals(serviceCode)) {
                return pushType;
            }
        }
        return null;
    }
}


