package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * 设备分组对象 iot_device_group
 *
 * @author zhuangpeng.li
 * @date 2024-11-12
 */

@ApiModel(value = "DeviceGroup", description = "设备分组 iot_device_group")
@Data
@TableName("iot_device_group" )
public class DeviceGroup implements Serializable{
    private static final long serialVersionUID=1L;

    /** 设备ID */
    @TableId(value = "device_id", type = IdType.AUTO)
    @ApiModelProperty("设备ID")
    private Long deviceId;

    /** 分组ID */
    @ApiModelProperty("分组ID")
    private Long groupId;

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
