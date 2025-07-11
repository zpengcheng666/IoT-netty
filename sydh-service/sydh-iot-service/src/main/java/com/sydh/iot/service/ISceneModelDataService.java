package com.sydh.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sydh.common.core.page.TableDataExtendInfo;
import com.sydh.iot.domain.SceneModelData;
import com.sydh.iot.model.scenemodel.SceneModelDataDTO;
import com.sydh.iot.model.vo.SceneModelDataVO;

/**
 * 【请填写功能名称】Service接口
 *
 * @author kerwincui
 * @date 2024-05-21
 */
public interface ISceneModelDataService extends IService<SceneModelData>
{
    /**
     * 查询变量详情
     *
     * @param id 主键
     * @return SceneModelData
     */
    public SceneModelData selectSceneModelDataById(Long id);

    /**
     * 查询sceneModelData列表
     *
     * @param sceneModelData sceneModelData
     * @return sceneModelData分页集合
     */
    Page<SceneModelDataVO> pageSceneModelDataVO(SceneModelData sceneModelData);

    /**
     * 查询sceneModelData列表
     *
     * @param sceneModelData sceneModelData
     * @return sceneModelData集合
     */
    List<SceneModelDataVO> listSceneModelDataVO(SceneModelData sceneModelData);

    /**
     * 查询所有变量列表
     *
     * @param sceneModelData 查询参数
     * @return 集合
     */
    public Page<SceneModelDataDTO> selectSceneModelDataDTOList(SceneModelData sceneModelData);

    /**
     * 查询关联设备变量的列表
     * @param sceneModelData 查询参数
     * @return com.sydh.common.core.page.TableDataExtendInfo
     */
    TableDataExtendInfo listByType(SceneModelData sceneModelData);

    /**
     * 新增【请填写功能名称】
     *
     * @param sceneModelData 【请填写功能名称】
     * @return 结果
     */
    public int insertSceneModelData(SceneModelData sceneModelData);

    /**
     * 修改【请填写功能名称】
     *
     * @param sceneModelData 【请填写功能名称】
     * @return 结果
     */
    public int updateSceneModelData(SceneModelData sceneModelData);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】主键集合
     * @return 结果
     */
    public int deleteSceneModelDataByIds(Long[] ids);

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteSceneModelDataById(Long id);

    /**
     * 更新场景变量启用
     * @param sceneModelData
     * @return int
     */
    int editEnable(SceneModelData sceneModelData);

    /**
     * 批量查询
     * @param ids id集合
     * @return java.util.List<com.sydh.iot.domain.SceneModelData>
     */
    List<SceneModelData> selectSceneModelDataListByIds(List<Long> ids);
}
