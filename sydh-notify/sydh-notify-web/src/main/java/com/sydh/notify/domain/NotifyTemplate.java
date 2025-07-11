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
 * 通知模版对象 notify_template
 *
 * @author kerwincui
 * @date 2023-12-01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName("notify_template" )
public class NotifyTemplate extends PageEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("编号")
    private Long id;

    /** 渠道名称 */
    @ApiModelProperty("渠道名称")
    private String name;

    /** 业务编码(唯一启用) */
    @ApiModelProperty("业务编码(唯一启用)")
    private String serviceCode;

    /** 通知渠道账号 */
    @ApiModelProperty("通知渠道账号")
    private Long channelId;

    /** 渠道类型 */
    @ApiModelProperty("渠道类型")
    private String channelType;

    /** 服务商 */
    @ApiModelProperty("服务商")
    private String provider;

    /** 模板配置参数 */
    @ApiModelProperty("模板配置参数")
    private String msgParams;

    /** 是否启用 0-不启用 1-启用 */
    @ApiModelProperty("是否启用 0-不启用 1-启用")
    private Integer status;

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
