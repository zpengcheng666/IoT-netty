package com.sydh.sip.service;

import com.sydh.sip.model.SendRtpItem;

import java.util.List;

/**
 * @author zhuangpengli
 */
public interface ISendRtpService {

    void update(SendRtpItem sendRtpItem);

    SendRtpItem queryByChannelId(String channelId, String targetId);


    SendRtpItem queryByStream(String stream, String targetId);

    void delete(SendRtpItem sendRtpInfo);

    void deleteByStream(String stream, String targetId);

    void deleteByChannel(String channelId, String targetId);

    List<SendRtpItem> queryAll();

    boolean isChannelSendingRTP(Integer channelId);
}
