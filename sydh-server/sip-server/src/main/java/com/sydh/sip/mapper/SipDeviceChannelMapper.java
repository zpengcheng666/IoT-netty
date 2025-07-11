package com.sydh.sip.mapper;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.sip.domain.BindingChannel;
import com.sydh.sip.domain.SipDeviceChannel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 监控设备通道信息Mapper接口
 *
 * @author zhuangpeng.li
 * @date 2022-10-07
 */
@Repository
public interface SipDeviceChannelMapper extends BaseMapperX<SipDeviceChannel> {

    public SipDeviceChannel selectSipDeviceChannelByChannelSipId(String channelSipId);
    public List<SipDeviceChannel> selectSipDeviceChannelByDeviceSipId(String deviceSipId);
    List<SipDeviceChannel> selectChannelWithCivilCodeAndLength(@Param("deviceSipId") String deviceSipId, @Param("parentId") String parentId, @Param("length")Integer length);
    public List<SipDeviceChannel> selectChannelByCivilCode(@Param("deviceSipId") String deviceSipId, @Param("parentId") String parentId);
    List<SipDeviceChannel> selectChannelWithoutCiviCode(String deviceId);
    Integer getChannelMinLength(String deviceSipId);

    /**
     * 修改监控设备通道信息
     *
     * @param sipDeviceChannel 监控设备通道信息
     * @return 结果
     */
    public int updateSipDeviceChannel(SipDeviceChannel sipDeviceChannel);

    public int updateChannelStreamId(SipDeviceChannel sipDeviceChannel);


    public int deleteSipDeviceChannelByDeviceId(String deviceSipId);

    /**
     * 根据channelId获取绑定的设备或场景
     * @param channelId
     * @return
     */
    public BindingChannel getBindingChannel(String channelId);

    /**
     * @description: 查询设备关联通道
     * @param: serialNumber 设备编号
     * @return: java.util.List<com.sydh.sip.domain.SipDeviceChannel>
     */
    List<SipDeviceChannel> selectDeviceRelSipDeviceChannelList(String serialNumber);

    /**
     * @description: 查询场景关联通道
     * @param: sceneModelId  场景id
     * @return: java.util.List<com.sydh.sip.domain.SipDeviceChannel>
     */
    List<SipDeviceChannel> selectSceneRelSipDeviceChannelList(Long sceneModelId);
}
