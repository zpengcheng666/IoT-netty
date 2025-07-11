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

import java.util.Date;

/**
 * 固件升级任务详细对象对象 iot_firmware_task_detail
 *
 * @author kerwincui
 * @date 2024-08-18
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "FirmwareTaskDetail" , description = "固件升级任务详细对象 iot_firmware_task_detail" )
@Data
@TableName("iot_firmware_task_detail" )
public class FirmwareTaskDetail extends PageEntity {
private static final long serialVersionUID=1L;



    /** 主键 */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键" )
    private Long id;


    /** $column.columnComment */
    @ApiModelProperty("${comment}" )
    private Long taskId;


    /** 设备编码 */
    @ApiModelProperty("设备编码" )
    private String serialNumber;


    /** 0:等待升级 1:已发送设备 2:设备收到  3:升级成功 4:升级失败 */
    @ApiModelProperty("0:等待升级 1:已发送设备 2:设备收到  3:升级成功 4:升级失败" )
    private Integer upgradeStatus;


    /** 描述 */
    @ApiModelProperty("描述" )
    private String detailMsg;


    /** $column.columnComment */
    @ApiModelProperty("${comment}" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    private Date createTime;


    /** 消息ID */
    @ApiModelProperty("消息ID" )
    private String messageId;


    /** $column.columnComment */
    @ApiModelProperty("${comment}" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    private Date updateTime;

}
