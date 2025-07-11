package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.core.domain.PageEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 固件升级任务对象对象 iot_firmware_task
 *
 * @author kerwincui
 * @date 2024-08-18
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "FirmwareTask" , description = "固件升级任务对象 iot_firmware_task" )
@Data
@TableName("iot_firmware_task" )
public class FirmwareTask extends PageEntity {
private static final long serialVersionUID=1L;



    /** 主键 */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键" )
    private Long id;


    /** 任务名称 */
    @ApiModelProperty("任务名称" )
    private String taskName;


    /** 关联固件ID */
    @ApiModelProperty("关联固件ID" )
    private Long firmwareId;


    /** 1:指定设备 2:产品级别 */
    @ApiModelProperty("1:指定设备 2:产品级别" )
    private Long upgradeType;


    /** $column.columnComment */
    @ApiModelProperty("${comment}" )
    private String taskDesc;


    /** 选中的设备总数 */
    @ApiModelProperty("选中的设备总数" )
    private Long deviceAmount;


    /** $column.columnComment */
    @ApiModelProperty("${comment}" )
    @TableLogic
    private Long delFlag;


    /** $column.columnComment */
    @ApiModelProperty("${comment}" )
    @JsonFormat(pattern = "yyyy-MM-dd" )
    private Date updateTime;


    /** $column.columnComment */
    @ApiModelProperty("${comment}" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    private Date createTime;


    /** 预定时间升级 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    @ApiModelProperty("预定时间升级" )
    private Date bookTime;

}
