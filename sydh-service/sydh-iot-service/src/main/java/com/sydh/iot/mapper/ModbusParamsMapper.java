package com.sydh.iot.mapper;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.ModbusParams;

/**
 * 产品modbus配置参数Mapper接口
 * @date 2024-08-20
 */
public interface ModbusParamsMapper extends BaseMapperX<ModbusParams>
{

    /**
     * 根据设备id获取modbus配置
     * @param clientId
     * @return
     */
    ModbusParams getModbusParamsByClientId(String clientId);
}
