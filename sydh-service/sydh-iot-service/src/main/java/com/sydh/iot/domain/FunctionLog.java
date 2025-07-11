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
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;


/**
 * 设备服务下发日志对象 iot_function_log
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "FunctionLog", description = "设备服务下发日志 iot_function_log")
@Data
@TableName("iot_function_log" )
@Accessors(chain = true)
public class FunctionLog extends PageEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** 设备功能日志ID */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("设备功能日志ID")
    private Long id;

    /** 标识符 */
    @ApiModelProperty("标识符")
    private String identify;

    /** 1==服务下发，2=属性获取，3.OTA升级 */
    @ApiModelProperty("1==服务下发，2=属性获取，3.OTA升级")
    private Integer funType;

    /** 日志值 */
    @ApiModelProperty("日志值")
    private String funValue;

    /** 消息id */
    @ApiModelProperty("消息id")
    private String messageId;

    /** 设备名称 */
    @ApiModelProperty("设备名称")
    private String deviceName;

    /** 设备编号 */
    @ApiModelProperty("设备编号")
    private String serialNumber;

    /** 模式(1=影子模式，2=在线模式，3=其他) */
    @ApiModelProperty("模式(1=影子模式，2=在线模式，3=其他)")
    private Integer mode;

    /** 用户id */
    @ApiModelProperty("用户id")
    private Long userId;

    /** 下发结果描述 */
    @ApiModelProperty("下发结果描述")
    private String resultMsg;

    /** 下发结果代码 */
    @ApiModelProperty("下发结果代码")
    private Integer resultCode;

    /** 创建者 */
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 备注 */
    @ApiModelProperty("备注")
    private String remark;

    /** 显示值 */
    @ApiModelProperty("显示值")
    private String showValue;

    /** 物模型名称 */
    @ApiModelProperty("物模型名称")
    private String modelName;

    /** 设备回复时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("设备回复时间")
    private Date replyTime;

}
