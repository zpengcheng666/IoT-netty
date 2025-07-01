package com.fastbee.pay.core.domain.mapper.order;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.pay.core.domain.dataobject.order.PayOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PayOrderMapper extends BaseMapperX<PayOrder> {

}
