package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.core.domain.PageEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;


/**
 * 产品轮训任务列对象 iot_product_modbus_job
 *
 * @author zhuangpeng.li
 * @date 2024-09-04
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ProductModbusJob", description = "产品轮训任务列 iot_product_modbus_job")
@Data
@TableName("iot_product_modbus_job" )
public class ProductModbusJob extends PageEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** 任务id */
    @TableId(value = "task_id", type = IdType.AUTO)
    @ApiModelProperty("任务id")
    private Long taskId;

    /** 任务名称 */
    @ApiModelProperty("任务名称")
    private String jobName;

    /** 产品id */
    @ApiModelProperty("产品id")
    private Long productId;

    /** 指令 */
    @ApiModelProperty("指令")
    private String command;

    /** 创建者 */
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 备注信息 */
    @ApiModelProperty("备注信息")
    private String remark;

    /**
     * 状态（0正常 1暂停）
     */
    @ApiModelProperty("状态")
    private Integer status;

    /** 默认的子设备地址 */
    @ApiModelProperty("默认的子设备地址")
    private String address;

    /** 类型（1轮询指令 2下发指令） */
    private Integer commandType;

}
