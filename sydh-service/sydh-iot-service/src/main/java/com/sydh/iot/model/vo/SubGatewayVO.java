package com.sydh.iot.model.vo;

import com.sydh.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


/**
 * 网关与子设备关联对象 iot_sub_gateway
 *
 * @author zhuangpeng.li
 * @date 2024-11-14
 */

@ApiModel(value = "SubGatewayVO", description = "网关与子设备关联 iot_sub_gateway")
@Data
public class SubGatewayVO{

    /** 业务id */
    @Excel(name = "业务id")
    @ApiModelProperty("业务id")
    private Long id;

    /** 网关设备编号 */
    @Excel(name = "网关设备编号")
    @ApiModelProperty("网关设备编号")
    private String parentClientId;

    /** 子设备编号 */
    @Excel(name = "子设备编号")
    @ApiModelProperty("子设备编号")
    private String subClientId;

    /** 从机地址 */
    @Excel(name = "从机地址")
    @ApiModelProperty("从机地址")
    private String address;

    /** 创建者 */
    @Excel(name = "创建者")
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 更新者 */
    @Excel(name = "更新者")
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 子设备名称
     */
    private String subDeviceName;
    /**
     * 子设备编号
     */
    private String subDeviceNo;

    /** 网关产品id */
    @ApiModelProperty("网关产品id")
    private Long parentProductId;

    /** 子设备产品id */
    @ApiModelProperty("子设备产品id")
    private Long subProductId;


}
