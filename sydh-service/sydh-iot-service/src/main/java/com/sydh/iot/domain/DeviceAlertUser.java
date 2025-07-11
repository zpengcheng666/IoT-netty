package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.core.domain.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * 设备告警用户对象 iot_device_alert_user
 *
 * @author zhuangpeng.li
 * @date 2024-11-12
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DeviceAlertUser", description = "设备告警用户 iot_device_alert_user")
@Data
@TableName("iot_device_alert_user" )
public class DeviceAlertUser extends PageEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** 设备id */
    @TableId(value = "device_id", type = IdType.AUTO)
    @ApiModelProperty("设备id")
    private Long deviceId;

    /** 用户id */
    @ApiModelProperty("用户id")
    private Long userId;

}
