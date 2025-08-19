package com.sydh.sip.server;

import cn.hutool.json.JSONUtil;
import com.sydh.common.core.redis.RedisCache;
import com.sydh.common.core.redis.RedisKeyBuilder;
import com.sydh.sip.enums.SessionType;
import com.sydh.sip.model.VideoSessionInfo;
import com.sydh.sip.util.SipUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;

@Component
@Slf4j
public class VideoSessionManager {
    @Autowired
    private RedisCache redisCache;

    public String createPlaySsrc(String domain) {
        return SipUtil.getPlaySsrc(domain);
    }

    public String createPlayBackSsrc(String domain) {
        return SipUtil.getPlayBackSsrc(domain);
    }

    public void put(VideoSessionInfo info) {
        String ssrc = info.getSsrc();
        if (info.getType() == SessionType.play || info.getType() == SessionType.playrecord) {
            ssrc = info.getType().name();
        }
        String key = RedisKeyBuilder.buildStreamCacheKey(info.getDeviceId(), info.getChannelId(), info.getStream(), ssrc);
        redisCache.setCacheObject(key, info);
    }

    public VideoSessionInfo getSessionInfo(String deviceId, String channelId, String stream, String callId) {
        if (ObjectUtils.isEmpty(deviceId)) {
            deviceId = "*";
        }
        if (ObjectUtils.isEmpty(channelId)) {
            channelId = "*";
        }
        if (ObjectUtils.isEmpty(stream)) {
            stream = "*";
        }
        if (ObjectUtils.isEmpty(callId)) {
            callId = "*";
        }
        String key = RedisKeyBuilder.buildStreamCacheKey(deviceId, channelId, stream, callId);
        log.info("[stream cache key]key:{}", key);
        List<Object> scanResult = redisCache.scan(key);
        log.info("[scan result]result:{}", JSONUtil.toJsonStr(scanResult));
        if (scanResult.size() == 0) {
            return null;
        }
        
        try {
            return (VideoSessionInfo) redisCache.getCacheObject((String) scanResult.get(0));
        } catch (ClassCastException e) {
            // 如果类型转换失败，尝试从JSONObject转换
            Object cachedObject = redisCache.getCacheObject((String) scanResult.get(0));
            if (cachedObject instanceof com.alibaba.fastjson2.JSONObject) {
                try {
                    com.alibaba.fastjson2.JSONObject jsonObject = (com.alibaba.fastjson2.JSONObject) cachedObject;
                    return jsonObject.toJavaObject(VideoSessionInfo.class);
                } catch (Exception ex) {
                    // 转换失败，删除这个无效的缓存
                    redisCache.deleteObject((String) scanResult.get(0));
                    return null;
                }
            }
            // 其他类型转换失败，删除这个无效的缓存
            redisCache.deleteObject((String) scanResult.get(0));
            return null;
        }
    }

    public VideoSessionInfo getSessionInfoByCallId(String callId) {
        if (ObjectUtils.isEmpty(callId)) {
            return null;
        }
        String key = RedisKeyBuilder.buildStreamCacheKey("*", "*", "*", callId);
        List<Object> scanResult = redisCache.scan(key);
        if (!scanResult.isEmpty()) {
            try {
                return (VideoSessionInfo) redisCache.getCacheObject((String) scanResult.get(0));
            } catch (ClassCastException e) {
                // 如果类型转换失败，尝试从JSONObject转换
                Object cachedObject = redisCache.getCacheObject((String) scanResult.get(0));
                if (cachedObject instanceof com.alibaba.fastjson2.JSONObject) {
                    try {
                        com.alibaba.fastjson2.JSONObject jsonObject = (com.alibaba.fastjson2.JSONObject) cachedObject;
                        return jsonObject.toJavaObject(VideoSessionInfo.class);
                    } catch (Exception ex) {
                        // 转换失败，删除这个无效的缓存
                        redisCache.deleteObject((String) scanResult.get(0));
                        return null;
                    }
                }
                // 其他类型转换失败，删除这个无效的缓存
                redisCache.deleteObject((String) scanResult.get(0));
                return null;
            }
        } else {
            return null;
        }
    }

    public VideoSessionInfo getSessionInfoBySSRC(String SSRC) {
        if (ObjectUtils.isEmpty(SSRC)) {
            return null;
        }
        String key = RedisKeyBuilder.buildStreamCacheKey("*", "*", SSRC, "*");
        List<Object> scanResult = redisCache.scan(key);
        if (!scanResult.isEmpty()) {
            try {
                return (VideoSessionInfo) redisCache.getCacheObject((String) scanResult.get(0));
            } catch (ClassCastException e) {
                // 如果类型转换失败，尝试从JSONObject转换
                Object cachedObject = redisCache.getCacheObject((String) scanResult.get(0));
                if (cachedObject instanceof com.alibaba.fastjson2.JSONObject) {
                    try {
                        com.alibaba.fastjson2.JSONObject jsonObject = (com.alibaba.fastjson2.JSONObject) cachedObject;
                        return jsonObject.toJavaObject(VideoSessionInfo.class);
                    } catch (Exception ex) {
                        // 转换失败，删除这个无效的缓存
                        redisCache.deleteObject((String) scanResult.get(0));
                        return null;
                    }
                }
                // 其他类型转换失败，删除这个无效的缓存
                redisCache.deleteObject((String) scanResult.get(0));
                return null;
            }
        } else {
            return null;
        }

    }

    public List<VideoSessionInfo> getSessionInfoForAll(String deviceId, String channelId, String stream, String callId) {
        if (ObjectUtils.isEmpty(deviceId)) {
            deviceId = "*";
        }
        if (ObjectUtils.isEmpty(channelId)) {
            channelId = "*";
        }
        if (ObjectUtils.isEmpty(stream)) {
            stream = "*";
        }
        if (ObjectUtils.isEmpty(callId)) {
            callId = "*";
        }
        String key = RedisKeyBuilder.buildStreamCacheKey(deviceId, channelId, stream, callId);
        List<Object> scanResult = redisCache.scan(key);
        if (scanResult.size() == 0) {
            return emptyList();
        }
        List<VideoSessionInfo> result = new ArrayList<>();
        for (Object keyObj : scanResult) {
            try {
                VideoSessionInfo sessionInfo = (VideoSessionInfo) redisCache.getCacheObject((String) keyObj);
                if (sessionInfo != null) {
                    result.add(sessionInfo);
                }
            } catch (ClassCastException e) {
                // 如果类型转换失败，尝试从JSONObject转换
                Object cachedObject = redisCache.getCacheObject((String) keyObj);
                if (cachedObject instanceof com.alibaba.fastjson2.JSONObject) {
                    try {
                        com.alibaba.fastjson2.JSONObject jsonObject = (com.alibaba.fastjson2.JSONObject) cachedObject;
                        VideoSessionInfo sessionInfo = jsonObject.toJavaObject(VideoSessionInfo.class);
                        if (sessionInfo != null) {
                            result.add(sessionInfo);
                        }
                    } catch (Exception ex) {
                        // 转换失败，删除这个无效的缓存
                        redisCache.deleteObject((String) keyObj);
                    }
                } else {
                    // 其他类型转换失败，删除这个无效的缓存
                    redisCache.deleteObject((String) keyObj);
                }
            }
        }
        return result;
    }

    public String getMediaServerId(String deviceId, String channelId, String stream) {
        VideoSessionInfo ssrcTransaction = getSessionInfo(deviceId, channelId, null, stream);
        if (ssrcTransaction == null) {
            return null;
        }
        return ssrcTransaction.getMediaServerId();
    }

    public String getSSRC(String deviceId, String channelId, String stream) {
        VideoSessionInfo ssrcTransaction = getSessionInfo(deviceId, channelId, null, stream);
        if (ssrcTransaction == null) {
            return null;
        }
        return ssrcTransaction.getSsrc();
    }

    public void remove(String deviceId, String channelId, String stream, String callId) {
        String key = RedisKeyBuilder.buildStreamCacheKey(deviceId, channelId, stream, callId);
        if (!Objects.equals(callId, "play")) {
            redisCache.deleteObject(key);
        }
    }

    public void remove(String deviceId, String channelId, String stream) {
        List<VideoSessionInfo> sinfoList = getSessionInfoForAll(deviceId, channelId, stream, null);
        if (sinfoList == null || sinfoList.isEmpty()) {
            return;
        }
        for (VideoSessionInfo sinfo : sinfoList) {
            String key = RedisKeyBuilder.buildStreamCacheKey(deviceId, channelId, stream, sinfo.getSsrc());
            if (sinfo.getType() != SessionType.play) {
                redisCache.deleteObject(key);
            }
        }
    }

    public void removeByCallId(String deviceId, String channelId, String callId) {
        VideoSessionInfo sinfo = getSessionInfo(deviceId, channelId, null, callId);
        if (sinfo == null) {
            return;
        }
        String key = RedisKeyBuilder.buildStreamCacheKey(deviceId, channelId, sinfo.getStream(), sinfo.getSsrc());
        if (sinfo.getType() != SessionType.play) {
            redisCache.deleteObject(key);
        }
    }

    public List<VideoSessionInfo> getAllSsrc() {
        String allkey = RedisKeyBuilder.buildStreamCacheKey("*", "*", "*", "*");
        List<Object> scanResult = redisCache.scan(allkey);
        if (scanResult.size() == 0) {
            return null;
        }
        List<VideoSessionInfo> result = new ArrayList<>();
        for (Object ssrcTransactionKey : scanResult) {
            String key = (String) ssrcTransactionKey;
            try {
                VideoSessionInfo sessionInfo = (VideoSessionInfo) redisCache.getCacheObject((String) key);
                if (sessionInfo != null) {
                    result.add(sessionInfo);
                }
            } catch (ClassCastException e) {
                // 如果类型转换失败，尝试从JSONObject转换
                Object cachedObject = redisCache.getCacheObject((String) key);
                if (cachedObject instanceof com.alibaba.fastjson2.JSONObject) {
                    try {
                        com.alibaba.fastjson2.JSONObject jsonObject = (com.alibaba.fastjson2.JSONObject) cachedObject;
                        VideoSessionInfo sessionInfo = jsonObject.toJavaObject(VideoSessionInfo.class);
                        if (sessionInfo != null) {
                            result.add(sessionInfo);
                        }
                    } catch (Exception ex) {
                        // 转换失败，删除这个无效的缓存
                        redisCache.deleteObject((String) key);
                    }
                } else {
                    // 其他类型转换失败，删除这个无效的缓存
                    redisCache.deleteObject((String) key);
                }
            }
        }
        return result;
    }
}
