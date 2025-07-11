package com.sydh.sip.server;

import com.sydh.sip.domain.SipDevice;
import com.sydh.sip.model.InviteInfo;
import com.sydh.sip.model.VideoSessionInfo;

public interface ISipCmd {
    VideoSessionInfo playStreamCmd(SipDevice device, String channelId, boolean record);
    VideoSessionInfo playScreenshotCmd(SipDevice device, String channelId, String fileName);
    VideoSessionInfo playbackStreamCmd(SipDevice device, String channelId, String startTime, String endTime);
    VideoSessionInfo downloadStreamCmd(SipDevice device, String channelId, String startTime, String endTime, int downloadSpeed);
    void streamByeCmd(SipDevice device, String channelId, String stream, String ssrc);
    void streamByeCmd(String deviceId, String channelId, String stream, String ssrc);
    void streamByeCmdByInvite(SipDevice device, String channelId, InviteInfo invite);
    void audioBroadcastCmd(SipDevice device, String channelId);
}
