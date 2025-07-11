package com.sydh.pay.core.api.refund;

import com.sydh.pay.api.api.refund.PayRefundApi;
import com.sydh.pay.api.api.refund.dto.PayRefundCreateReqDTO;
import com.sydh.pay.api.api.refund.dto.PayRefundRespDTO;
import com.sydh.pay.core.convert.refund.PayRefundConvert;
import com.sydh.pay.core.service.refund.PayRefundService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 退款单 API 实现类
 *
 * @author sydh
 */
@Service
@Validated
public class PayRefundApiImpl implements PayRefundApi {

    @Resource
    private PayRefundService payRefundService;

    @Override
    public Long createRefund(PayRefundCreateReqDTO reqDTO) {
        return payRefundService.createPayRefund(reqDTO);
    }

    @Override
    public PayRefundRespDTO getRefund(Long id) {
        return PayRefundConvert.INSTANCE.convert02(payRefundService.getRefund(id));
    }

}
