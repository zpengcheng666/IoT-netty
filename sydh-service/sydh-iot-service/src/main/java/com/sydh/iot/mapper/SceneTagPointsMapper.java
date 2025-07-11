package com.sydh.iot.mapper;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.SceneTagPoints;

/**
 * 运算型变量点Mapper接口
 *
 * @author kerwincui
 * @date 2024-05-21
 */
public interface SceneTagPointsMapper extends BaseMapperX<SceneTagPoints>
{

    /**
     * 批量删除场景变量运算
     * @param sceneModelIds
     * @return void
     */
    void deleteBySceneModelIds(Long[] sceneModelIds);
}
