package com.sydh.iot.model.dashBoard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 大屏数据匹配bo
 * @author bill
 */
@Data
public class DeviceMatchBo {

    @NotNull(message = "设备id不能为空")
    @ApiModelProperty(value = "设备id")
    private Long deviceId;

    @ApiModelProperty(value = "数据结构")
    private Integer type;

    @NotNull(message = "属性不能为空")
    @ApiModelProperty(value = "属性")
    private String[] identifier;

    @ApiModelProperty(value = "从机id")
    private String slaveId;

}
