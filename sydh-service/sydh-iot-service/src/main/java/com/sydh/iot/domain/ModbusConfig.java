package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.sydh.common.core.domain.BaseEntity;
import com.sydh.common.extend.core.protocol.modbus.ModbusCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * modbus配置对象 iot_modbus_config
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ModbusConfig", description = "modbus配置 iot_modbus_config")
@Data
@TableName("iot_modbus_config" )
public class ModbusConfig extends BaseEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** 业务id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("业务id")
    private Long id;

    /** 所属产品id */
    @ApiModelProperty("所属产品id")
    private Long productId;

    /** 关联属性 */
    @ApiModelProperty("关联属性")
    private String identifier;

    /** 子设备地址 */
    @ApiModelProperty("子设备地址")
    private String address;

    /** 寄存器地址 */
    @ApiModelProperty("寄存器地址")
    private Integer register;

    /** 是否只读(0-否，1-是) */
    @ApiModelProperty("是否只读(0-否，1-是)")
    private Integer isReadonly;

    /** modbus数据类型 */
    @ApiModelProperty("modbus数据类型")
    private String dataType;

    /** 读取个数 */
    @ApiModelProperty("读取个数")
    private Integer quantity;

    /** 寄存器类型 1-IO寄存器 2-数据寄存器 */
    @ApiModelProperty("寄存器类型 1-IO寄存器 2-数据寄存器")
    private Integer type;

    /** bit位排序 */
    @ApiModelProperty("bit位排序")
    private Integer bitOrder;

    /** 排序 */
    @ApiModelProperty("排序")
    private Integer sort;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    private String delFlag;

    @TableField(exist = false)
    private ModbusCode modbusCode;

}
