package com.sydh.iot.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 设备MQTT连接参数实体类
 * @author fastb
 * @date 2023-08-02 17:01
 */
@Accessors(chain = true)
@Data
public class DeviceMqttConnectVO {

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 连接用户名
     */
    private String username;

    /**
     * 连接密码
     */
    private String passwd;

    /**
     * 连接端口号
     */
    private Long port;

    /**
     * 订阅主题
     */
    private String subscribeTopic;

    /**
     * 下发主题
     */
    private String reportTopic;

    /**
     * 注册包
     */
    private String enrollPackage;

    /**
     * 心跳包
     */
    private String heartbeatPackage;

}
