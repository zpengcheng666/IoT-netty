package com.sydh.iot.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.iot.domain.Scene;
import org.springframework.stereotype.Service;
import com.sydh.iot.mapper.SceneDeviceMapper;
import com.sydh.iot.domain.SceneDevice;
import com.sydh.iot.service.ISceneDeviceService;

import javax.annotation.Resource;

/**
 * 场景设备Service业务层处理
 *
 * @author kerwincui
 * @date 2023-12-28
 */
@Service
public class SceneDeviceServiceImpl extends ServiceImpl<SceneDeviceMapper,SceneDevice> implements ISceneDeviceService
{
    @Resource
    private SceneDeviceMapper sceneDeviceMapper;

    /**
     * 查询场景设备列表
     *
     * @param sceneDevice 场景设备
     * @return 场景设备
     */
    @Override
    public List<SceneDevice> selectSceneDeviceList(SceneDevice sceneDevice)
    {
        return sceneDeviceMapper.selectSceneDeviceList(sceneDevice);
    }

    @Override
    public int deleteSceneDeviceBySceneIds(Long[] sceneIds) {
        return sceneDeviceMapper.deleteSceneDeviceBySceneIds(sceneIds);
    }

    @Override
    public int insertSceneDeviceList(List<SceneDevice> sceneDeviceList) {
        return sceneDeviceMapper.insertSceneDeviceList(sceneDeviceList);
    }

    @Override
    public List<Scene> selectTriggerDeviceRelateScenes(SceneDevice sceneDevice) {
        return sceneDeviceMapper.selectTriggerDeviceRelateScenes(sceneDevice);
    }
}
