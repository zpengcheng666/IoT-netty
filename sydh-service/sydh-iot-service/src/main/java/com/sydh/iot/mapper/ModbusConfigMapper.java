package com.sydh.iot.mapper;

import java.util.List;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.ModbusConfig;
import org.apache.ibatis.annotations.Param;

/**
 * modbus配置Mapper接口
 *
 * @author kerwincui
 * @date 2024-05-22
 */
public interface ModbusConfigMapper extends BaseMapperX<ModbusConfig>
{

    /**
     * 根据标识查询配置
     * @param productId
     * @param identifiers
     * @return
     */
    List<ModbusConfig> selectListByProductIdAndIdentifiers(@Param("productId") Long productId, @Param("identifiers") List<String> identifiers);
}
