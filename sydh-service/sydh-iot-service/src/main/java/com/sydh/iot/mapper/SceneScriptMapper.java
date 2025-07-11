package com.sydh.iot.mapper;

import java.util.List;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.SceneScript;
import org.springframework.stereotype.Repository;

/**
 * 场景脚本Mapper接口
 *
 * @author kerwincui
 * @date 2023-12-27
 */
@Repository
public interface SceneScriptMapper extends BaseMapperX<SceneScript>
{

    /**
     * 查询场景脚本列表
     *
     * @param sceneScript 场景脚本
     * @return 场景脚本集合
     */
    public List<SceneScript> selectSceneScriptList(SceneScript sceneScript);
}
