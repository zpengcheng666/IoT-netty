package com.sydh.iot.cache.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sydh.common.core.redis.RedisCache;
import com.sydh.common.core.redis.RedisKeyBuilder;
import com.sydh.iot.cache.SceneModelTagCache;
import com.sydh.iot.model.scenemodel.SceneModelTagCacheVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 物模型缓存和物模型值缓存
 * @author gsb
 * @date 2024/3/22 16:37
 */
@Service
public class SceneModelTagCacheImpl implements SceneModelTagCache {

    @Resource
    private RedisCache redisCache;


    @Override
    public void addSceneModelTagValue(Long sceneModelId, SceneModelTagCacheVO sceneModelTagCacheVO) {
        String key = RedisKeyBuilder.buildSceneModelTagCacheKey(sceneModelId);
        redisCache.setHashValue(key, sceneModelTagCacheVO.getId(), JSON.toJSONString(sceneModelTagCacheVO));
    }

    @Override
    public SceneModelTagCacheVO getSceneModelTagValue(Long sceneModelId, Long sceneModelTagId) {
        String key = RedisKeyBuilder.buildSceneModelTagCacheKey(sceneModelId);
        return JSONObject.parseObject(redisCache.getCacheMapValue(key, sceneModelTagId.toString()), SceneModelTagCacheVO.class);
    }
}
