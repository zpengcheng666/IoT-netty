package com.sydh.sip.model.vo;

import com.sydh.common.annotation.Excel;
import com.sydh.common.core.domain.PageEntity;
import com.sydh.sip.domain.BindingChannel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 监控设备通道信息对象 sip_device_channel
 *
 * @author zhuangpeng.li
 * @date 2024-11-20
 */

@ApiModel(value = "SipDeviceChannelVO", description = "监控设备通道信息 sip_device_channel")
@Data
@EqualsAndHashCode(callSuper = true)
public class SipDeviceChannelVO extends PageEntity {

    /** 主键 */
    @Excel(name = "主键")
    @ApiModelProperty("主键")
    private Long id;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 产品ID */
    @Excel(name = "产品ID")
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 产品名称 */
    @Excel(name = "产品名称")
    @ApiModelProperty("产品名称")
    private String productName;

    /** 设备SipID */
    @Excel(name = "设备SipID")
    @ApiModelProperty("设备SipID")
    private String deviceSipId;

    /** 通道SipID */
    @Excel(name = "通道SipID")
    @ApiModelProperty("通道SipID")
    private String channelSipId;

    /** 通道名称 */
    @Excel(name = "通道名称")
    @ApiModelProperty("通道名称")
    private String channelName;

    /** 注册时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("注册时间")
    @Excel(name = "注册时间")
    private Date registerTime;

    /** 设备类型 */
    @Excel(name = "设备类型")
    @ApiModelProperty("设备类型")
    private String deviceType;

    /** 通道类型 */
    @Excel(name = "通道类型")
    @ApiModelProperty("通道类型")
    private String channelType;

    /** 城市编码 */
    @Excel(name = "城市编码")
    @ApiModelProperty("城市编码")
    private String cityCode;

    /** 行政区域 */
    @Excel(name = "行政区域")
    @ApiModelProperty("行政区域")
    private String civilCode;

    /** 厂商名称 */
    @Excel(name = "厂商名称")
    @ApiModelProperty("厂商名称")
    private String manufacture;

    /** 产品型号 */
    @Excel(name = "产品型号")
    @ApiModelProperty("产品型号")
    private String model;

    /** 设备归属 */
    @Excel(name = "设备归属")
    @ApiModelProperty("设备归属")
    private String owner;

    /** 警区 */
    @Excel(name = "警区")
    @ApiModelProperty("警区")
    private String block;

    /** 安装地址 */
    @Excel(name = "安装地址")
    @ApiModelProperty("安装地址")
    private String address;

    /** 父级id */
    @Excel(name = "父级id")
    @ApiModelProperty("父级id")
    private String parentId;

    /** 设备入网IP */
    @Excel(name = "设备入网IP")
    @ApiModelProperty("设备入网IP")
    private String ipAddress;

    /** 设备接入端口号 */
    @Excel(name = "设备接入端口号")
    @ApiModelProperty("设备接入端口号")
    private Integer port;

    /** 密码 */
    @Excel(name = "密码")
    @ApiModelProperty("密码")
    private String password;

    /** PTZ类型 */
    @Excel(name = "PTZ类型")
    @ApiModelProperty("PTZ类型")
    private Long ptzType;

    /** PTZ类型描述字符串 */
    @Excel(name = "PTZ类型描述字符串")
    @ApiModelProperty("PTZ类型描述字符串")
    private String ptzTypeText;

    /** 设备状态（1-未激活，2-禁用，3-在线，4-离线） */
    @ApiModelProperty("设备状态")
    @Excel(name = "设备状态")
    private Integer status;

    /** 设备经度 */
    @Excel(name = "设备经度")
    @ApiModelProperty("设备经度")
    private BigDecimal longitude;

    /** 设备纬度 */
    @Excel(name = "设备纬度")
    @ApiModelProperty("设备纬度")
    private BigDecimal latitude;

    /** 流媒体ID */
    @Excel(name = "流媒体ID")
    @ApiModelProperty("流媒体ID")
    private String streamId;

    /** 子设备数 */
    @Excel(name = "子设备数")
    @ApiModelProperty("子设备数")
    private Long subCount;

    /** 是否有子设备（1-有, 0-没有） */
    @ApiModelProperty("是否有子设备")
    @Excel(name = "是否有子设备")
    private Integer parental;

    /** 是否含有音频（1-有, 0-没有） */
    @ApiModelProperty("是否含有音频")
    @Excel(name = "是否含有音频")
    private Integer hasAudio;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @Excel(name = "删除标志")
    private String delFlag;

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
     * 推流状态（0-未使用，1-推流中）
     */
    @ApiModelProperty("推流状态（0-未使用，1-推流中）")
    @Excel(name = "推流状态", readConverterExp = "0-未使用，1-推流中")
    private Integer streamPush = 0;
    /**
     * 直播录像状态（0-未使用，1-录像中）
     */
    @ApiModelProperty("直播录像状态（0-未使用，1-录像中）")
    @Excel(name = "直播录像状态", readConverterExp = "0-未使用，1-录像中")
    private Integer streamRecord = 0;

    /**
     * 录像转存状态（0-未使用，1-转存中）
     */
    @ApiModelProperty("录像转存状态（0-未使用，1-转存中）")
    @Excel(name = "录像转存状态", readConverterExp = "0-未使用，1-转存中")
    private Integer videoRecord = 0;

    @ApiModelProperty("关联信息")
    private BindingChannel bindingChannel;

    /**
     * 设备id
     */
    @ApiModelProperty("关联设备id")
    private Long reDeviceId;

    /**
     * 直播流
     */
    private String playUrl;

    /**
     * 场景id
     */
    @ApiModelProperty("关联场景id")
    private Long reSceneModelId;
    @ApiModelProperty("关联设备名称")
    private String reDeviceName;
    @ApiModelProperty("关联场景名称")
    private String reSceneModelName;

}
