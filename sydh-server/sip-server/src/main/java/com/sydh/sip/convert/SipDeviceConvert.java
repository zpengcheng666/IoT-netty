package com.sydh.sip.convert;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.sip.domain.SipDevice;
import com.sydh.sip.model.vo.SipDeviceVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 监控设备Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-20
 */
@Mapper
public interface SipDeviceConvert
{

    SipDeviceConvert INSTANCE = Mappers.getMapper(SipDeviceConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sipDevice
     * @return 监控设备集合
     */
    SipDeviceVO convertSipDeviceVO(SipDevice sipDevice);

    /**
     * VO类转换为实体类集合
     *
     * @param sipDeviceVO
     * @return 监控设备集合
     */
    SipDevice convertSipDevice(SipDeviceVO sipDeviceVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sipDeviceList
     * @return 监控设备集合
     */
    List<SipDeviceVO> convertSipDeviceVOList(List<SipDevice> sipDeviceList);

    /**
     * VO类转换为实体类
     *
     * @param sipDeviceVOList
     * @return 监控设备集合
     */
    List<SipDevice> convertSipDeviceList(List<SipDeviceVO> sipDeviceVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sipDevicePage
     * @return 监控设备分页
     */
    Page<SipDeviceVO> convertSipDeviceVOPage(Page<SipDevice> sipDevicePage);

    /**
     * VO类转换为实体类
     *
     * @param sipDeviceVOPage
     * @return 监控设备分页
     */
    Page<SipDevice> convertSipDevicePage(Page<SipDeviceVO> sipDeviceVOPage);
}
