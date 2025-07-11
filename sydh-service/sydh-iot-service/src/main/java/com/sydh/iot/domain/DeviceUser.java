package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.sydh.common.core.domain.BaseEntity;

/**
 * 设备用户对象 iot_device_user
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DeviceUser", description = "设备用户对象 iot_device_user")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName("iot_device_user")
public class DeviceUser extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 固件ID */
    @ApiModelProperty("设备ID")
    private Long deviceId;

    /** 用户ID */
    @ApiModelProperty("用户ID")
    private Long userId;

    /** 手机号码 */
    @ApiModelProperty("手机号码")
    private String phonenumber;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志（0代表存在 2代表删除）")
    @TableLogic
    private String delFlag;

}
