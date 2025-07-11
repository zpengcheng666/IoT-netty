package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.annotation.Excel;
import com.sydh.common.core.domain.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 指令偏好设置对象 command_preferences
 *
 * @author kerwincui
 * @date 2024-06-29
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "CommandPreferences", description = "指令偏好设置 command_preferences")
@Data
@TableName("command_preferences")
public class CommandPreferences extends PageEntity {

    /**
     * 指令id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 指令名称
     */
    @Excel(name = "指令名称")
    @ApiModelProperty("指令名称")
    private String name;

    /**
     * 指令
     */
    @Excel(name = "指令")
    @ApiModelProperty("指令")
    private String command;

    /**
     * 设备编号
     */
    @Excel(name = "设备编号")
    @ApiModelProperty("设备编号")
    private String serialNumber;

}
