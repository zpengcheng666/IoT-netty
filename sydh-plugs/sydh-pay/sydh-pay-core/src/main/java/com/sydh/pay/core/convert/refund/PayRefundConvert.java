package com.sydh.pay.core.convert.refund;

import com.sydh.common.core.domain.PageResult;
import com.sydh.common.utils.MapUtils;
import com.sydh.common.utils.collection.CollectionUtils;
import com.sydh.pay.api.api.refund.dto.PayRefundCreateReqDTO;
import com.sydh.pay.api.api.refund.dto.PayRefundRespDTO;
import com.sydh.pay.core.controller.admin.refund.vo.PayRefundDetailsRespVO;
import com.sydh.pay.core.controller.admin.refund.vo.PayRefundExcelVO;
import com.sydh.pay.core.controller.admin.refund.vo.PayRefundPageItemRespVO;
import com.sydh.pay.core.domain.dataobject.app.PayApp;
import com.sydh.pay.core.domain.dataobject.order.PayOrder;
import com.sydh.pay.core.domain.dataobject.refund.PayRefund;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper
public interface PayRefundConvert {

    PayRefundConvert INSTANCE = Mappers.getMapper(PayRefundConvert.class);


    default PayRefundDetailsRespVO convert(PayRefund refund, PayApp app) {
        PayRefundDetailsRespVO respVO = convert(refund);
        if (app != null) {
            respVO.setAppName(app.getName());
        }
        return respVO;
    }
    PayRefundDetailsRespVO convert(PayRefund bean);
    PayRefundDetailsRespVO.Order convert(PayOrder bean);

    default PageResult<PayRefundPageItemRespVO> convertPage(PageResult<PayRefund> page, Map<Long, PayApp> appMap) {
        PageResult<PayRefundPageItemRespVO> result = convertPage(page);
        result.getList().forEach(order -> MapUtils.findAndThen(appMap, order.getAppId(), app -> order.setAppName(app.getName())));
        return result;
    }
    PageResult<PayRefundPageItemRespVO> convertPage(PageResult<PayRefund> page);

    PayRefund convert(PayRefundCreateReqDTO bean);

    PayRefundRespDTO convert02(PayRefund bean);

    default List<PayRefundExcelVO> convertList(List<PayRefund> list, Map<Long, PayApp> appMap) {
        return CollectionUtils.convertList(list, order -> {
            PayRefundExcelVO excelVO = convertExcel(order);
            MapUtils.findAndThen(appMap, order.getAppId(), app -> excelVO.setAppName(app.getName()));
            return excelVO;
        });
    }
    PayRefundExcelVO convertExcel(PayRefund bean);

}
