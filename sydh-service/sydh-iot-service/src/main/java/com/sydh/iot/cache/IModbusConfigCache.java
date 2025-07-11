package com.sydh.iot.cache;

import com.sydh.iot.domain.ModbusConfig;

import java.util.List;
import java.util.Map;

/**
 * 产品对应modbus配置参数缓存
 * @author gsb
 * @date 2024/6/13 9:44
 */
public interface IModbusConfigCache {


    /**
     * 获取modbus参数 --返回 Map<String, ModbusConfig>
     * @param productId
     * @return
     *  <p>
     *      返回key-value
     *      key: 物模型标识符
     *      value：ModbusConfig
     *  </p>
     */
    Map<Integer, List<ModbusConfig>> getModbusConfigCacheByProductId(Long productId, String directConnAddress);


    /**
     * 缓存modbus参数-返回Map
     * @param productId
     *  <p>
     *      返回key-value
     *      key: 物模型标识符
     *      value：ModbusConfig
     *  </p>
     */
    void setModbusConfigCacheByProductId(Long productId);

    /**
     * 缓存modbus参数-返回Map（支持直连地址）
     * @param productId 产品ID
     * @param directConnAddress 直连设备地址（可为null）
     *  <p>
     *      返回key-value
     *      key: 物模型标识符
     *      value：ModbusConfig
     *  </p>
     */
    void setModbusConfigCacheByProductId(Long productId, String directConnAddress);

    /**
     * 获取单个modbus参数缓存值
     * @param productId 产品id
     * @param register 标识符
     * @return ModbusConfig
     */
    public List<ModbusConfig> getSingleModbusConfig(Long productId, Integer register);
}
