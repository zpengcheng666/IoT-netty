package com.sydh.notify.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.sydh.common.core.domain.PageEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 通知日志对象 notify_log
 *
 * @author sydh
 * @date 2023-12-16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName("notify_log")
public class NotifyLog extends PageEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 通知日志ID */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("通知日志ID")
    private Long id;

    /** 渠道编号 */
    @ApiModelProperty("渠道编号")
    private Long channelId;

    /** 通知模版编号 */
    @ApiModelProperty("通知模版编号")
    private Long notifyTemplateId;

    /** 消息内容 */
    @ApiModelProperty("消息内容")
    private String msgContent;

    /** 发送账号 */
    @ApiModelProperty("发送账号")
    private String sendAccount;

    /** 发送状态 */
    @ApiModelProperty("发送状态")
    private Long sendStatus;

    /** 返回内容 */
    @ApiModelProperty("返回内容")
    private String resultContent;

    /** 业务编码(唯一启用) */
    @ApiModelProperty("业务编码(唯一启用)")
    private String serviceCode;

    /** 创建人 */
    @ApiModelProperty("创建人")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新人 */
    @ApiModelProperty("更新人")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /** 逻辑删除标识 */
    @ApiModelProperty("逻辑删除标识")
    @TableLogic
    private Integer delFlag;

    /** 租户id */
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

}
