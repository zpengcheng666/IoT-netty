package com.sydh.notify.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.core.domain.PageEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 通知渠道对象 notify_channel
 *
 * @author kerwincui
 * @date 2023-12-01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel(value = "NotifyChannel", description = "通知渠道 notify_channel")
@TableName("notify_channel" )
public class NotifyChannel extends PageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 编号 */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("编号")
    private Long id;

    /** 通知名称 */
    @ApiModelProperty("通知名称")
    private String name;

    /** 渠道类型 */
    @ApiModelProperty("渠道类型")
    private String channelType;

    /** 服务商 */
    @ApiModelProperty("服务商")
    private String provider;

    /** 配置内容 */
    @ApiModelProperty("配置内容")
    private String configContent;

    /** 租户id */
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

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
}
