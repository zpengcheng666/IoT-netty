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
 * 场景联动对象 iot_scene
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Scene", description = "场景联动 iot_scene")
@Data
@TableName("iot_scene" )
public class Scene extends BaseEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** 场景ID */
    @TableId(value = "scene_id", type = IdType.AUTO)
    @ApiModelProperty("场景ID")
    private Long sceneId;

    /** 场景名称 */
    @ApiModelProperty("场景名称")
    private String sceneName;

    /** 规则名称 */
    @ApiModelProperty("规则名称")
    private String chainName;

    /** 场景状态（1-启动，2-停止） */
    @ApiModelProperty("场景状态")
    private Integer enable;

    /** 用户ID */
    @ApiModelProperty("用户ID")
    private Long userId;

    /** 用户名称 */
    @ApiModelProperty("用户名称")
    private String userName;

    /** 静默周期（分钟） */
    @ApiModelProperty("静默周期")
    private Long silentPeriod;

    /** 执行条件（1=或、任意条件，2=且、所有条件，3=非，不满足） */
    @ApiModelProperty("执行条件")
    private Integer cond;

    /** 执行方式（1=串行，顺序执行，2=并行，同时执行） */
    @ApiModelProperty("执行方式")
    private Integer executeMode;

    /** 延时执行（秒钟） */
    @ApiModelProperty("延时执行")
    private Integer executeDelay;

    /** 是否包含告警推送（1=包含，2=不包含） */
    @ApiModelProperty("是否包含告警推送")
    private Integer hasAlert;

    /** 应用名称 */
    @ApiModelProperty("应用名称")
    private String applicationName;

    /** 规则数据 */
    @ApiModelProperty("规则数据")
    private String elData;

    /** 是否终端用户（1-是，0-不是） */
    @ApiModelProperty("是否终端用户")
    private Integer terminalUser;

    /** 延时匹配（秒钟） */
    @ApiModelProperty("延时匹配")
    private Integer checkDelay;

    /** 恢复告警场景ID */
    @ApiModelProperty("恢复告警场景ID")
    private Long recoverId;

}
