package com.sydh.iot.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author fastb
 * @version 1.0
 * @description: 告警用户VO类
 * @date 2024-05-15 16:16
 */
@Data
public class DeviceAlertUserVO {

    /** 设备id */
    private Long deviceId;

    /** 用户id */
    private Long userId;

    private String userName;

    private String phoneNumber;

    private List<Long> userIdList;
}
