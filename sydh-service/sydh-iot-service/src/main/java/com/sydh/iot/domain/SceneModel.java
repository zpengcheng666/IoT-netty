package com.sydh.iot.domain;

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

import java.io.Serializable;
import java.util.Date;

/**
 * 场景管理对象 scene_model
 *
 * @author kerwincui
 * @date 2024-05-20
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SceneModel", description = "场景管理 scene_model")
@Data
@TableName("scene_model")
public class SceneModel extends PageEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 场景管理id */
    @TableId(value = "scene_model_id", type = IdType.AUTO)
    @ApiModelProperty("场景管理id")
    private Long sceneModelId;

    /** 所属租户id */
    @ApiModelProperty("所属租户id")
    private Long tenantId;

    /** 场景管理名称 */
    @ApiModelProperty("场景管理名称")
    private String sceneModelName;

    /** 场景状态 0-停用 1-启用 */
    @ApiModelProperty("场景状态 0-停用 1-启用")
    private Integer status;

    /** 关联的组态id */
    @ApiModelProperty("关联的组态id")
    private String guid;

    /** 场景描述 */
    @ApiModelProperty("场景描述")
    private String sceneDesc;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    private String delFlag;

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

    /** 图片地址 */
    @ApiModelProperty("图片地址")
    private String imgUrl;

}
