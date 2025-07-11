package com.sydh.sip.server.impl;

import com.sydh.sip.domain.SipConfig;
import com.sydh.sip.domain.SipDevice;
import com.sydh.sip.server.IRtspCmd;
import com.sydh.sip.server.ReqMsgHeaderBuilder;
import com.sydh.sip.server.SIPSender;
import com.sydh.sip.server.SipLayer;
import com.sydh.sip.service.ISipConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.message.Request;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhuangpengli
 */
@Slf4j
@Component
public class RtspCmdImpl implements IRtspCmd {
    public static Map<String, Long> CSEQCACHE = new ConcurrentHashMap<>();

    @Autowired
    private ReqMsgHeaderBuilder headerBuilder;

    @Autowired
    private ISipConfigService sipConfigService;

    @Autowired
    private SIPSender sipSender;

    @Autowired
    private SipLayer sipLayer;
    @Override
    public void playPause(SipDevice device, String channelId, String streamId) {
        try {
            SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
            if (sipConfig == null) {
                log.error("[playPause] sipConfig is null");
                return ;
            }
            String content = "PAUSE RTSP/1.0\r\n" +
                    "CSeq: " + getInfoCseq() + "\r\n" +
                    "PauseTime: now\r\n";
            Request request = headerBuilder.createRtspRequest(device, sipConfig, channelId, streamId, content);
            //发送消息
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()),request);

        } catch (SipException | ParseException | InvalidArgumentException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void playReplay(SipDevice device, String channelId, String streamId) {
        try {
            SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
            if (sipConfig == null) {
                log.error("[playReplay] sipConfig is null");
                return ;
            }
            String content = "PLAY RTSP/1.0\r\n" +
                    "CSeq: " + getInfoCseq() + "\r\n" +
                    "Range: npt=now-\r\n";
            Request request = headerBuilder.createRtspRequest(device, sipConfig, channelId, streamId, content);
            //发送消息
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()),request);
        } catch (SipException | ParseException | InvalidArgumentException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void playBackSeek(SipDevice device, String channelId, String streamId, long seektime) {
        try {
            SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
            if (sipConfig == null) {
                log.error("[playBackSeek] sipConfig is null");
                return ;
            }
            String content = "PLAY RTSP/1.0\r\n" +
                    "CSeq: " + getInfoCseq() + "\r\n" +
                    "Range: npt=" + Math.abs(seektime) + "-\r\n";
            Request request = headerBuilder.createRtspRequest(device, sipConfig, channelId, streamId, content);
            //发送消息
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()),request);
        } catch (SipException | ParseException | InvalidArgumentException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void playBackSpeed(SipDevice device, String channelId, String streamId, Integer speed) {
        try {
            SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
            if (sipConfig == null) {
                log.error("[playBackSpeed] sipConfig is null");
                return ;
            }
            String content = "PLAY RTSP/1.0\r\n" +
                    "CSeq: " + getInfoCseq() + "\r\n" +
                    "Scale: " + speed + ".000000\r\n";
            Request request = headerBuilder.createRtspRequest(device, sipConfig, channelId, streamId, content);
            //发送消息
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()),request);
        } catch (SipException | ParseException | InvalidArgumentException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void setCseq(String streamId) {
        if (CSEQCACHE.containsKey(streamId)) {
            CSEQCACHE.put(streamId, CSEQCACHE.get(streamId) + 1);
        } else {
            CSEQCACHE.put(streamId, 2L);
        }
    }

    private int getInfoCseq() {
        return (int) ((Math.random() * 9 + 1) * Math.pow(10, 8));
    }
}
