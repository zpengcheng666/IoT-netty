package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.annotation.Excel;
import com.sydh.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 设备分享对象 iot_device_share
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName("iot_device_share")
public class DeviceShare extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 设备id */
    @ApiModelProperty("设备ID")
    private Long deviceId;

    /** 用户id */
    private Long userId;

    /** 手机 */
    @Excel(name = "手机")
    private String phonenumber;

    /** 用户物模型权限，多个以英文逗号分隔 */
    @Excel(name = "用户物模型权限，多个以英文逗号分隔")
    private String perms;

    /** 删除标志（0代表存在 2代表删除） */
    @TableLogic
    private String delFlag;
}
