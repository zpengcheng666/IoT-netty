package com.sydh.pay.core.domain.dataobject.channel;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.sydh.common.core.domain.TenantBaseDO;
import com.sydh.common.enums.CommonStatusEnum;
import com.sydh.pay.core.domain.dataobject.app.PayApp;
import com.sydh.pay.framework.client.PayClientConfig;
import com.sydh.pay.framework.enums.channel.PayChannelEnum;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 支付渠道 DO
 * 一个应用下，会有多种支付渠道，例如说微信支付、支付宝支付等等
 * 即 PayApp : PayChannel = 1 : n
 *
 * @author sydh
 */
@TableName(value = "pay_channel", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PayChannel extends TenantBaseDO {

    /**
     * 渠道编号，数据库自增
     */
    private Long id;
    /**
     * 渠道编码
     * 枚举 {@link PayChannelEnum}
     */
    private String code;
    /**
     * 状态
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 渠道费率，单位：百分比
     */
    private Double feeRate;
    /**
     * 备注
     */
    private String remark;

    /**
     * 应用编号
     * 关联 {@link PayApp#getId()}
     */
    private Long appId;
    /**
     * 支付渠道配置
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private PayClientConfig config;

}
