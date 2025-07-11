package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.core.domain.PageEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 指令权限控制对象 order_control
 *
 * @author kerwincui
 * @date 2024-07-01
 */
@ApiModel(value = "OrderControl", description = "指令权限控制 order_control")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_control" )
public class OrderControl extends PageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Long id;

    /** 租户id */
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 逗号分隔 */
    @ApiModelProperty("逗号分隔")
    private String selectOrder;

    /** 是否生效 0-否 1-是 */
    @ApiModelProperty("是否生效 0-否 1-是")
    private Integer status;

    /** 被限制的用户id */
    @ApiModelProperty("被限制的用户id")
    private Long userId;

    /** 设备id */
    @ApiModelProperty("设备id")
    private Long deviceId;

    /** 可操作次数 */
    @ApiModelProperty("可操作次数")
    private Integer count;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("开始时间")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("结束时间")
    private Date endTime;

    /** 文件路径 */
    @ApiModelProperty("文件路径")
    private String filePath;

    /** 图片路径 */
    @ApiModelProperty("图片路径")
    private String imgUrl;

    /** 创建者 */
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新者 */
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /** 备注 */
    @ApiModelProperty("备注")
    private String remark;

}

