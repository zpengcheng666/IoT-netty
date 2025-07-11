package com.sydh.iot.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.SceneModel;
import com.sydh.iot.model.vo.SceneModelVO;
import org.apache.ibatis.annotations.Param;

/**
 * 场景管理Mapper接口
 *
 * @author kerwincui
 * @date 2024-05-20
 */
public interface SceneModelMapper extends BaseMapperX<SceneModel>
{
    /**
     * 查询场景管理
     *
     * @param sceneModelId 场景管理主键
     * @return 场景管理
     */
    public SceneModelVO selectSceneModelBySceneModelId(Long sceneModelId);

    /**
     * 查询场景管理列表
     *
     * @param sceneModelVO 场景管理
     * @return 场景管理集合
     */
    public Page<SceneModelVO> selectSceneModelVoPage(Page<SceneModelVO> page, @Param("sceneModelVO") SceneModelVO sceneModelVO);

    /**
     * 查询组态信息
     * @param guidList 组态id集合
     * @return java.util.List<com.sydh.iot.domain.Product>
     */
    List<SceneModelVO> selectListScadaIdByGuidS(@Param("guidList") List<String> guidList);

}
