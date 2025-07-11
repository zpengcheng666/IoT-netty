package com.sydh.pay.core.domain.dataobject.refund;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.core.domain.BaseDO;
import com.sydh.pay.api.api.refund.dto.PayRefundRespDTO;
import com.sydh.pay.api.enums.refund.PayRefundStatusEnum;
import com.sydh.pay.core.domain.dataobject.app.PayApp;
import com.sydh.pay.core.domain.dataobject.channel.PayChannel;
import com.sydh.pay.core.domain.dataobject.order.PayOrder;
import com.sydh.pay.framework.enums.channel.PayChannelEnum;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 支付退款单 DO
 * 一个支付订单，可以拥有多个支付退款单
 * 即 PayOrder : PayRefund = 1 : n
 *
 * @author sydh
 */
@TableName("pay_refund")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PayRefund extends BaseDO {

    /**
     * 退款单编号，数据库自增
     */
    @TableId
    private Long id;
    /**
     * 外部退款号，根据规则生成
     * 调用支付渠道时，使用该字段作为对接的退款号：
     * 1. 微信退款：对应 <a href="https://pay.weixin.qq.com/wiki/doc/api/micropay.php?chapter=9_4">申请退款</a> 的 out_refund_no 字段
     * 2. 支付宝退款：对应 <a href="https://opendocs.alipay.com/open/02e7go"统一收单交易退款接口></a> 的 out_request_no 字段
     */
    private String no;

    /**
     * 应用编号
     * 关联 {@link PayApp#getId()}
     */
    private Long appId;
    /**
     * 渠道编号
     * 关联 {@link PayChannel#getId()}
     */
    private Long channelId;
    /**
     * 商户编码
     * 枚举 {@link PayChannelEnum}
     */
    private String channelCode;
    /**
     * 订单编号
     * 关联 {@link PayOrder#getId()}
     */
    private Long orderId;
    /**
     * 支付订单编号
     * 冗余 {@link PayOrder#getNo()}
     */
    private String orderNo;

    // ========== 商户相关字段 ==========
    /**
     * 商户订单编号
     * 例如说，内部系统 A 的订单号，需要保证每个 PayApp 唯一
     */
    private String merchantOrderId;
    /**
     * 商户退款订单号
     * 例如说，内部系统 A 的订单号，需要保证每个 PayApp 唯一
     */
    private String merchantRefundId;
    /**
     * 异步通知地址
     */
    private String notifyUrl;

    // ========== 退款相关字段 ==========
    /**
     * 退款状态
     * 枚举 {@link PayRefundStatusEnum}
     */
    private Integer status;

    /**
     * 支付金额，单位：分
     */
    private Integer payPrice;
    /**
     * 退款金额，单位：分
     */
    private Integer refundPrice;

    /**
     * 退款原因
     */
    private String reason;

    /**
     * 用户 IP
     */
    private String userIp;

    // ========== 渠道相关字段 ==========
    /**
     * 渠道订单号
     * 冗余 {@link PayOrder#getChannelOrderNo()}
     */
    private String channelOrderNo;
    /**
     * 渠道退款单号
     * 1. 微信退款：对应 <a href="https://pay.weixin.qq.com/wiki/doc/api/micropay.php?chapter=9_4">申请退款</a> 的 refund_id 字段
     * 2. 支付宝退款：没有字段
     */
    private String channelRefundNo;
    /**
     * 退款成功时间
     */
    private LocalDateTime successTime;

    /**
     * 调用渠道的错误码
     */
    private String channelErrorCode;
    /**
     * 调用渠道的错误提示
     */
    private String channelErrorMsg;

    /**
     * 支付渠道的同步/异步通知的内容
     * 对应 {@link PayRefundRespDTO#getRawData()}
     */
    private String channelNotifyData;

}
