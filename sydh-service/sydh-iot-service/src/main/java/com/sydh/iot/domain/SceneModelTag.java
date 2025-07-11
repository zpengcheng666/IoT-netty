package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.sydh.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * 场景录入型变量对象 scene_model_tag
 *
 * @author zhuangpeng.li
 * @date 2024-12-26
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SceneModelTag", description = "场景录入型变量 scene_model_tag")
@Data
@TableName("scene_model_tag" )
public class SceneModelTag extends BaseEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** 主键id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Long id;

    /** 场景id */
    @ApiModelProperty("场景id")
    private Long sceneModelId;

    /** 录入型变量名 */
    @ApiModelProperty("录入型变量名")
    private String name;

    /** 单位 */
    @ApiModelProperty("单位")
    private String unit;

    /** 数据类型 */
    @ApiModelProperty("数据类型")
    private String dataType;

    /** 默认值 */
    @ApiModelProperty("默认值")
    private String defaultValue;

    /** 是否只读 0-否 1-是，默认0 */
    @ApiModelProperty("是否只读 0-否 1-是，默认0")
    private Integer isReadonly;

    /** 存储方式 0-不存储 1-存储 */
    @ApiModelProperty("存储方式 0-不存储 1-存储")
    private Integer storage;

    /** 变量类型 2-录入型变量 3-运算型变量 */
    @ApiModelProperty("变量类型 2-录入型变量 3-运算型变量")
    private Integer variableType;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    private String delFlag;

    /** 周期执行（0未执行 1执行） */
    @ApiModelProperty("周期执行")
    private Integer cycleExecuted;

    /** 计算公式 ${id} + ${id} */
    @ApiModelProperty("计算公式 ${id} + ${id}")
    private String formule;

    /** 显示的计算公式  A+B */
    @ApiModelProperty("显示的计算公式  A+B")
    private String aliasFormule;

    /** 时间周期方式 1-周期计算 2-自定义时间段 */
    @ApiModelProperty("时间周期方式 1-周期计算 2-自定义时间段")
    private Integer cycleType;

    /** 时间周期内容 */
    @ApiModelProperty("时间周期内容")
    private String cycle;

}
