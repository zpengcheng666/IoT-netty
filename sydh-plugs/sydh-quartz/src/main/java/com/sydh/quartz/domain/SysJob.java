package com.sydh.quartz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务调度表 sys_job
 *
 * @author ruoyi
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysJob", description = "定时任务调度表 sys_job")
@Data
@TableName("sys_job" )
public class SysJob extends BaseEntity
{
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

    /** 调用目标字符串 */
    @ApiModelProperty("调用目标字符串")
    private String invokeTarget;

    /** cron执行表达式 */
    @ApiModelProperty("cron执行表达式")
    private String cronExpression;

    /** cron计划策略 */
    @ApiModelProperty("计划执行错误策略")
    private String misfirePolicy;

    /** 是否并发执行（0允许 1禁止） */
    @ApiModelProperty("是否并发执行")
    private String concurrent;

    /** 状态（0正常 1暂停） */
    @ApiModelProperty("状态")
    private Integer status;

}
