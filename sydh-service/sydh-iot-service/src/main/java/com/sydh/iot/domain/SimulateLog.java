package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 模拟设备日志对象 iot_simulate_log
 *
 * @author zhuangpeng.li
 * @date 2024-11-14
 */

@ApiModel(value = "SimulateLog", description = "模拟设备日志 iot_simulate_log")
@Data
@TableName("iot_simulate_log" )
public class SimulateLog implements Serializable{
    private static final long serialVersionUID=1L;

    /** 设备模拟日志ID */
    @TableId(value = "log_id", type = IdType.AUTO)
    @ApiModelProperty("设备模拟日志ID")
    private Long logId;

    /** 云端发送指令 */
    @ApiModelProperty("云端发送指令")
    private String sendData;

    /** 设备回复 */
    @ApiModelProperty("设备回复")
    private String callbackData;

    /** 设备ID */
    @ApiModelProperty("设备ID")
    private Long deviceId;

    /** 设备名称 */
    @ApiModelProperty("设备名称")
    private String deviceName;

    /** 设备编号 */
    @ApiModelProperty("设备编号")
    private String serialNumber;

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

    @Setter
    @TableField(exist = false)
    @ApiModelProperty("请求参数")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    public Map<String, Object> getParams(){
        if (params == null){
            params = new HashMap<>();
        }
        return params;
    }

}
