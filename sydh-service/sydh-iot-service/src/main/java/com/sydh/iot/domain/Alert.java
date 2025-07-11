package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.sydh.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * 设备告警对象 iot_alert
 *
 * @author zhuangpeng.li
 * @date 2024-11-12
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Alert", description = "设备告警 iot_alert")
@Data
@TableName("iot_alert" )
public class Alert extends BaseEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** 告警ID */
    @TableId(value = "alert_id", type = IdType.AUTO)
    @ApiModelProperty("告警ID")
    private Long alertId;

    /** 告警名称 */
    @ApiModelProperty("告警名称")
    private String alertName;

    /** 告警级别（1=提醒通知，2=轻微问题，3=严重警告） */
    @ApiModelProperty("告警级别")
    private Long alertLevel;

    /** 告警状态（1-启动，2-停止） */
    @ApiModelProperty("告警状态")
    private Integer status;

    /** 通知方式[1,2,3] */
    @ApiModelProperty("通知方式[1,2,3]")
    private String notify;

    /** 租户id */
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

}
