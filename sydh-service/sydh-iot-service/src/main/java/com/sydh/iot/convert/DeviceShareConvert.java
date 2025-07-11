package com.sydh.iot.convert;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.iot.domain.DeviceShare;
import com.sydh.iot.model.vo.DeviceShareVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 设备分享Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */
@Mapper
public interface DeviceShareConvert
{

    DeviceShareConvert INSTANCE = Mappers.getMapper(DeviceShareConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param deviceShare
     * @return 设备分享集合
     */
    DeviceShareVO convertDeviceShareVO(DeviceShare deviceShare);

    /**
     * VO类转换为实体类集合
     *
     * @param deviceShareVO
     * @return 设备分享集合
     */
    DeviceShare convertDeviceShare(DeviceShareVO deviceShareVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param deviceShareList
     * @return 设备分享集合
     */
    List<DeviceShareVO> convertDeviceShareVOList(List<DeviceShare> deviceShareList);
    Page<DeviceShareVO> convertDeviceShareVOList(Page<DeviceShare> deviceShareList);
    /**
     * VO类转换为实体类
     *
     * @param deviceShareVOList
     * @return 设备分享集合
     */
    List<DeviceShare> convertDeviceShareList(List<DeviceShareVO> deviceShareVOList);
}
