package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.core.domain.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * sceneModelData对象 scene_model_data
 *
 * @author sydh
 * @date 2024-12-27
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SceneModelData", description = "sceneModelData scene_model_data")
@Data
@TableName("scene_model_data" )
public class SceneModelData extends PageEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** 主键id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Long id;

    /** 场景管理id */
    @ApiModelProperty("场景管理id")
    private Long sceneModelId;

    /** 场景关联数据来源id */
    @ApiModelProperty("场景关联数据来源id")
    private Long sceneModelDeviceId;

    /** 来源类型(0设备 1录入型 2运算型) */
    @ApiModelProperty("来源类型(0设备 1录入型 2运算型)")
    private Integer variableType;

    /** 物模型或变量id */
    @ApiModelProperty("物模型或变量id")
    private Long datasourceId;

    /** 启用（0未启用 1启用） */
    @ApiModelProperty("启用")
    private Integer enable;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    private String delFlag;

    /** 物模型或变量名称 */
    @ApiModelProperty("物模型或变量名称")
    private String sourceName;

    /** 标识符 */
    @ApiModelProperty("标识符")
    private String identifier;

    /** 模型类别（1-属性，2-功能，3-事件） */
    @ApiModelProperty("模型类别")
    private Integer type;

    /** 变量单位 */
    @ApiModelProperty("变量单位")
    private String unit;

}
