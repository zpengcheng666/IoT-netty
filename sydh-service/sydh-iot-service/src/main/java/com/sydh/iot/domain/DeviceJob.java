package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * 设备定时对象 iot_device_job
 *
 * @author zhuangpeng.li
 * @date 2025-01-07
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DeviceJob", description = "设备定时 iot_device_job")
@Data
@TableName("iot_device_job" )
public class DeviceJob extends BaseEntity implements Serializable {
    private static final long serialVersionUID=1L;

    /** 任务ID */
    @TableId(value = "job_id", type = IdType.AUTO)
    @ApiModelProperty("任务ID")
    private Long jobId;

    /** 任务名称 */
    @ApiModelProperty("任务名称")
    private String jobName;

    /** 任务组名 */
    @ApiModelProperty("任务组名")
    private String jobGroup;

    /** cron执行表达式 */
    @ApiModelProperty("cron执行表达式")
    private String cronExpression;

    /** 计划执行错误策略（1立即执行 2执行一次 3放弃执行） */
    @ApiModelProperty("计划执行错误策略")
    private String misfirePolicy;

    /** 是否并发执行（0允许 1禁止） */
    @ApiModelProperty("是否并发执行")
    private String concurrent;

    /** 状态（0正常 1暂停） */
    @ApiModelProperty("状态")
    private Integer status;

    /** 设备ID */
    @ApiModelProperty("设备ID")
    private Long deviceId;

    /** 设备编号 */
    @ApiModelProperty("设备编号")
    private String serialNumber;

    /** 设备名称 */
    @ApiModelProperty("设备名称")
    private String deviceName;

    /** 是否详细corn表达式（1=是，0=否） */
    @ApiModelProperty("是否详细corn表达式")
    private Integer isAdvance;

    /** 执行的动作集合 */
    @ApiModelProperty("执行的动作集合")
    private String actions;

    /** 任务类型（1=设备定时，2=设备告警，3=场景联动） */
    @ApiModelProperty("任务类型")
    private Integer jobType;

    /** 产品ID */
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 产品名称 */
    @ApiModelProperty("产品名称")
    private String productName;

    /** 场景联动ID */
    @ApiModelProperty("场景联动ID")
    private Long sceneId;

    /** 告警ID */
    @ApiModelProperty("告警ID")
    private Long alertId;

    /** 定时告警触发器 */
    @ApiModelProperty("定时告警触发器")
    private String alertTrigger;

    /** 执行id,可共用，通过jobType区分 */
    @ApiModelProperty("执行id,可共用，通过jobType区分")
    private Long datasourceId;

}
