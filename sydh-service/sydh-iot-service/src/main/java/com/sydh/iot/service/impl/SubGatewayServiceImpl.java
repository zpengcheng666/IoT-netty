package com.sydh.iot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.core.redis.RedisCache;
import com.sydh.common.core.redis.RedisKeyBuilder;
import com.sydh.common.extend.utils.SecurityUtils;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.iot.domain.ModbusParams;
import com.sydh.iot.domain.SubGateway;
import com.sydh.iot.mapper.SubGatewayMapper;
import com.sydh.iot.model.gateWay.GateSubDeviceVO;
import com.sydh.iot.model.gateWay.SubDeviceAddVO;
import com.sydh.iot.model.gateWay.SubDeviceListVO;
import com.sydh.iot.service.IModbusParamsService;
import com.sydh.iot.service.ISubGatewayService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

import static org.apache.commons.lang3.SystemUtils.getUserName;

/**
 * 网关与子设备关联Service业务层处理
 *
 * @author gsb
 * @date 2024-05-27
 */
@Service
public class SubGatewayServiceImpl extends ServiceImpl<SubGatewayMapper,SubGateway> implements ISubGatewayService
{
    @Resource
    private SubGatewayMapper gatewayMapper;
    @Resource
    private IModbusParamsService modbusParamsService;
    @Resource
    private RedisCache redisCache;

    /**
     * 查询网关与子设备关联
     *
     * @param id 网关与子设备关联主键
     * @return 网关与子设备关联
     */
    @Override
    public SubGateway selectGatewayById(Long id)
    {
        return gatewayMapper.selectById(id);
    }

    /**
     * 查询网关与子设备关联列表
     *
     * @param gateway 网关与子设备关联
     * @return 网关与子设备关联
     */
    @Override
    public Page<SubDeviceListVO> selectGatewayList(SubGateway gateway)
    {
        return gatewayMapper.selectGatewayList(new Page<>(gateway.getPageNum(),gateway.getPageSize()), gateway);
    }

    /**
     * 新增网关与子设备关联
     *
     * @param gateway 网关与子设备关联
     * @return 结果
     */
    @Override
    public int insertGateway(SubGateway gateway)
    {
        gateway.setCreateTime(DateUtils.getNowDate());
        return gatewayMapper.insert(gateway);
    }

    /**
     * 修改网关与子设备关联
     *
     * @param gateway 网关与子设备关联
     * @return 结果
     */
    @Override
    public int updateGateway(SubGateway gateway)
    {
        gateway.setUpdateTime(DateUtils.getNowDate());
        return gatewayMapper.updateById(gateway);
    }

    /**
     * 批量删除网关与子设备关联
     *
     * @param ids 需要删除的网关与子设备关联主键
     * @return 结果
     */
    @Override
    public int deleteGatewayByIds(Long[] ids)
    {
        List<SubGateway> subGatewayList = gatewayMapper.selectBatchIds(Arrays.asList(ids));
        for (SubGateway subGateway : subGatewayList) {
            // redis中删除设备协议缓存信息
            String cacheKey = RedisKeyBuilder.buildDeviceMsgCacheKey(subGateway.getSubClientId());
            redisCache.deleteObject(cacheKey);
        }
        return gatewayMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除网关与子设备关联信息
     *
     * @param id 网关与子设备关联主键
     * @return 结果
     */
    @Override
    public int deleteGatewayById(Long id)
    {
        return gatewayMapper.deleteById(id);
    }

    /**
     * 获取可选的网关子设备列表
     * @return
     */
    @Override
    public Page<GateSubDeviceVO> getIsSelectGateSubDevice(GateSubDeviceVO subDeviceVO){
        return gatewayMapper.getIsSelectGateSubDevice(new Page<>(subDeviceVO.getPageNum(), subDeviceVO.getPageSize()), subDeviceVO);
    }

    /**
     * 批量添加子设备
     * @param subDeviceAddVO
     * @return
     */
    @Override
    public int insertSubDeviceBatch(SubDeviceAddVO subDeviceAddVO){
        String[] subClientIds = subDeviceAddVO.getSubClientIds();
        List<SubGateway> list = new ArrayList<>();
        for (String clientId : subClientIds) {
            ModbusParams modbusParams = modbusParamsService.getModbusParamsByClientId(clientId);
            SubGateway subGateway = new SubGateway();
            subGateway.setSubClientId(clientId);
            subGateway.setParentClientId(subDeviceAddVO.getParentClientId());
            subGateway.setParentProductId(subDeviceAddVO.getParentProductId());
            if (!Objects.isNull(modbusParams)) {
                subGateway.setAddress(modbusParams.getAddress());
                subGateway.setSubProductId(modbusParams.getProductId());
            }
            subGateway.setCreateTime(new Date());
            subGateway.setCreateBy(SecurityUtils.getUsername());
            list.add(subGateway);
            // redis中删除设备协议缓存信息
            String cacheKey = RedisKeyBuilder.buildDeviceMsgCacheKey(clientId);
            redisCache.deleteObject(cacheKey);
        }
        return gatewayMapper.insertBatch(list) ? 1 : 0;
    }

    /**
     * 批量更新子设备
     * @param list
     * @return
     */
    @Override
    public void updateSubDeviceBatch(List<SubGateway> list){
        assert !CollectionUtils.isEmpty(list) : "集合为空";
        for (SubGateway gateway : list) {
            gateway.setUpdateBy(getUserName());
            gateway.setUpdateTime(new Date());
            this.updateGateway(gateway);
            // redis中删除设备协议缓存信息
            String cacheKey = RedisKeyBuilder.buildDeviceMsgCacheKey(gateway.getSubClientId());
            redisCache.deleteObject(cacheKey);
        }
    }

    /**
     * 根据网关设备编号查询子设备列表
     * @param gwSerialNumber
     * @return
     */
    @Override
    public List<SubGateway> getSubDeviceListByGw(String gwSerialNumber){
        LambdaQueryWrapper<SubGateway> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubGateway::getParentClientId, gwSerialNumber);
        return gatewayMapper.selectList(queryWrapper);
    }

    @Override
    public void deleteByParentClientId(String serialNumber) {
        LambdaQueryWrapper<SubGateway> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubGateway::getParentClientId, serialNumber);
        List<SubGateway> subGatewayList = gatewayMapper.selectList(queryWrapper);
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(subGatewayList)) {
            return;
        }
        gatewayMapper.delete(queryWrapper);
        for (SubGateway subGateway : subGatewayList) {
            // redis中删除设备协议缓存信息
            String cacheKey = RedisKeyBuilder.buildDeviceMsgCacheKey(subGateway.getSubClientId());
            redisCache.deleteObject(cacheKey);
        }
    }

    private LambdaQueryWrapper<SubGateway> buildQueryWrapper(SubGateway query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SubGateway> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getParentClientId() != null, SubGateway::getParentClientId, query.getParentClientId());
        lqw.eq(query.getSubClientId() != null, SubGateway::getSubClientId, query.getSubClientId());
        lqw.eq(StringUtils.isNotEmpty(query.getAddress()), SubGateway::getAddress, query.getAddress());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SubGateway::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SubGateway entity){
        //TODO 做一些数据校验,如唯一约束
    }
}
