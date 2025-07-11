package com.sydh.sip.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流媒体服务器配置对象 media_server
 *
 * @author zhuangpeng.li
 * @date 2022-11-30
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "MediaServer", description = "流媒体服务器配置对象 media_server")
@Data
@TableName("media_server" )
public class MediaServer extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 流媒体配置ID */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("流媒体配置ID")
    private Long id;

    /** 服务器标识 */
    @ApiModelProperty("服务器标识")
    private String serverId;

    /** 租户ID */
    @ApiModelProperty("租户ID")
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 使能开关 */
    @ApiModelProperty("使能开关")
    private Long enabled;

    /** 默认播放协议 */
    @ApiModelProperty("默认播放协议")
    private String protocol;

    /** 服务器ip */
    @ApiModelProperty("服务器ip")
    private String ip;

    /** 服务器域名 */
    @ApiModelProperty("服务器域名")
    private String domainAlias;

    /** 回调服务器地址 */
    @ApiModelProperty("回调服务器地址")
    private String hookurl;

    /** 流媒体密钥 */
    @ApiModelProperty("流媒体密钥")
    private String secret;

    /** http端口 */
    @ApiModelProperty("http端口")
    private Long portHttp;

    /** https端口 */
    @ApiModelProperty("https端口")
    private Long portHttps;

    /** rtmp端口 */
    @ApiModelProperty("rtmp端口")
    private Long portRtmp;

    /** rtsp端口 */
    @ApiModelProperty("rtsp端口")
    private Long portRtsp;

    /** RTP收流端口 */
    @ApiModelProperty("RTP收流端口")
    private Long rtpProxyPort;

    /** 是否使用多端口模式 */
    @ApiModelProperty("是否使用多端口模式")
    private Long rtpEnable;

    /** rtp端口范围 */
    @ApiModelProperty("rtp端口范围")
    private String rtpPortRange;

    /** 录像服务端口 */
    @ApiModelProperty("录像服务端口")
    private Long recordPort;

    /** 是否自动同步配置ZLM */
    @ApiModelProperty("是否自动同步配置ZLM")
    private Long autoConfig;

    /** 状态 */
    @ApiModelProperty("状态")
    private Long status;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    private String delFlag;

    /** 备注 */
    @ApiModelProperty("备注")
    private String remark;

    /** ws端口 */
    @ApiModelProperty("ws端口")
    private Long portWs;
}
