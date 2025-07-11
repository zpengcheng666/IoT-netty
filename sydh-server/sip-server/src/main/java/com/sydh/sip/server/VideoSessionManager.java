package com.sydh.sip.server;

import com.sydh.common.core.redis.RedisCache;
import com.sydh.common.core.redis.RedisKeyBuilder;
import com.sydh.sip.enums.SessionType;
import com.sydh.sip.model.VideoSessionInfo;
import com.sydh.sip.util.SipUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;

@Component
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
        List<Object> scanResult = redisCache.scan(key);
        if (scanResult.size() == 0) {
            return null;
        }
        return (VideoSessionInfo) redisCache.getCacheObject((String) scanResult.get(0));
    }

    public VideoSessionInfo getSessionInfoByCallId(String callId) {
        if (ObjectUtils.isEmpty(callId)) {
            return null;
        }
        String key = RedisKeyBuilder.buildStreamCacheKey("*", "*", "*", callId);
        List<Object> scanResult = redisCache.scan(key);
        if (!scanResult.isEmpty()) {
            return (VideoSessionInfo) redisCache.getCacheObject((String) scanResult.get(0));
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
            return (VideoSessionInfo) redisCache.getCacheObject((String) scanResult.get(0));
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
            result.add((VideoSessionInfo) redisCache.getCacheObject((String) keyObj));
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
            result.add((VideoSessionInfo) redisCache.getCacheObject((String) key));
        }
        return result;
    }
}
