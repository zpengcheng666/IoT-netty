package com.sydh.sip.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.sydh.common.utils.DateUtils;
import com.sydh.sip.domain.MediaServer;
import com.sydh.sip.domain.SipDevice;
import com.sydh.sip.model.Stream;
import com.sydh.sip.model.VideoSessionInfo;
import com.sydh.sip.server.IRtspCmd;
import com.sydh.sip.server.ISipCmd;
import com.sydh.sip.server.VideoSessionManager;
import com.sydh.sip.service.IMediaServerService;
import com.sydh.sip.service.IPlayService;
import com.sydh.sip.service.ISipDeviceService;
import com.sydh.sip.service.IZmlHookService;
import com.sydh.sip.util.SipUtil;
import com.sydh.sip.util.ZlmApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PlayServiceImpl implements IPlayService {

    @Autowired
    private ISipCmd sipCmd;

    @Autowired
    private IRtspCmd rtspCmd;

    @Autowired
    private IZmlHookService zmlHookService;

    @Autowired
    private VideoSessionManager streamSession;

    @Autowired
    private ISipDeviceService sipDeviceService;

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private ZlmApiUtils zlmApiUtils;

    @Override
    public Stream play(String deviceId, String channelId, boolean record) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        if (dev == null) {
            log.error("play dev is null,deviceId:{},channelId:{}", deviceId, channelId);
            return null;
        }
        String streamid;
        if (record) {
            streamid = String.format("%s_%s_%s", SipUtil.PREFIX_PLAYRECORD,
                    deviceId,
                    channelId);
        } else {
            streamid = String.format("%s_%s_%s", SipUtil.PREFIX_PLAY, deviceId, channelId);
        }
        VideoSessionInfo sinfo = streamSession.getSessionInfo(deviceId, channelId, streamid, null);
        if (sinfo == null) {
            VideoSessionInfo info = sipCmd.playStreamCmd(dev, channelId, record);
            return zmlHookService.updateStream(info);
        } else {
            log.info("sinfo： {}", sinfo);
            MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(deviceId);
            if (mediaInfo == null) {
                log.error("mediaInfo is null,deviceId:{},channelId:{}", deviceId, channelId);
            }
            JSONObject rtpInfo = zlmApiUtils.getRtpInfo(mediaInfo, streamid);
            if (rtpInfo != null && rtpInfo.getInteger("code") == 0) {
                if (rtpInfo.getBoolean("exist")) {
                    //直播
                    if (sinfo.isPushing() && !record) {
                        return zmlHookService.updateStream(sinfo);
                    }
                    //直播录像
                    if (sinfo.isRecording() && record) {
                        return zmlHookService.updateStream(sinfo);
                    }
                } else {
                    //清理会话后 重新发起播放
                    sipCmd.streamByeCmd(dev, channelId, streamid, null);
                    VideoSessionInfo info = sipCmd.playStreamCmd(dev, channelId, record);
                    return zmlHookService.updateStream(info);
                }
            } else {
                log.error("获取流信息失败：{}", rtpInfo);
            }
            return null;
        }
    }

    @Override
    public String screenshot(String deviceId, String channelId) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        if (dev == null) {
            log.error("play dev is null,deviceId:{},channelId:{}", deviceId, channelId);
            return null;
        }
        String streamid = String.format("%s_%s_%s", SipUtil.PREFIX_PLAY, deviceId, channelId);
        VideoSessionInfo sinfo = streamSession.getSessionInfo(deviceId, channelId, streamid, null);
        if (sinfo == null) {
            // 重新发起推流后截图 可以自定义文件格式
            String fileName = sinfo.getDeviceId() + "_" + sinfo.getChannelId() + "_"+ DateUtils.getNowDate() + ".jpg";
            sipCmd.playScreenshotCmd(dev, channelId, fileName);
            // 返回截图文件名
            return fileName;
        } else {
            // 流存在，立刻截图
            log.info("sinfo： {}", sinfo);
            MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(deviceId);
            if (mediaInfo == null) {
                log.error("mediaInfo is null,deviceId:{},channelId:{}", deviceId, channelId);
            }
            JSONObject rtpInfo = zlmApiUtils.getRtpInfo(mediaInfo, streamid);
            if (rtpInfo != null && rtpInfo.getInteger("code") == 0) {
                if (sinfo.isPushing()) {
                    String streamUrl = String.format("rtsp://127.0.0.1:%s/%s/%s", mediaInfo.getPortRtsp(), "rtp", sinfo.getStream());
                    // 可以自定义文件格式
                    String fileName = sinfo.getDeviceId() + "_" + sinfo.getChannelId() + "_"+ DateUtils.getNowDate() + ".jpg";
                    // 请求截图
                    log.info("[请求截图]: {}:{}", streamUrl, fileName);
                    zlmApiUtils.getSnap(mediaInfo, streamUrl, 15, 1, fileName);
                    return fileName;
                }
            }
            return "";
        }
    }

    @Override
    public String closeStream(String deviceId, String channelId, String streamId) {
        MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(deviceId);
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        JSONObject ret = zlmApiUtils.getMediaInfo(mediaInfo, "rtp", "rtmp", streamId);
        int code = ret.getInteger("code");
        if (code == 0) {
            int readerCount = ret.getInteger("readerCount");
            log.info("还有{}位用户正在观看该流！", readerCount);
            if (readerCount < 1) {
                sipCmd.streamByeCmd(dev, channelId, streamId, null);
            }
        } else {
            log.info("流详细信息：{}，错误码：{}", ret, code);
        }
        return "";
    }

    @Override
    public Stream playback(String deviceId, String channelId, String startTime, String endTime) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        VideoSessionInfo info = sipCmd.playbackStreamCmd(dev, channelId, startTime, endTime);
        return zmlHookService.updateStream(info);
    }

    @Override
    public String playbackPause(String deviceId, String channelId, String streamId) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        VideoSessionInfo sinfo = streamSession.getSessionInfo(deviceId, channelId, streamId, null);
        if (null == sinfo) {
            return "streamId不存在";
        }
        rtspCmd.setCseq(sinfo.getStream());
        rtspCmd.playPause(dev, channelId, streamId);
        return null;
    }

    @Override
    public String playbackReplay(String deviceId, String channelId, String streamId) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        VideoSessionInfo sinfo = streamSession.getSessionInfo(deviceId, channelId, streamId, null);
        if (null == sinfo) {
            return "streamId不存在";
        }
        rtspCmd.setCseq(streamId);
        rtspCmd.playReplay(dev, channelId, streamId);
        return null;
    }

    @Override
    public String playbackSeek(String deviceId, String channelId, String streamId, long seektime) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        VideoSessionInfo sinfo = streamSession.getSessionInfo(deviceId, channelId, streamId, null);
        if (null == sinfo) {
            return "streamId不存在";
        }
        rtspCmd.setCseq(streamId);
        rtspCmd.playBackSeek(dev, channelId, streamId, seektime);
        return null;
    }

    @Override
    public String playbackSpeed(String deviceId, String channelId, String streamId, Integer speed) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        VideoSessionInfo sinfo = streamSession.getSessionInfo(deviceId, channelId, streamId, null);
        if (null == sinfo) {
            return "streamId不存在";
        }
        rtspCmd.setCseq(streamId);
        rtspCmd.playBackSpeed(dev, channelId, streamId, speed);
        return null;
    }
}
