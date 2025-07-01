package com.fastbee.iot.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class IotDBDeviceLog
{

    private Long time;

    private String serialNumber;

    private Integer logType;

    private String logValue;

    private Long deviceId;

    private String deviceName;

    private String identify;

    private String createBy;

    private Integer isMonitor;

    private Integer mode;

    private String remark;

    private Long tenantId;

}
