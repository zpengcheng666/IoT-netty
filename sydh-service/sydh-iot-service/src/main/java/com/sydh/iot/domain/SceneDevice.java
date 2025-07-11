package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.core.domain.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * 场景设备对象 iot_scene_device
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SceneDevice", description = "场景设备 iot_scene_device")
@Data
@TableName("iot_scene_device" )
public class SceneDevice extends PageEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** 场景设备ID */
    @TableId(value = "scene_device_id", type = IdType.AUTO)
    @ApiModelProperty("场景设备ID")
    private Long sceneDeviceId;

    /** 设备编号（产品触发的没有设备编号） */
    @ApiModelProperty("设备编号")
    private String serialNumber;

    /** 产品ID */
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 产品名称 */
    @ApiModelProperty("产品名称")
    private String productName;

    /** 触发源（1=设备触发，3=产品触发） */
    @ApiModelProperty("触发源")
    private Integer source;

    /** 场景ID */
    @ApiModelProperty("场景ID")
    private Long sceneId;

    /** 场景脚本ID */
    @ApiModelProperty("场景脚本ID")
    private String scriptId;

    /** 类型（2=触发器，3=执行动作） */
    @ApiModelProperty("类型")
    private Integer type;

}
