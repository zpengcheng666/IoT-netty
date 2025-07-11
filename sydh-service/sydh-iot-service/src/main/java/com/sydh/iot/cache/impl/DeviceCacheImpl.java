package com.sydh.iot.cache.impl;

import com.sydh.common.core.redis.RedisCache;
import com.sydh.common.core.redis.RedisKeyBuilder;
import com.sydh.common.enums.DeviceStatus;
import com.sydh.common.exception.ServiceException;
import com.sydh.common.extend.core.domin.mq.DeviceStatusBo;
import com.sydh.common.utils.DateUtils;
import com.sydh.iot.cache.IDeviceCache;
import com.sydh.iot.domain.Device;
import com.sydh.iot.service.IDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author bill
 */
@Service
@Slf4j
public class DeviceCacheImpl implements IDeviceCache {

    @Resource
    private RedisCache redisCache;
    @Resource
    private IDeviceService deviceService;


    /**
     * 更新设备状态
     * 如果设备状态保持不变，更新redis设备最新在线时间
     * 如果设备状态更改，更新redis同时，更新MySQL数据库设备状态
     *
     * @param dto dto
     */
    @Override
    public void updateDeviceStatusCache(DeviceStatusBo dto, Device device) {

        Optional.ofNullable(device).orElseThrow(() -> new ServiceException("设备不存在" + "[{" + dto.getSerialNumber() + "}]"));
        if (dto.getStatus() == DeviceStatus.ONLINE) {
            /*redis设备在线列表*/
            redisCache.zSetAdd(RedisKeyBuilder.buildDeviceOnlineListKey(), dto.getSerialNumber(), DateUtils.getTimestampSeconds());
            device.setStatus(DeviceStatus.ONLINE.getType());
        } else if (DeviceStatus.FORBIDDEN == dto.getStatus()) {
            /*在redis设备在线列表移除设备*/
            redisCache.zRem(RedisKeyBuilder.buildDeviceOnlineListKey(), dto.getSerialNumber());
            //更新一下mysql的设备状态为离线
            device.setStatus(DeviceStatus.FORBIDDEN.getType());
        } else {
            /*在redis设备在线列表移除设备*/
            redisCache.zRem(RedisKeyBuilder.buildDeviceOnlineListKey(), dto.getSerialNumber());
            //更新一下mysql的设备状态为离线
            device.setStatus(DeviceStatus.OFFLINE.getType());
        }
        device.setUpdateTime(DateUtils.getNowDate());
        deviceService.updateDeviceStatusAndLocation(device, dto.getIp());

    }

    /**
     * 获取设备在线总数
     *
     * @return 设备在线总数
     */
    @Override
    public long deviceOnlineTotal() {
        return redisCache.zSize(RedisKeyBuilder.buildDeviceOnlineListKey());
    }


    /**
     * 批量更新redis缓存设备状态
     *
     * @param serialNumbers 设备列表
     * @param status        状态
     */
    @Override
    public void updateBatchDeviceStatusCache(List<String> serialNumbers, DeviceStatus status) {
        if (CollectionUtils.isEmpty(serialNumbers)) {
            return;
        }
        for (String serialNumber : serialNumbers) {
            DeviceStatusBo statusBo = new DeviceStatusBo();
            statusBo.setStatus(status);
            statusBo.setSerialNumber(serialNumber);
            //this.updateDeviceStatusCache(statusBo);
        }
    }

    /**
     * 系统停止时，更新所有设备为离线状态
     */
    @PreDestroy
    public void resetDeviceStatus(){
        deviceService.reSetDeviceStatus();
    }


}
