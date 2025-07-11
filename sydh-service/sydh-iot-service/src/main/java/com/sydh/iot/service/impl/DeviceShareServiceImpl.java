package com.sydh.iot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.extend.core.domin.model.LoginUser;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.iot.domain.Device;
import com.sydh.iot.domain.DeviceShare;
import com.sydh.iot.mapper.DeviceMapper;
import com.sydh.iot.mapper.DeviceShareMapper;
import com.sydh.iot.mapper.SceneDeviceMapper;
import com.sydh.iot.model.vo.DeviceShareVO;
import com.sydh.iot.model.vo.DeviceUserVO;
import com.sydh.iot.service.IDeviceShareService;
import com.sydh.iot.service.IDeviceUserService;
import com.sydh.iot.service.ISceneService;
import com.sydh.system.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.sydh.common.extend.utils.SecurityUtils.getLoginUser;

/**
 * @author admin
 * @version 1.0
 * @description: 设备分享Service业务层处理
 * @date 2024-11-13 10:28
 */
@Service
public class DeviceShareServiceImpl extends ServiceImpl<DeviceShareMapper, DeviceShare> implements IDeviceShareService {

    @Resource
    private DeviceShareMapper deviceShareMapper;
    @Resource
    private IDeviceUserService deviceUserService;

    @Resource
    private SceneDeviceMapper sceneDeviceMapper;
    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private ISceneService sceneService;
    @Resource
    private SysUserMapper userMapper;


    /**
     * 根据用户id、设备Id获取分享详情
     *
     * @param deviceId
     * @param userId
     * @return
     */
    @Override
    public DeviceShare selectDeviceShareByDeviceIdAndUserId(Long deviceId, Long userId) {
        LambdaQueryWrapper<DeviceShare> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceShare::getDeviceId, deviceId);
        queryWrapper.eq(DeviceShare::getUserId, userId);
        return deviceShareMapper.selectOne(queryWrapper);
    }


    /**
     * 查询设备分享 -根据设备id查询可能有多个分享用户
     *
     * @param deviceId 设备分享主键
     * @return 设备分享
     */
    @Override
    public List<DeviceShare> selectDeviceShareByDeviceId(Long deviceId) {
        LambdaQueryWrapper<DeviceShare> queryWrapper = new LambdaQueryWrapper<DeviceShare>().eq(DeviceShare::getDeviceId, deviceId);
        return deviceShareMapper.selectList(queryWrapper);
    }

    /**
     * 查询设备分享列表
     *
     * @param deviceShare 设备分享
     * @return 设备分享
     */
    @Override
    public Page<DeviceShareVO> selectDeviceShareVOList(DeviceShare deviceShare) {
        List<DeviceShareVO> result = new ArrayList<>();
        //将当前设备的所属设备用户添加到第一列
        LoginUser loginUser = getLoginUser();
        DeviceUserVO deviceUserVO = deviceUserService.selectDeviceUserByDeviceId(deviceShare.getDeviceId());
        if (loginUser.getUserId() == 1L) {
            if (!Objects.isNull(deviceUserVO)) {
                DeviceShareVO shareVO = new DeviceShareVO();
                shareVO.setDeviceId(deviceUserVO.getDeviceId())
                        .setUserId(deviceUserVO.getUserId())
                        .setIsOwner(1)
                        .setUserName(deviceUserVO.getUserName())
                        .setPhonenumber(deviceUserVO.getPhonenumber())
                        .setCreateTime(deviceUserVO.getCreateTime());
                result.add(shareVO);
            }
        }
        DeviceShare share = new DeviceShare();
        share.setDeviceId(deviceShare.getDeviceId());
        share.setUserId(deviceShare.getUserId());
        deviceShare.setUserId(null);
        Page<DeviceShareVO> deviceShareVOList = deviceShareMapper.selectDeviceShareList(new Page<>(deviceShare.getPageNum(), deviceShare.getPageSize()),deviceShare);
        for (DeviceShareVO d : deviceShareVOList.getRecords()) {
            if (Objects.equals(d.getUserId(), loginUser.getUserId())) {
                SysUser sysUser = userMapper.selectUserByUserName(d.getCreateBy());
                DeviceShareVO shareVO = new DeviceShareVO();
                shareVO.setDeviceId(d.getDeviceId())
                        .setUserId(sysUser.getUserId())
                        .setIsOwner(1)
                        .setUserName(sysUser.getUserName())
                        .setPhonenumber(sysUser.getPhonenumber())
                        .setCreateTime(d.getCreateTime());
                result.add(0,shareVO);
            } else if(loginUser.getUsername().equals(d.getCreateBy()) || loginUser.getUserId() == 1L) {
                d.setIsOwner(0);
                result.add(d);
            }
        }
        deviceShareVOList.setRecords(result);
        deviceShareVOList.setTotal(result.size());
        return deviceShareVOList;
    }

    /**
     * 新增设备分享
     *
     * @param deviceShare 设备分享
     * @return 结果
     */
    @Override
    public int insertDeviceShare(DeviceShare deviceShare) {
        assert !Objects.isNull(deviceShare.getUserId()) : "设备用户ID不能为空";
        assert !Objects.isNull(deviceShare.getDeviceId()) : "设备ID不能为空";
        DeviceShare queryDeviceShare = this.selectDeviceShareByDeviceIdAndUserId(deviceShare.getDeviceId(), deviceShare.getUserId());
        if (!Objects.isNull(queryDeviceShare)) throw new RuntimeException("该用户已添加, 禁止重复添加");
        deviceShare.setCreateTime(DateUtils.getNowDate());
        deviceShare.setCreateBy(getLoginUser().getUsername());
        return deviceShareMapper.insert(deviceShare);
    }

    /**
     * 修改设备分享
     *
     * @param deviceShare 设备分享
     * @return 结果
     */
    @Override
    public int updateDeviceShare(DeviceShare deviceShare) {
        deviceShare.setUpdateTime(DateUtils.getNowDate());
        deviceShare.setUpdateBy(getLoginUser().getUsername());
        LambdaUpdateWrapper<DeviceShare> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DeviceShare::getDeviceId, deviceShare.getDeviceId());
        return deviceShareMapper.update(deviceShare, updateWrapper);
    }

    /**
     * 批量删除设备分享
     *
     * @param deviceIds 需要删除的设备分享主键
     * @return 结果
     */
    @Override
    public int deleteDeviceShareByDeviceIds(Long[] deviceIds) {
        LambdaUpdateWrapper<DeviceShare> wrapper = new LambdaUpdateWrapper<DeviceShare>().in(DeviceShare::getDeviceId, Arrays.asList(deviceIds));
        return deviceShareMapper.delete(wrapper);
    }

    /**
     * 删除设备分享信息
     *
     * @param deviceId 设备分享主键
     * @return 结果
     */
    @Override
    public int deleteDeviceShareByDeviceId(Long deviceId) {
        LambdaUpdateWrapper<DeviceShare> wrapper = new LambdaUpdateWrapper<DeviceShare>().eq(DeviceShare::getDeviceId, deviceId);
        return deviceShareMapper.delete(wrapper);
    }

    /**
     * 查询分享设备用户
     *
     * @param share 设备分享
     * @return 设备用户集合
     */
    @Override
    public SysUser selectShareUser(DeviceShare share) {
        return deviceShareMapper.selectShareUser(share);
    }

    @Override
    public int deleteDeviceShareByDeviceIdAndUserId(DeviceShare deviceShare) {
        // 把分享用户配置的场景删掉
        Device device = deviceMapper.selectById(deviceShare.getDeviceId());
        if (null != device) {
            Long[] sceneIds = sceneDeviceMapper.listSceneIdByDeviceIdAndUserId(device.getSerialNumber(), Collections.singletonList(deviceShare.getUserId()));
            if (null != sceneIds && sceneIds.length > 0) {
                sceneService.deleteSceneBySceneIds(sceneIds);
            }
        }
        LambdaQueryWrapper<DeviceShare> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceShare::getDeviceId, deviceShare.getDeviceId());
        queryWrapper.eq(DeviceShare::getUserId, deviceShare.getUserId());
        return deviceShareMapper.delete(queryWrapper);
    }

    private LambdaQueryWrapper<DeviceShare> buildQueryWrapper(DeviceShare query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<DeviceShare> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(query.getPhonenumber()), DeviceShare::getPhonenumber, query.getPhonenumber());
        lqw.eq(StringUtils.isNotBlank(query.getPerms()), DeviceShare::getPerms, query.getPerms());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(DeviceShare::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }


    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DeviceShare entity) {
        //TODO 做一些数据校验,如唯一约束
    }

}
