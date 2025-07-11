package com.sydh.iot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.iot.domain.DeviceAlertUser;
import com.sydh.iot.mapper.DeviceAlertUserMapper;
import com.sydh.iot.model.vo.DeviceAlertUserVO;
import com.sydh.iot.service.IDeviceAlertUserService;
import com.sydh.system.service.ISysUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备告警用户Service业务层处理
 *
 * @author kerwincui
 * @date 2024-05-15
 */
@Service
public class DeviceAlertUserServiceImpl extends ServiceImpl<DeviceAlertUserMapper,DeviceAlertUser> implements IDeviceAlertUserService
{
    @Resource
    private DeviceAlertUserMapper deviceAlertUserMapper;

    @Resource
    private ISysUserService sysUserService;

    /**
     * 查询设备告警用户列表
     *
     * @param deviceAlertUser 设备告警用户
     * @return 设备告警用户
     */
    @Override
    public Page<DeviceAlertUserVO> selectDeviceAlertUserList(DeviceAlertUser deviceAlertUser)
    {
        return deviceAlertUserMapper.selectDeviceAlertUserList(new Page<>(deviceAlertUser.getPageNum(), deviceAlertUser.getPageSize()),deviceAlertUser);
    }

    /**
     * 新增设备告警用户
     *
     * @param deviceAlertUserVO 设备告警用户
     * @return 结果
     */
    @Override
    public int insertDeviceAlertUser(DeviceAlertUserVO deviceAlertUserVO)
    {
        DeviceAlertUser deviceAlertUser = new DeviceAlertUser();
        deviceAlertUser.setDeviceId(deviceAlertUserVO.getDeviceId());
        List<DeviceAlertUserVO> alertUserList = deviceAlertUserMapper.selectDeviceAlertUserList(deviceAlertUser);
        List<Long> userIdList = alertUserList.stream().map(DeviceAlertUserVO::getUserId).collect(Collectors.toList());
        List<Long> newUserIdList = deviceAlertUserVO.getUserIdList();
        newUserIdList.removeAll(userIdList);
        for (Long userId : newUserIdList) {
            DeviceAlertUser deviceAlertUser1 = new DeviceAlertUser();
            deviceAlertUser1.setUserId(userId);
            deviceAlertUser1.setDeviceId(deviceAlertUserVO.getDeviceId());
            deviceAlertUserMapper.insert(deviceAlertUser1);
        }
        return 1;
    }


    @Override
    public int deleteByDeviceIdAndUserId(Long deviceId, Long userId) {
        LambdaQueryWrapper<DeviceAlertUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceAlertUser::getDeviceId, deviceId);
        queryWrapper.eq(DeviceAlertUser::getUserId, userId);
        return deviceAlertUserMapper.delete(queryWrapper);
    }

    @Override
    public Page<SysUser> selectUserList(SysUser sysUser) {
        return sysUserService.queryUserList(sysUser);
    }
}
