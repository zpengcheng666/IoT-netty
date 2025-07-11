package com.sydh.iot.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.DeviceUser;
import com.sydh.iot.model.vo.DeviceUserVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 设备用户Mapper接口
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Repository
public interface DeviceUserMapper extends BaseMapperX<DeviceUser>
{
    /**
     * 查询设备用户
     *
     * @param deviceId 设备用户主键
     * @return 设备用户
     */
    public DeviceUserVO selectDeviceUserByDeviceId(Long deviceId);

    /**
     * 查询设备用户列表
     *
     * @param deviceUser 设备用户
     * @return 设备用户集合
     */
    public Page<DeviceUser> selectDeviceUserList(Page<DeviceUser> page, @Param("deviceUser") DeviceUser deviceUser);

    /**
     * 查询设备分享用户
     *
     * @param deviceUser 设备用户
     * @return 设备用户集合
     */
    public SysUser selectShareUser(DeviceUser deviceUser);

    /**
     * 修改设备用户
     *
     * @param deviceUser 设备用户
     * @return 结果
     */
    public int updateDeviceUser(DeviceUser deviceUser);

}
