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
 * 网关与子产品关联对象 iot_product_sub_gateway
 *
 * @author zhuangpeng.li
 * @date 2024-09-04
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ProductSubGateway", description = "网关与子产品关联 iot_product_sub_gateway")
@Data
@TableName("iot_product_sub_gateway" )
public class ProductSubGateway extends BaseEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** 业务id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("业务id")
    private Long id;

    /** 网关产品id */
    @ApiModelProperty("网关产品id")
    private Long gwProductId;

    /** 子产品id */
    @ApiModelProperty("子产品id")
    private Long subProductId;

    /** 从机地址 */
    @ApiModelProperty("从机地址")
    private String address;

}
