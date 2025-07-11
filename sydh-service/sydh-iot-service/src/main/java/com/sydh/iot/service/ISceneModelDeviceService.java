package com.sydh.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sydh.iot.domain.SceneModelDevice;
import com.sydh.iot.model.vo.SceneModelDeviceVO;

/**
 * 场景管理关联设备Service接口
 *
 * @author kerwincui
 * @date 2024-05-21
 */
public interface ISceneModelDeviceService extends IService<SceneModelDevice>
{
    /**
     * 查询场景管理关联设备
     *
     * @param id 场景管理关联设备主键
     * @return 场景管理关联设备
     */
    public SceneModelDevice selectSceneModelDeviceById(Long id);

    /**
     * 查询场景管理关联设备列表
     *
     * @param sceneModelDevice 场景管理关联设备
     * @return 场景管理关联设备分页集合
     */
    Page<SceneModelDeviceVO> pageSceneModelDeviceVO(SceneModelDevice sceneModelDevice);

    /**
     * 查询场景管理关联设备列表
     *
     * @param sceneModelDevice 场景管理关联设备
     * @return 场景管理关联设备集合
     */
    List<SceneModelDeviceVO> listSceneModelDeviceVO(SceneModelDevice sceneModelDevice);

    /**
     * 新增场景管理关联设备
     *
     * @param sceneModelDevice 场景管理关联设备
     * @return 结果
     */
    public int insertSceneModelDevice(SceneModelDevice sceneModelDevice);

    /**
     * 修改场景管理关联设备
     *
     * @param sceneModelDevice 场景管理关联设备
     * @return 结果
     */
    public int updateSceneModelDevice(SceneModelDevice sceneModelDevice);

    /**
     * 批量删除场景管理关联设备
     *
     * @param ids 需要删除的场景管理关联设备主键集合
     * @return 结果
     */
    public int deleteSceneModelDeviceByIds(Long[] ids);

    /**
     * 删除场景管理关联设备信息
     *
     * @param id 场景管理关联设备主键
     * @return 结果
     */
    public int deleteSceneModelDeviceById(Long id);

    /**
     * 更新启用状态
     * @param sceneModelDevice 场景关联设备
     * @return int
     */
    int editEnable(SceneModelDevice sceneModelDevice);

    /**
     * 查询录入、运算型
     * @param sceneModelId
     * @param: variableType
     * @return com.sydh.iot.domain.SceneModelDevice
     */
    SceneModelDevice selectOneBySceneModelIdAndVariableType(Long sceneModelId, Integer variableType);

    /**
     * 根据场景id删除关联设备
     * @param sceneModelIdList
     */
    void deleteBySceneModelIds(List<Long> sceneModelIdList);
}
