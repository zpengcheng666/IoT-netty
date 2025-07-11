package com.sydh.sip.service.impl;

import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.core.redis.RedisCache;
import com.sydh.common.core.redis.RedisKeyBuilder;
import com.sydh.sip.model.RecordList;
import com.sydh.sip.model.ZlmMediaServer;
import com.sydh.sip.service.ISipCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SipCacheServiceImpl implements ISipCacheService {

    @Autowired
    private RedisCache redisCache;

    @Override
    public Long getCSEQ(String serverSipId) {
        String key = RedisKeyBuilder.buildSipCSEQCacheKey(serverSipId);
        long result = redisCache.incr(key, 1L);
        if (result > Integer.MAX_VALUE) {
            redisCache.setCacheObject(key, 1);
            result = 1;
        }
        return result;
    }

    @Override
    public void updateMediaInfo(ZlmMediaServer mediaServerConfig) {
        redisCache.setCacheObject(SYDHConstant.REDIS.DEFAULT_MEDIA_CONFIG, mediaServerConfig);
    }

    @Override
    public void setRecordList(String key, RecordList recordList) {
        String catchkey = RedisKeyBuilder.buildSipRecordinfoCacheKey(key);
        redisCache.setCacheObject(catchkey, recordList);
    }
}
