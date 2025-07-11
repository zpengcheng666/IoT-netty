package com.sydh.pay.core.convert.order;

import com.sydh.common.core.domain.PageResult;
import com.sydh.common.utils.MapUtils;
import com.sydh.common.utils.collection.CollectionUtils;
import com.sydh.pay.api.api.order.dto.PayOrderCreateReqDTO;
import com.sydh.pay.api.api.order.dto.PayOrderRespDTO;
import com.sydh.pay.core.controller.admin.order.vo.*;
import com.sydh.pay.core.controller.app.order.vo.AppPayOrderSubmitRespVO;
import com.sydh.pay.core.domain.dataobject.app.PayApp;
import com.sydh.pay.core.domain.dataobject.order.PayOrder;
import com.sydh.pay.core.domain.dataobject.order.PayOrderExtension;
import com.sydh.pay.framework.client.dto.order.PayOrderUnifiedReqDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

/**
 * 支付订单 Convert
 *
 * @author aquan
 */
@Mapper
public interface PayOrderConvert {

    PayOrderConvert INSTANCE = Mappers.getMapper(PayOrderConvert.class);

    PayOrderRespVO convert(PayOrder bean);

    PayOrderRespDTO convert2(PayOrder order);

    default PayOrderDetailsRespVO convert(PayOrder order, PayOrderExtension orderExtension, PayApp app) {
        PayOrderDetailsRespVO respVO = convertDetail(order);
        respVO.setExtension(convert(orderExtension));
        if (app != null) {
            respVO.setAppName(app.getName());
        }
        return respVO;
    }
    PayOrderDetailsRespVO convertDetail(PayOrder bean);
    PayOrderDetailsRespVO.PayOrderExtension convert(PayOrderExtension bean);

    default PageResult<PayOrderPageItemRespVO> convertPage(PageResult<PayOrder> page, Map<Long, PayApp> appMap) {
        PageResult<PayOrderPageItemRespVO> result = convertPage(page);
        result.getList().forEach(order -> MapUtils.findAndThen(appMap, order.getAppId(), app -> order.setAppName(app.getName())));
        return result;
    }
    PageResult<PayOrderPageItemRespVO> convertPage(PageResult<PayOrder> page);

    default List<PayOrderExcelVO> convertList(List<PayOrder> list, Map<Long, PayApp> appMap) {
        return CollectionUtils.convertList(list, order -> {
            PayOrderExcelVO excelVO = convertExcel(order);
            MapUtils.findAndThen(appMap, order.getAppId(), app -> excelVO.setAppName(app.getName()));
            return excelVO;
        });
    }
    PayOrderExcelVO convertExcel(PayOrder bean);

    PayOrder convert(PayOrderCreateReqDTO bean);

    @Mapping(target = "id", ignore = true)
    PayOrderExtension convert(PayOrderSubmitReqVO bean, String userIp);

    PayOrderUnifiedReqDTO convert2(PayOrderSubmitReqVO reqVO, String userIp);

    @Mapping(source = "order.status", target = "status")
    PayOrderSubmitRespVO convert(PayOrder order, com.sydh.pay.framework.client.dto.order.PayOrderRespDTO respDTO);

    AppPayOrderSubmitRespVO convert3(PayOrderSubmitRespVO bean);

}
