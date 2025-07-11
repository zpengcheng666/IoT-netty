package com.sydh.iot.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.ProductSubGateway;
import com.sydh.iot.model.gateWay.ProductSubGatewayVO;
import org.apache.ibatis.annotations.Param;

/**
 * 网关与子产品关联Mapper接口
 *
 * @author zhuangpeng.li
 * @date 2024-09-04
 */
public interface ProductSubGatewayMapper extends BaseMapperX<ProductSubGateway>
{

    /**
     * @description: 查询网关
     * @param: productSubGateway 网关
     * @return: java.util.List<com.sydh.iot.model.gateWay.ProductSubGatewayVO>
     */
    Page<ProductSubGatewayVO> selectListVO(Page<ProductSubGatewayVO> page, @Param("productSubGateway") ProductSubGateway productSubGateway);
}
