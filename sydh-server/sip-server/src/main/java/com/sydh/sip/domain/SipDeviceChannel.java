package com.sydh.sip.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.sydh.common.core.domain.PageEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 监控设备通道信息对象 sip_device_channel
 *
 * @author zhuangpeng.li
 * @date 2023-02-23
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SipDeviceChannel", description = "监控设备通道信息 sip_device_channel")
@Data
@TableName("sip_device_channel" )
public class SipDeviceChannel extends PageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Long id;

    /**  */
    @ApiModelProperty("")
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 产品ID */
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 产品名称 */
    @ApiModelProperty("产品名称")
    private String productName;

    /** 设备SipID */
    @ApiModelProperty("设备SipID")
    private String deviceSipId;

    /** 通道SipID */
    @ApiModelProperty("通道SipID")
    private String channelSipId;

    /** 通道名称 */
    @ApiModelProperty("通道名称")
    private String channelName;

    /** 注册时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("注册时间")
    private Date registerTime;

    /** 设备类型 */
    @ApiModelProperty("设备类型")
    private String deviceType;

    /** 通道类型 */
    @ApiModelProperty("通道类型")
    private String channelType;

    /** 城市编码 */
    @ApiModelProperty("城市编码")
    private String cityCode;

    /** 行政区域 */
    @ApiModelProperty("行政区域")
    private String civilCode;

    /** 厂商名称 */
    @ApiModelProperty("厂商名称")
    private String manufacture;

    /** 产品型号 */
    @ApiModelProperty("产品型号")
    private String model;

    /** 设备归属 */
    @ApiModelProperty("设备归属")
    private String owner;

    /** 警区 */
    @ApiModelProperty("警区")
    private String block;

    /** 安装地址 */
    @ApiModelProperty("安装地址")
    private String address;

    /** 父级id */
    @ApiModelProperty("父级id")
    private String parentId;

    /** 设备入网IP */
    @ApiModelProperty("设备入网IP")
    private String ipAddress;

    /** 设备接入端口号 */
    @ApiModelProperty("设备接入端口号")
    private Integer port;

    /** 密码 */
    @ApiModelProperty("密码")
    private String password;

    /** PTZ类型 */
    @ApiModelProperty("PTZ类型")
    private Long ptzType;

    /** PTZ类型描述字符串 */
    @ApiModelProperty("PTZ类型描述字符串")
    private String ptzTypeText;

    /** 设备状态（1-未激活，2-禁用，3-在线，4-离线） */
    @ApiModelProperty("设备状态")
    private Integer status;

    /** 设备经度 */
    @ApiModelProperty("设备经度")
    private BigDecimal longitude;

    /** 设备纬度 */
    @ApiModelProperty("设备纬度")
    private BigDecimal latitude;

    /** 流媒体ID */
    @ApiModelProperty("流媒体ID")
    private String streamId;

    /** 子设备数 */
    @ApiModelProperty("子设备数")
    private Long subCount;

    /** 是否有子设备（1-有, 0-没有） */
    @ApiModelProperty("是否有子设备")
    private Integer parental;

    /** 是否含有音频（1-有, 0-没有） */
    @ApiModelProperty("是否含有音频")
    private Integer hasAudio;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    private String delFlag;

    /** 创建者 */
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新者 */
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

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
