package com.sydh.iot.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.DeviceAlertUser;
import com.sydh.iot.model.vo.DeviceAlertUserVO;
import org.apache.ibatis.annotations.Param;

/**
 * 设备告警用户Mapper接口
 *
 * @author kerwincui
 * @date 2024-05-15
 */
public interface DeviceAlertUserMapper extends BaseMapperX<DeviceAlertUser>
{

    /**
     * 查询设备告警用户列表
     *
     * @param deviceAlertUser 设备告警用户
     * @return 设备告警用户集合
     */
    public List<DeviceAlertUserVO> selectDeviceAlertUserList(@Param("deviceAlertUser") DeviceAlertUser deviceAlertUser);
    public Page<DeviceAlertUserVO> selectDeviceAlertUserList(Page<DeviceAlertUserVO> page, @Param("deviceAlertUser") DeviceAlertUser deviceAlertUser);

}
