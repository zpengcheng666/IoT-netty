package com.sydh.iot.cache.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sydh.common.core.redis.RedisCache;
import com.sydh.common.core.redis.RedisKeyBuilder;
import com.sydh.common.utils.StringUtils;
import com.sydh.iot.cache.IModbusConfigCache;
import com.sydh.iot.domain.ModbusConfig;
import com.sydh.iot.domain.Product;
import com.sydh.iot.enums.DeviceType;
import com.sydh.iot.mapper.ProductMapper;
import com.sydh.iot.service.IModbusConfigService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gsb
 * @date 2024/6/13 9:45
 */
@Service
public class ModbusConfigCacheImpl implements IModbusConfigCache {

    @Resource
    private IModbusConfigService modbusConfigService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private ProductMapper productMapper;

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
    @Override
    public Map<Integer, List<ModbusConfig>> getModbusConfigCacheByProductId(Long productId, String directConnAddress){
        Map<Integer, List<ModbusConfig>> resultMap = new HashMap<>();
        String modbusKey;
        if (StringUtils.isNotEmpty(directConnAddress)) {
            modbusKey = RedisKeyBuilder.buildModbusKey(productId, directConnAddress);
        } else {
            modbusKey = RedisKeyBuilder.buildModbusKey(productId);
        }
        Map<String,String> map = redisCache.hashEntity(modbusKey);
        if (!CollectionUtils.isEmpty(map)){
            for (Map.Entry<String, String> entry : map.entrySet()) {
                List<ModbusConfig> modbusConfigList = JSON.parseArray(entry.getValue(), ModbusConfig.class);
                resultMap.put(Integer.parseInt(entry.getKey()), modbusConfigList);
            }
            return resultMap;
        }
        setModbusConfigCacheByProductId(productId, directConnAddress);
        map = redisCache.hashEntity(modbusKey);
        if (!CollectionUtils.isEmpty(map)){
            for (Map.Entry<String, String> entry : map.entrySet()) {
                List<ModbusConfig> modbusConfigList = JSON.parseArray(entry.getValue(), ModbusConfig.class);
                resultMap.put(Integer.parseInt(entry.getKey()), modbusConfigList);
            }
        }
        return resultMap;
    }

    /**
     * 缓存modbus参数-返回Map
     * @param productId 产品ID
     * @param directConnAddress 直连设备地址（可为null）
     *  <p>
     *      返回key-value
     *      key: 物模型标识符
     *      value：ModbusConfig
     *  </p>
     */
    @Override
    public void setModbusConfigCacheByProductId(Long productId, String directConnAddress){
        Product product = productMapper.selectById(productId);
        Map<String,String> resultMap = new HashMap<>(2);
        ModbusConfig modbusConfig = new ModbusConfig();
        modbusConfig.setProductId(productId);
        
        // 如果指定了直连地址，则只缓存该地址的配置
        if (StringUtils.isNotEmpty(directConnAddress)) {
            modbusConfig.setAddress(directConnAddress);
        }
        
        List<ModbusConfig> modbusConfigList = modbusConfigService.selectShortListByProductId(modbusConfig);
        
        if (DeviceType.DIRECT_DEVICE.getCode() == product.getDeviceType()){
            Map<String, List<ModbusConfig>> addressMap = modbusConfigList.stream().collect(Collectors.groupingBy(ModbusConfig::getAddress));
            for (Map.Entry<String, List<ModbusConfig>> entry : addressMap.entrySet()) {
                // 如果指定了直连地址，只处理该地址的配置
                if (StringUtils.isNotEmpty(directConnAddress) && !directConnAddress.equals(entry.getKey())) {
                    continue;
                }
                
                List<ModbusConfig> modbusConfigs = entry.getValue();
                Map<Integer, List<ModbusConfig>> registerMap = modbusConfigs.stream().collect(Collectors.groupingBy(ModbusConfig::getRegister));
                Map<String,String> resultRegisterMap = new HashMap<>(2);
                for (Map.Entry<Integer, List<ModbusConfig>> registerEntry : registerMap.entrySet()) {
                    List<ModbusConfig> value = registerEntry.getValue();
                    String valueStr = JSONObject.toJSONString(value);
                    resultRegisterMap.put(registerEntry.getKey() + "", valueStr);
                }
                String modbusKey = RedisKeyBuilder.buildModbusKey(productId, entry.getKey());
                redisCache.hashPutAll(modbusKey, resultRegisterMap);
            }
        } else {
            Map<Integer, List<ModbusConfig>> listMap = modbusConfigList.stream().collect(Collectors.groupingBy(ModbusConfig::getRegister));
            for (Map.Entry<Integer, List<ModbusConfig>> entry : listMap.entrySet()) {
                List<ModbusConfig> value = entry.getValue();
                String valueStr = JSONObject.toJSONString(value);
                resultMap.put(entry.getKey()+"", valueStr);
            }
            String modbusKey = RedisKeyBuilder.buildModbusKey(productId);
            redisCache.hashPutAll(modbusKey, resultMap);
        }
    }

    /**
     * 缓存modbus参数-返回Map（保持向后兼容）
     * @param productId 产品ID
     */
    public void setModbusConfigCacheByProductId(Long productId){
        setModbusConfigCacheByProductId(productId, null);
    }

    /**
     * 获取单个modbus参数缓存值
     * @param productId 产品id
     * @param register 标识符
     * @return ModbusConfig
     */
    @Override
    public List<ModbusConfig> getSingleModbusConfig(Long productId, Integer register){
        String modbusKey = RedisKeyBuilder.buildModbusKey(productId);
        String cacheMapValue = redisCache.getCacheMapValue(modbusKey, String.valueOf(register));
        if (!StringUtils.isEmpty(cacheMapValue)){
            return JSON.parseArray(cacheMapValue, ModbusConfig.class);
        }
        ModbusConfig modbusConfig = new ModbusConfig();
        modbusConfig.setProductId(productId);
        modbusConfig.setRegister(register);
        List<ModbusConfig> modbusConfigList = modbusConfigService.selectShortListByProductId(modbusConfig);
        if (!CollectionUtils.isEmpty(modbusConfigList)){
            setModbusConfigCacheByProductId(productId);
        }
        return modbusConfigList;
    }

}
