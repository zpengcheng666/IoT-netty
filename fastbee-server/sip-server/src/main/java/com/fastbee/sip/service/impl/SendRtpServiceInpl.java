package com.fastbee.sip.service.impl;

import com.fastbee.common.constant.FastBeeConstant;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.sip.model.SendRtpItem;
import com.fastbee.sip.service.ISendRtpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author zhuangpengli
 */
@Slf4j
@Service
public class SendRtpServiceInpl implements ISendRtpService {
    @Autowired
    private RedisCache redisCache;


    @Override
    public void update(SendRtpItem sendRtpItem) {
        redisCache.setCacheMapValue(FastBeeConstant.REDIS.SENDRTP_STREAM_KEY + sendRtpItem.getStreamId(), sendRtpItem.getDeviceId(), sendRtpItem);
        redisCache.setCacheMapValue(FastBeeConstant.REDIS.SENDRTP_CHANNEL_KEY + sendRtpItem.getChannelId(), sendRtpItem.getDeviceId(), sendRtpItem);
    }

    @Override
    public SendRtpItem queryByChannelId(String channelId, String targetId) {
        String key = FastBeeConstant.REDIS.SENDRTP_CHANNEL_KEY + channelId;
        return redisCache.getCacheMapValue( key, targetId);
    }

    @Override
    public SendRtpItem queryByStream(String stream, String targetId) {
        String key = FastBeeConstant.REDIS.SENDRTP_STREAM_KEY + stream;
        return redisCache.getCacheMapValue( key, targetId);
    }

    @Override
    public void delete(SendRtpItem sendRtpInfo) {
        if (sendRtpInfo == null) {
            return;
        }
        if (redisCache.getCacheMapSize(FastBeeConstant.REDIS.SENDRTP_STREAM_KEY + sendRtpInfo.getStreamId()) == 0) {
            redisCache.deleteStrObject(FastBeeConstant.REDIS.SENDRTP_STREAM_KEY + sendRtpInfo.getStreamId());
        }else {
            redisCache.deleteCacheMapValue(FastBeeConstant.REDIS.SENDRTP_STREAM_KEY  + sendRtpInfo.getStreamId(), sendRtpInfo.getDeviceId());
        }
        if (redisCache.getCacheMapSize(FastBeeConstant.REDIS.SENDRTP_CHANNEL_KEY + sendRtpInfo.getChannelId()) == 0) {
            redisCache.deleteStrObject(FastBeeConstant.REDIS.SENDRTP_CHANNEL_KEY + sendRtpInfo.getChannelId());
        }else {
            redisCache.deleteCacheMapValue(FastBeeConstant.REDIS.SENDRTP_CHANNEL_KEY  + sendRtpInfo.getChannelId(), sendRtpInfo.getDeviceId());
        }
    }


    @Override
    public void deleteByStream(String stream, String targetId) {
        SendRtpItem sendRtpInfo = queryByStream(stream, targetId);
        if (sendRtpInfo == null) {
            return;
        }
        delete(sendRtpInfo);
    }

    @Override
    public void deleteByChannel(String channelId, String targetId) {
        SendRtpItem sendRtpInfo = queryByChannelId(channelId, targetId);
        if (sendRtpInfo == null) {
            return;
        }
        delete(sendRtpInfo);
    }

    @Override
    public List<SendRtpItem> queryAll() {
        return Collections.emptyList();
    }

    @Override
    public boolean isChannelSendingRTP(Integer channelId) {
        return false;
    }
}
