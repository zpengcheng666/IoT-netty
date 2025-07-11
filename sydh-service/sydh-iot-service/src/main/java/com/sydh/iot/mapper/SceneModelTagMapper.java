package com.sydh.iot.mapper;

import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.SceneModelTag;
import com.sydh.iot.model.vo.SceneModelTagVO;

/**
 * 场景录入型变量Mapper接口
 *
 * @author kerwincui
 * @date 2024-05-21
 */
public interface SceneModelTagMapper extends BaseMapperX<SceneModelTag>
{
    /**
     * 查询场景录入型变量
     *
     * @param id 场景录入型变量主键
     * @return 场景录入型变量
     */
    public SceneModelTagVO selectSceneModelTagById(Long id);

    /**
     * 校验变量名称唯一
     * @param sceneModelTagVO 场景变量
     * @return com.sydh.iot.domain.SceneModelTag
     */
    SceneModelTag checkName(SceneModelTagVO sceneModelTagVO);

    /**
     * 查询场景录入型变量
     *
     * @param id 场景录入型变量主键
     * @return 场景录入型变量
     */
    SceneModelTagVO selectSceneModelTagAndTenantById(Long id);
}
