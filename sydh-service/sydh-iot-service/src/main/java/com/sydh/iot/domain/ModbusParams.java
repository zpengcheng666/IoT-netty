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
 * 产品modbus配置参数对象 iot_modbus_params
 *
 * @author admin
 * @date 2024-08-20
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ModbusParams", description = "产品modbus配置参数 iot_modbus_params")
@Data
@TableName("iot_modbus_params" )
public class ModbusParams extends BaseEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** 业务id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("业务id")
    private Long id;

    /** 产品id */
    @ApiModelProperty("产品id")
    private Long productId;

    /** 是否启动云端轮训(1-云端轮训,2-边缘采集) */
    @ApiModelProperty("是否启动云端轮训(0-云端轮训,1-边缘采集)")
    private Integer pollType;

    /** 默认的子设备地址 */
    @ApiModelProperty("默认的子设备地址")
    private String address;

    /** 子设备状态判断方式 1-设备数据 2- 网关 */
    @ApiModelProperty("子设备状态判断方式 1-设备数据 2- 网关")
    private Integer statusDeter;

    /** 设备数据来判断子设备状态的时长(s) */
    @ApiModelProperty("设备数据来判断子设备状态的时长(s)")
    private String deterTimer;

    /** 批量读取的个数 */
    @ApiModelProperty("批量读取的个数")
    private Integer pollLength;

}
