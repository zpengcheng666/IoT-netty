package com.sydh.sip.service.impl;

import com.sydh.sip.domain.MediaServer;
import com.sydh.sip.domain.SipDevice;
import com.sydh.sip.model.InviteInfo;
import com.sydh.sip.model.SendRtpItem;
import com.sydh.sip.model.Stream;
import com.sydh.sip.server.ISipCmd;
import com.sydh.sip.service.*;
import com.sydh.sip.util.ZlmRtpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TalkServiceImpl implements ITalkService {
    @Autowired
    private ISipCmd sipCmd;

    @Autowired
    private ZlmRtpUtils zlmRtpUtils;

    @Autowired
    private ISipDeviceService sipDeviceService;

    @Autowired
    private IZmlHookService zmlHookService;

    @Autowired
    private ISendRtpService sendRtpService;

    @Autowired
    private IMediaServerService mediaServerService;

    @Override
    public Stream getBroadcastUrl(String deviceId, String channelId) {
        return zmlHookService.buildPushRtc("broadcast", deviceId, channelId);
    }

    @Override
    public String broadcast(String deviceId, String channelId) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        if (dev == null) {
            log.error("broadcast dev is null,deviceId:{},channelId:{}", deviceId, channelId);
            return null;
        }
        sipCmd.audioBroadcastCmd(dev, channelId);
        return "";
    }

    @Override
    public String broadcastStop(String deviceId, String channelId) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        if (dev == null) {
            log.error("[发送BYE] device is null");
            return "";
        }
        SendRtpItem sendRtpInfo = sendRtpService.queryByChannelId(channelId, deviceId);
        if (sendRtpInfo != null) {
            MediaServer mediaServer = mediaServerService.selectMediaServerById(sendRtpInfo.getServerId());
            zlmRtpUtils.stopSendRtpStream(mediaServer, sendRtpInfo.getApp(), sendRtpInfo.getStreamId(), sendRtpInfo.getSsrc());
            sendRtpService.delete(sendRtpInfo);
            InviteInfo info = InviteInfo.builder()
                    .callId(sendRtpInfo.getCallId())
                    .ssrc(sendRtpInfo.getSsrc())
                    .fromTag(sendRtpInfo.getFromTag())
                    .viaTag(sendRtpInfo.getViaTag())
                    .build();
            sipCmd.streamByeCmdByInvite(dev,sendRtpInfo.getChannelId(),info);
        }
        return "";
    }
}
