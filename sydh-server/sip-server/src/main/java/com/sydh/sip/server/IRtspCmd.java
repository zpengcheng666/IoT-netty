package com.sydh.sip.server;

import com.sydh.sip.domain.SipDevice;

/**
 * @author zhuangpengli
 */
public interface IRtspCmd {
    void playPause(SipDevice device, String channelId, String streamId);
    void playReplay(SipDevice device, String channelId, String streamId);
    void playBackSeek(SipDevice device, String channelId, String streamId, long seektime);
    void playBackSpeed(SipDevice device, String channelId, String streamId, Integer speed);
    void setCseq(String streamId);
}
