package com.sydh.pay.framework.client;


import com.sydh.pay.framework.client.dto.order.PayOrderRespDTO;
import com.sydh.pay.framework.client.dto.order.PayOrderUnifiedReqDTO;
import com.sydh.pay.framework.client.dto.refund.PayRefundRespDTO;
import com.sydh.pay.framework.client.dto.refund.PayRefundUnifiedReqDTO;

import java.util.Map;

/**
 * 支付客户端，用于对接各支付渠道的 SDK，实现发起支付、退款等功能
 *
 * @author sydh
 */
public interface PayClient {

    /**
     * 获得渠道编号
     *
     * @return 渠道编号
     */
    Long getId();

    // ============ 支付相关 ==========

    /**
     * 调用支付渠道，统一下单
     *
     * @param reqDTO 下单信息
     * @return 支付订单信息
     */
    PayOrderRespDTO unifiedOrder(PayOrderUnifiedReqDTO reqDTO);

    /**
     * 解析 order 回调数据
     *
     * @param params HTTP 回调接口 content type 为 application/x-www-form-urlencoded 的所有参数
     * @param body HTTP 回调接口的 request body
     * @return 支付订单信息
     */
    PayOrderRespDTO parseOrderNotify(Map<String, String> params, String body);

    /**
     * 获得支付订单信息
     *
     * @param outTradeNo 外部订单号
     * @return 支付订单信息
     */
    PayOrderRespDTO getOrder(String outTradeNo);

    // ============ 退款相关 ==========

    /**
     * 调用支付渠道，进行退款
     *
     * @param reqDTO  统一退款请求信息
     * @return 退款信息
     */
    PayRefundRespDTO unifiedRefund(PayRefundUnifiedReqDTO reqDTO);

    /**
     * 解析 refund 回调数据
     *
     * @param params HTTP 回调接口 content type 为 application/x-www-form-urlencoded 的所有参数
     * @param body HTTP 回调接口的 request body
     * @return 支付订单信息
     */
    PayRefundRespDTO parseRefundNotify(Map<String, String> params, String body);

    /**
     * 获得退款订单信息
     *
     * @param outTradeNo 外部订单号
     * @param outRefundNo 外部退款号
     * @return 退款订单信息
     */
    PayRefundRespDTO getRefund(String outTradeNo, String outRefundNo);

}
