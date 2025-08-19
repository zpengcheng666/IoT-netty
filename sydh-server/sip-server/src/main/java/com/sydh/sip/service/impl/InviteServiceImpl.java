package com.sydh.sip.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.sydh.common.core.redis.RedisCache;
import com.sydh.common.core.redis.RedisKeyBuilder;
import com.sydh.sip.enums.SessionType;
import com.sydh.sip.model.InviteInfo;
import com.sydh.sip.model.VideoSessionInfo;
import com.sydh.sip.service.IInviteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class InviteServiceImpl implements IInviteService {
    @Autowired
    private RedisCache redisCache;

    @Override
    public void updateInviteInfo(VideoSessionInfo sinfo, InviteInfo inviteInfo) {
        InviteInfo invite = getInviteInfo(sinfo.getType(), sinfo.getDeviceId(),
                sinfo.getChannelId(), sinfo.getStream());
        if (invite == null) {
            log.info("[更新Invite信息]，未从缓存中读取到Invite信息： deviceId: {}, channel: {}, stream: {}",
                    sinfo.getDeviceId(), sinfo.getChannelId(), sinfo.getStream());
            invite = inviteInfo;
        }
        if (inviteInfo.getSsrc() != null) {
            invite.setSsrc(inviteInfo.getSsrc());
        }
        if (inviteInfo.getCallId() != null) {
            invite.setCallId(inviteInfo.getCallId());
        }
        if (inviteInfo.getPort() != 0) {
            invite.setPort(inviteInfo.getPort());
        }
        String key = RedisKeyBuilder.buildInviteCacheKey(
                sinfo.getType() != null ? sinfo.getType().name() : "*",
                sinfo.getDeviceId() != null ? sinfo.getDeviceId() : "*",
                sinfo.getChannelId() != null ? sinfo.getChannelId() : "*",
                sinfo.getStream() != null ? sinfo.getStream() : "*",
                inviteInfo.getSsrc() != null ? inviteInfo.getSsrc() : "*");
        redisCache.setCacheObject(key, invite);
        log.debug("[更新Invite信息] 缓存键: {}, 数据: {}", key, invite);
    }

    @Override
    public InviteInfo getInviteInfo(SessionType type, String deviceId, String channelId, String stream) {
        String key = RedisKeyBuilder.buildInviteCacheKey(
                type != null ? type.name() : "*",
                deviceId != null ? deviceId : "*",
                channelId != null ? channelId : "*",
                stream != null ? stream : "*",
                "*");
        
        log.debug("[获取InviteInfo] 查找键: {}, type: {}, deviceId: {}, channelId: {}, stream: {}", 
                key, type, deviceId, channelId, stream);
        
        List<Object> scanResult = redisCache.scan(key);
        log.debug("[获取InviteInfo] 扫描结果数量: {}", scanResult.size());
        
        if (scanResult.isEmpty()) {
            // 尝试更宽松的匹配
            String fallbackKey = RedisKeyBuilder.buildInviteCacheKey(
                    type != null ? type.name() : "*",
                    deviceId != null ? deviceId : "*",
                    channelId != null ? channelId : "*",
                    "*",
                    "*");
            log.debug("[获取InviteInfo] 尝试备用查找键: {}", fallbackKey);
            scanResult = redisCache.scan(fallbackKey);
            log.debug("[获取InviteInfo] 备用扫描结果数量: {}", scanResult.size());
        }
        
        if (scanResult.isEmpty()) {
            log.debug("[获取InviteInfo] 未找到匹配的缓存数据");
            return null;
        }
        
        if (scanResult.size() != 1) {
            log.warn("[获取InviteInfo] 发现 key: {}存在多条记录: {}", key, scanResult.size());
        }

        String foundKey = (String) scanResult.get(0);
        log.debug("[获取InviteInfo] 找到缓存键: {}", foundKey);
        
        return safeGetInviteInfo(foundKey);
    }

    @Override
    public List<InviteInfo> getInviteInfoAll(SessionType type, String deviceId, String channelId, String stream) {
        String key = RedisKeyBuilder.buildInviteCacheKey(
                type != null ? type.name() : "*",
                deviceId != null ? deviceId : "*",
                channelId != null ? channelId : "*",
                stream != null ? stream : "*",
                "*");
        List<Object> scanResult = redisCache.scan(key);
        if (scanResult.size() != 1) {
            log.warn("[获取InviteInfo] 发现 key: {}存在多条", key);
        }
        if (!scanResult.isEmpty()) {
            List<InviteInfo> list = new ArrayList<>();
            for (Object keyObj : scanResult) {
                InviteInfo inviteInfo = safeGetInviteInfo((String) keyObj);
                if (inviteInfo != null) {
                    list.add(inviteInfo);
                }
            }
            return list;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public InviteInfo getInviteInfoBySSRC(String ssrc) {
        String key = RedisKeyBuilder.buildInviteCacheKey("*",
                "*",
                "*",
                "*",
                ssrc);
        List<Object> scanResult = redisCache.scan(key);
        if (scanResult.size() != 1) {
            return null;
        }
        return safeGetInviteInfo((String) scanResult.get(0));
    }

    @Override
    public void removeInviteInfo(SessionType type, String deviceId, String channelId, String stream) {
        String scanKey = RedisKeyBuilder.buildInviteCacheKey(
                type != null ? type.name() : "*",
                deviceId != null ? deviceId : "*",
                channelId != null ? channelId : "*",
                stream != null ? stream : "*",
                "*");
        List<Object> scanResult = redisCache.scan(scanKey);
        if (!scanResult.isEmpty()) {
            for (Object keyObj : scanResult) {
                String key = (String) keyObj;
                InviteInfo inviteInfo = safeGetInviteInfo(key);
                if (inviteInfo == null) {
                    continue;
                }
                redisCache.deleteObject(key);
            }
        }
    }

    /**
     * 安全获取InviteInfo，处理类型转换异常
     */
    private InviteInfo safeGetInviteInfo(String key) {
        try {
            Object cachedObject = redisCache.getCacheObject(key);
            if (cachedObject instanceof InviteInfo) {
                log.debug("[InviteInfo类型转换] 直接返回InviteInfo对象，key: {}", key);
                return (InviteInfo) cachedObject;
            } else if (cachedObject instanceof JSONObject) {
                // 尝试从JSONObject转换为InviteInfo
                JSONObject jsonObject = (JSONObject) cachedObject;
                InviteInfo inviteInfo = InviteInfo.builder()
                        .ssrc(jsonObject.getString("ssrc"))
                        .callId(jsonObject.getString("callId"))
                        .fromTag(jsonObject.getString("fromTag"))
                        .viaTag(jsonObject.getString("viaTag"))
                        .toTag(jsonObject.getString("toTag"))
                        .port(jsonObject.getIntValue("port"))
                        .build();
                log.warn("[InviteInfo类型转换] 从JSONObject成功转换为InviteInfo，key: {}", key);
                return inviteInfo;
            } else {
                log.error("[InviteInfo类型转换] 未知的缓存对象类型: {}, key: {}", 
                    cachedObject != null ? cachedObject.getClass().getName() : "null", key);
                // 删除无效的缓存数据
                redisCache.deleteObject(key);
                return null;
            }
        } catch (ClassCastException e) {
            log.error("[InviteInfo类型转换] 类型转换失败，key: {}, error: {}", key, e.getMessage());
            // 删除无效的缓存数据
            redisCache.deleteObject(key);
            return null;
        } catch (Exception e) {
            log.error("[InviteInfo类型转换] 获取缓存对象失败，key: {}, error: {}", key, e.getMessage());
            return null;
        }
    }
}
