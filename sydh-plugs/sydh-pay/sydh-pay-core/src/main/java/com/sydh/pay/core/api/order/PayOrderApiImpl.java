package com.sydh.pay.core.api.order;

import com.sydh.pay.api.api.order.PayOrderApi;
import com.sydh.pay.api.api.order.dto.PayOrderCreateReqDTO;
import com.sydh.pay.api.api.order.dto.PayOrderRespDTO;
import com.sydh.pay.core.convert.order.PayOrderConvert;
import com.sydh.pay.core.domain.dataobject.order.PayOrder;
import com.sydh.pay.core.service.order.PayOrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 支付单 API 实现类
 *
 * @author sydh
 */
@Service
public class PayOrderApiImpl implements PayOrderApi {

    @Resource
    private PayOrderService payOrderService;

    @Override
    public Long createOrder(PayOrderCreateReqDTO reqDTO) {
        return payOrderService.createOrder(reqDTO);
    }

    @Override
    public PayOrderRespDTO getOrder(Long id) {
        PayOrder order = payOrderService.getOrder(id);
        return PayOrderConvert.INSTANCE.convert2(order);
    }

}
