package com.sydh.iot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.iot.convert.DeviceUserConvert;
import com.sydh.iot.domain.DeviceShare;
import com.sydh.iot.domain.DeviceUser;
import com.sydh.iot.mapper.DeviceUserMapper;
import com.sydh.iot.model.vo.DeviceUserVO;
import com.sydh.iot.service.IDeviceShareService;
import com.sydh.iot.service.IDeviceUserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * 设备用户Service业务层处理
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Service
public class DeviceUserServiceImpl extends ServiceImpl<DeviceUserMapper,DeviceUser> implements IDeviceUserService
{
    @Resource
    private DeviceUserMapper deviceUserMapper;
    @Resource
    private IDeviceShareService deviceShareService;

    /**
     * 查询设备用户
     *
     * @param deviceId 设备用户主键
     * @return 设备用户
     */
    @Override
    public DeviceUserVO selectDeviceUserByDeviceId(Long deviceId)
    {
        return deviceUserMapper.selectDeviceUserByDeviceId(deviceId);
    }

    /**
     * 查询设备用户列表
     *
     * @param deviceUser 设备用户
     * @return 设备用户
     */
    @Override
    public Page<DeviceUser> selectDeviceUserList(DeviceUser deviceUser)
    {
        return deviceUserMapper.selectDeviceUserList(new Page<>(deviceUser.getPageNum(), deviceUser.getPageSize()), deviceUser);
    }

    /**
     * 查询设备分享用户
     *
     * @param deviceUser 设备用户
     * @return 设备用户
     */
    @Override
    public SysUser selectShareUser(DeviceUser deviceUser)
    {
        return deviceUserMapper.selectShareUser(deviceUser);
    }

    /**
     * 新增设备用户
     *
     * @param deviceUser 设备用户
     * @return 结果
     */
    @Override
    public int insertDeviceUser(DeviceUser deviceUser)
    {
        List<DeviceUser> deviceUsers = selectDeviceUserList(deviceUser).getRecords();
        if (!deviceUsers.isEmpty()) { throw new RuntimeException("该用户已添加, 禁止重复添加");}
        deviceUser.setCreateTime(DateUtils.getNowDate());
        return deviceUserMapper.insert(deviceUser);
    }

    /**
     * 修改设备用户
     *
     * @param deviceUser 设备用户
     * @return 结果
     */
    @Override
    public int updateDeviceUser(DeviceUser deviceUser)
    {
        deviceUser.setUpdateTime(DateUtils.getNowDate());
        return deviceUserMapper.updateDeviceUser(deviceUser);
    }

    /**
     * 批量删除设备用户
     *
     * @param deviceIds 需要删除的设备用户主键
     * @return 结果
     */
    @Override
    public int deleteDeviceUserByDeviceIds(Long[] deviceIds)
    {
        LambdaQueryWrapper<DeviceUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DeviceUser::getDeviceId, Arrays.asList(deviceIds));
        return deviceUserMapper.delete(queryWrapper);
    }

    /**
     * 删除设备用户信息
     *
     * @param deviceId 设备用户主键
     * @return 结果
     */
    @Override
    public int deleteDeviceUserByDeviceId(Long deviceId)
    {
        LambdaQueryWrapper<DeviceUser> deviceUserWrapper = new LambdaQueryWrapper<>();
        deviceUserWrapper.eq(DeviceUser::getDeviceId, deviceId);
        return deviceUserMapper.delete(deviceUserWrapper);
    }

    @Override
    public int insertDeviceUserList(List<DeviceUser> deviceUsers) {
        try {
            deviceUsers.forEach(deviceUser -> {
                deviceUser.setCreateTime(DateUtils.getNowDate());
            });
            return deviceUserMapper.insertBatch(deviceUsers) ? 1 : 0;
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("存在设备已经与用户绑定");
        }
    }

    @Override
    public DeviceUser selectDeviceUserByDeviceIdAndUserId(Long deviceId, Long userId) {
        LambdaQueryWrapper<DeviceUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceUser::getDeviceId, deviceId);
        queryWrapper.eq(DeviceUser::getUserId, userId);
        return deviceUserMapper.selectOne(queryWrapper);
    }

    @Override
    public int deleteDeviceUser(DeviceUser deviceUser) {
        LambdaQueryWrapper<DeviceUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceUser::getDeviceId, deviceUser.getDeviceId());
        queryWrapper.eq(DeviceUser::getUserId, deviceUser.getUserId());
        return deviceUserMapper.delete(queryWrapper);
    }

    /**
     * 获取设备用户与分享用户
     * @param deviceId
     * @return
     */
    public List<DeviceUser> getDeviceUserAndShare(Long deviceId){
        List<DeviceUser> result = new ArrayList<>();
        //获取设备用户
        DeviceUserVO deviceUserVO = this.selectDeviceUserByDeviceId(deviceId);
        if (null != deviceUserVO) {
            result.add(DeviceUserConvert.INSTANCE.convertDeviceUser(deviceUserVO));
        }
        //获取分享用户
        List<DeviceShare> deviceShareList = deviceShareService.selectDeviceShareByDeviceId(deviceId);
        if (!CollectionUtils.isEmpty(deviceShareList)) {
            for (DeviceShare deviceShare : deviceShareList) {
                DeviceUser user = new DeviceUser();
                user.setDeviceId(deviceShare.getDeviceId());
                user.setUserId(deviceShare.getUserId());
                user.setPhonenumber(deviceShare.getPhonenumber());
                result.add(user);
            }
        }
        return result;
    }

    private LambdaQueryWrapper<DeviceUser> buildQueryWrapper(DeviceUser query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<DeviceUser> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(query.getPhonenumber()), DeviceUser::getPhonenumber, query.getPhonenumber());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(DeviceUser::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DeviceUser entity){
        //TODO 做一些数据校验,如唯一约束
    }
}
