package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.sydh.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * 产品对象 iot_product
 *
 * @author zhuangpeng.li
 * @date 2024-12-20
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Product", description = "产品 iot_product")
@Data
@TableName("iot_product" )
public class Product extends BaseEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** 产品ID */
    @TableId(value = "product_id", type = IdType.AUTO)
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 产品名称 */
    @ApiModelProperty("产品名称")
    private String productName;

    /** 协议编号 */
    @ApiModelProperty("协议编号")
    private String protocolCode;

    /** 产品分类ID */
    @ApiModelProperty("产品分类ID")
    private Long categoryId;

    /** 产品分类名称 */
    @ApiModelProperty("产品分类名称")
    private String categoryName;

    /** 租户ID */
    @ApiModelProperty("租户ID")
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 是否系统通用（0-否，1-是） */
    @ApiModelProperty("是否系统通用")
    private Integer isSys;

    /** 是否启用授权码（0-否，1-是） */
    @ApiModelProperty("是否启用授权码")
    private Integer isAuthorize;

    /** mqtt账号 */
    @ApiModelProperty("认证账号")
    private String account;

    /** mqtt密码 */
    @ApiModelProperty("认证密码")
    private String authPassword;

    /** 产品秘钥 */
    @ApiModelProperty("产品认证秘钥")
    private String secret;

    /** 状态（1-未发布，2-已发布） */
    @ApiModelProperty("状态")
    private Integer status;

    /** 物模型JSON（属性、功能、事件） */
    @ApiModelProperty("物模型JSON")
    private String thingsModelsJson;

    /** 设备类型（1-直连设备、2-网关设备、3-监控设备） */
    @ApiModelProperty("设备类型")
    private Integer deviceType;

    /** 联网方式（1=wifi、2=蜂窝(2G/3G/4G/5G)、3=以太网、4=其他） */
    @ApiModelProperty("联网方式")
    private Integer networkMethod;

    /** 认证方式（1-简单认证、2-加密认证、3-简单+加密） */
    @ApiModelProperty("认证方式")
    private Integer vertificateMethod;

    /** 图片地址 */
    @ApiModelProperty("图片地址")
    private String imgUrl;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    private String delFlag;

    /** 产品支持的传输协议 */
    @ApiModelProperty("产品支持的传输协议")
    private String transport;

    /** 定位方式(1=ip自动定位，2=设备定位，3=自定义) */
    @ApiModelProperty("定位方式(1=ip自动定位，2=设备定位，3=自定义)")
    private Integer locationWay;

    /** 产品关联的组态id */
    @ApiModelProperty("产品关联的组态id")
    private String guid;

    /**
     * 固件类型(1-二进制包升级，2-http升级)
     */
    @ApiModelProperty("产品固件类型")
    private Integer firmwareType;

    /**
     * 面板启用(0-停用，1-启用)
     */
    @ApiModelProperty("面板启用")
    private Integer panelEnable;

    /**
     * 拖拽面板json数据
     */
    @ApiModelProperty("拖拽面板json数据")
    private String panelModelsJson;

    /**
     * 物模型
     */
    @TableField(exist = false)
    private String model;

}
