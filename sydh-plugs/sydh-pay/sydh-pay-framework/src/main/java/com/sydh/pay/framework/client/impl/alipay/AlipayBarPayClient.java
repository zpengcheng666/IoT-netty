package com.sydh.pay.framework.client.impl.alipay;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.response.AlipayTradePayResponse;
import com.sydh.pay.framework.client.dto.order.PayOrderRespDTO;
import com.sydh.pay.framework.client.dto.order.PayOrderUnifiedReqDTO;
import com.sydh.pay.framework.enums.channel.PayChannelEnum;
import com.sydh.pay.framework.enums.order.PayOrderDisplayModeEnum;
import lombok.extern.slf4j.Slf4j;

import static com.sydh.common.enums.GlobalErrorCodeConstants.BAD_REQUEST;
import static com.sydh.common.exception.ServiceExceptionUtil.exception0;


/**
 * 支付宝【条码支付】的 PayClient 实现类
 * 文档：<a href="https://opendocs.alipay.com/open/194/105072">当面付</a>
 *
 * @author sydh
 */
@Slf4j
public class AlipayBarPayClient extends AbstractAlipayPayClient {

    public AlipayBarPayClient(Long channelId, AlipayPayClientConfig config) {
        super(channelId, PayChannelEnum.ALIPAY_BAR.getCode(), config);
    }

    @Override
    public PayOrderRespDTO doUnifiedOrder(PayOrderUnifiedReqDTO reqDTO) throws AlipayApiException {
        String authCode = MapUtil.getStr(reqDTO.getChannelExtras(), "auth_code");
        if (StrUtil.isEmpty(authCode)) {
            throw exception0(BAD_REQUEST.getCode(), "条形码不能为空");
        }

        // 1.1 构建 AlipayTradePayModel 请求
        AlipayTradePayModel model = new AlipayTradePayModel();
        // ① 通用的参数
        model.setOutTradeNo(reqDTO.getOutTradeNo());
        model.setSubject(reqDTO.getSubject());
        model.setBody(reqDTO.getBody());
        model.setTotalAmount(formatAmount(reqDTO.getPrice()));
        model.setScene("bar_code"); // 当面付条码支付场景
        // ② 个性化的参数
        model.setAuthCode(authCode);
        // ③ 支付宝条码支付只有一种展示
        String displayMode = PayOrderDisplayModeEnum.BAR_CODE.getMode();

        // 1.2 构建 AlipayTradePayRequest 请求
        AlipayTradePayRequest request = new AlipayTradePayRequest();
        request.setBizModel(model);
        request.setNotifyUrl(reqDTO.getNotifyUrl());
        request.setReturnUrl(reqDTO.getReturnUrl());

        // 2.1 执行请求
        AlipayTradePayResponse response = client.execute(request);
        // 2.2 处理结果
        if (!response.isSuccess()) {
            return buildClosedPayOrderRespDTO(reqDTO, response);
        }
        if ("10000".equals(response.getCode())) { // 免密支付
            return PayOrderRespDTO.successOf(response.getTradeNo(), response.getBuyerUserId(), LocalDateTimeUtil.of(response.getGmtPayment()),
                    response.getOutTradeNo(), response);
        }
        // 大额支付，需要用户输入密码，所以返回 waiting。此时，前端一般会进行轮询
        return PayOrderRespDTO.waitingOf(displayMode, "",
                reqDTO.getOutTradeNo(), response);
    }

}
