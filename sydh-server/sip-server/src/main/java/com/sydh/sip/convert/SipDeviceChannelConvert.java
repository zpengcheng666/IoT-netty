package com.sydh.sip.convert;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.sip.domain.SipDeviceChannel;
import com.sydh.sip.model.vo.SipDeviceChannelVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 监控设备通道信息Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-20
 */
@Mapper
public interface SipDeviceChannelConvert
{

    SipDeviceChannelConvert INSTANCE = Mappers.getMapper(SipDeviceChannelConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sipDeviceChannel
     * @return 监控设备通道信息集合
     */
    SipDeviceChannelVO convertSipDeviceChannelVO(SipDeviceChannel sipDeviceChannel);

    /**
     * VO类转换为实体类集合
     *
     * @param sipDeviceChannelVO
     * @return 监控设备通道信息集合
     */
    SipDeviceChannel convertSipDeviceChannel(SipDeviceChannelVO sipDeviceChannelVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sipDeviceChannelList
     * @return 监控设备通道信息集合
     */
    List<SipDeviceChannelVO> convertSipDeviceChannelVOList(List<SipDeviceChannel> sipDeviceChannelList);

    /**
     * VO类转换为实体类
     *
     * @param sipDeviceChannelVOList
     * @return 监控设备通道信息集合
     */
    List<SipDeviceChannel> convertSipDeviceChannelList(List<SipDeviceChannelVO> sipDeviceChannelVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sipDeviceChannelPage
     * @return 监控设备通道信息分页
     */
    Page<SipDeviceChannelVO> convertSipDeviceChannelVOPage(Page<SipDeviceChannel> sipDeviceChannelPage);

    /**
     * VO类转换为实体类
     *
     * @param sipDeviceChannelVOPage
     * @return 监控设备通道信息分页
     */
    Page<SipDeviceChannel> convertSipDeviceChannelPage(Page<SipDeviceChannelVO> sipDeviceChannelVOPage);
}
