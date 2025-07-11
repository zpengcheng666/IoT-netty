package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * 物模型对象 iot_things_model
 *
 * @author zhuangpeng.li
 * @date 2024-12-23
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ThingsModel", description = "物模型 iot_things_model")
@Data
@TableName("iot_things_model" )
public class ThingsModel extends BaseEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** 物模型ID */
    @TableId(value = "model_id", type = IdType.AUTO)
    @ApiModelProperty("物模型ID")
    private Long modelId;

    /** 物模型名称 */
    @ApiModelProperty("物模型名称")
    private String modelName;

    /** 产品ID */
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 产品名称 */
    @ApiModelProperty("产品名称")
    private String productName;

    /** 租户ID */
    @ApiModelProperty("租户ID")
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 标识符，产品下唯一 */
    @ApiModelProperty("标识符，产品下唯一")
    private String identifier;

    /** 模型类别（1-属性，2-功能，3-事件） */
    @ApiModelProperty("模型类别")
    private Integer type;

    /** 数据类型（integer、decimal、string、bool、array、enum） */
    @ApiModelProperty("数据类型")
    private String datatype;

    /** 数据定义 */
    @ApiModelProperty("数据定义")
    private String specs;

    /** 计算公式 */
    @ApiModelProperty("计算公式")
    private String formula;

    /** 是否图表展示（0-否，1-是） */
    @ApiModelProperty("是否图表展示")
    private Integer isChart;

    /** 是否实时监测（0-否，1-是） */
    @ApiModelProperty("是否实时监测")
    private Integer isMonitor;

    /** 是否历史存储（0-否，1-是） */
    @ApiModelProperty("是否历史存储")
    private Integer isHistory;

    /** 是否只读数据(0-否，1-是) */
    @ApiModelProperty("是否只读数据(0-否，1-是)")
    private Integer isReadonly;

    /** 是否设备分享权限(0-否，1-是) */
    @ApiModelProperty("是否设备分享权限(0-否，1-是)")
    private Integer isSharePerm;

    /** 是否在APP显示(0-否，1-是) */
    @ApiModelProperty("是否在APP显示(0-否，1-是)")
    private Integer isApp;

    /** 排序，值越大，排序越靠前 */
    @ApiModelProperty("排序，值越大，排序越靠前")
    private Integer modelOrder;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    private String delFlag;

}
