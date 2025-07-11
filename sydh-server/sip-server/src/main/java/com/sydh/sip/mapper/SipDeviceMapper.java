package com.sydh.sip.mapper;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.sip.domain.SipDevice;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 监控设备Mapper接口
 *
 * @author zhuangpeng.li
 * @date 2022-10-07
 */
@Repository
public interface SipDeviceMapper extends BaseMapperX<SipDevice>
{
    public SipDevice selectSipDeviceBySipId(String sipId);
    /**
     * 查询监控设备列表
     *
     * @param sipDevice 监控设备
     * @return 监控设备集合
     */
    public List<SipDevice> selectSipDeviceList(SipDevice sipDevice);
    public List<SipDevice> selectOfflineSipDevice(String checkTimeCondition);

    /**
     * 更新设备状态
     *
     * @param sipDevice 设备
     * @return 结果
     */
    public int updateSipDeviceStatus(SipDevice sipDevice);


    public int deleteSipDeviceByByDeviceSipId(String deviceSipId);
}
