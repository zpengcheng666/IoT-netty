package com.sydh.pay.core.domain.mapper.demo;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.pay.core.domain.dataobject.demo.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 示例订单 Mapper
 *
 * @author sydh
 */
@Mapper
public interface OrderInfoMapper extends BaseMapperX<OrderInfo> {

}
