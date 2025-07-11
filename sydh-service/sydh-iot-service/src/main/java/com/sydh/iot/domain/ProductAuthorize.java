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
 * 产品授权码对象 iot_product_authorize
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ProductAuthorize", description = "产品授权码 iot_product_authorize")
@Data
@TableName("iot_product_authorize" )
public class ProductAuthorize extends BaseEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** 授权码ID */
    @TableId(value = "authorize_id", type = IdType.AUTO)
    @ApiModelProperty("授权码ID")
    private Long authorizeId;

    /** 授权码 */
    @ApiModelProperty("授权码")
    private String authorizeCode;

    /** 产品ID */
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 设备ID */
    @ApiModelProperty("设备ID")
    private Long deviceId;

    /** 设备编号 */
    @ApiModelProperty("设备编号")
    private String serialNumber;

    /** 用户ID */
    @ApiModelProperty("用户ID")
    private Long userId;

    /** 用户名称 */
    @ApiModelProperty("用户名称")
    private String userName;

    /** 状态（1-未使用，2-使用中） */
    @ApiModelProperty("状态")
    private Integer status;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    private String delFlag;

    public ProductAuthorize() {
    }

    public ProductAuthorize(String authorizeCode, Long productId) {
        this.authorizeCode = authorizeCode;
        this.productId = productId;
    }

}
