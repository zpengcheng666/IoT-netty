package com.sydh.pay.core.convert.demo;

import com.sydh.common.core.domain.PageResult;
import com.sydh.pay.core.controller.admin.demo.vo.OrderInfoCreateReqVO;
import com.sydh.pay.core.controller.admin.demo.vo.OrderInfoRespVO;
import com.sydh.pay.core.domain.dataobject.demo.OrderInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 示例订单 Convert
 *
 * @author sydh
 */
@Mapper
public interface OrderInfoConvert {

    OrderInfoConvert INSTANCE = Mappers.getMapper(OrderInfoConvert.class);

    OrderInfo convert(OrderInfoCreateReqVO bean);

    OrderInfoRespVO convert(OrderInfo bean);

    PageResult<OrderInfoRespVO> convertPage(PageResult<OrderInfo> page);

}
