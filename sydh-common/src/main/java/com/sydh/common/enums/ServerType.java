package com.sydh.common.enums;

/**
 * 服务器类型枚举
 * 
 * @author sydh
 */
public enum ServerType {
    
    MQTT(1, "MQTT", "MQTT-BROKER"),
    COAP(2, "COAP", "COAP-SERVER"),
    TCP(3, "TCP", "TCP-SERVER"),
    UDP(4, "UDP", "UDP-SERVER"),
    WEBSOCKET(5, "WEBSOCKET", "WEBSOCKET-SERVER"),
    GB28181(6, "GB28181", "SIP-SERVER"),
    HTTP(7, "HTTP", "HTTP-SERVER"),
    OTHER(999, "OTHER", "其他");

    private final int type;
    private final String code;
    private final String des;

    ServerType(int type, String code, String des) {
        this.type = type;
        this.code = code;
        this.des = des;
    }

    public int getType() {
        return this.type;
    }

    public String getCode() {
        return this.code;
    }

    public String getDes() {
        return this.des;
    }

    /**
     * 根据代码获取对应的枚举值
     * 
     * @param code 代码
     * @return 对应的枚举值，如果找不到则返回 MQTT
     */
    public static ServerType explain(String code) {
        for (ServerType serverType : values()) {
            if (serverType.code.equals(code)) {
                return serverType;
            }
        }
        return MQTT;
    }

    /**
     * 根据类型获取对应的枚举值
     * 
     * @param type 类型
     * @return 对应的枚举值，如果找不到则返回 MQTT
     */
    public static ServerType explainByType(int type) {
        for (ServerType serverType : values()) {
            if (serverType.type == type) {
                return serverType;
            }
        }
        return MQTT;
    }
}


