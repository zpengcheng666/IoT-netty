package com.sydh.iot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.iot.domain.DeviceUser;
import com.sydh.iot.model.vo.DeviceUserVO;

import java.util.List;

/**
 * 设备用户Service接口
 *
 * @author kerwincui
 * @date 2021-12-16
 */
public interface IDeviceUserService extends IService<DeviceUser>
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
    public Page<DeviceUser> selectDeviceUserList(DeviceUser deviceUser);

    /**
     * 查询分享设备用户
     *
     * @param deviceUser 设备用户
     * @return 设备用户集合
     */
    public SysUser selectShareUser(DeviceUser deviceUser);

    /**
     * 新增设备用户
     *
     * @param deviceUser 设备用户
     * @return 结果
     */
    public int insertDeviceUser(DeviceUser deviceUser);

    /**
     * 修改设备用户
     *
     * @param deviceUser 设备用户
     * @return 结果
     */
    public int updateDeviceUser(DeviceUser deviceUser);

    /**
     * 批量删除设备用户
     *
     * @param deviceIds 需要删除的设备用户主键集合
     * @return 结果
     */
    public int deleteDeviceUserByDeviceIds(Long[] deviceIds);

    /**
     * 删除设备用户信息
     *
     * @param deviceId 设备用户主键
     * @return 结果
     */
    public int deleteDeviceUserByDeviceId(Long deviceId);

    /**
     * 批量添加设备用户
     * @param deviceUsers 设备用户
     * @return 结果
     */
    public int insertDeviceUserList(List<DeviceUser> deviceUsers);

    /**
     * 查询设备用户
     *
     * @param deviceId 设备用户主键
     * @return 设备用户
     */
    public DeviceUser selectDeviceUserByDeviceIdAndUserId(Long deviceId, Long userId);

    public int deleteDeviceUser(DeviceUser deviceUser);

    /**
     * 获取设备用户与分享用户
     * @param deviceId
     * @return
     */
    public List<DeviceUser> getDeviceUserAndShare(Long deviceId);
}
