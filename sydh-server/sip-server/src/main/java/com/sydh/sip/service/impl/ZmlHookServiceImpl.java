package com.sydh.sip.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sydh.sip.conf.SysSipConfig;
import com.sydh.sip.domain.MediaServer;
import com.sydh.sip.enums.SessionType;
import com.sydh.sip.model.Stream;
import com.sydh.sip.model.VideoSessionInfo;
import com.sydh.sip.model.vo.MediaServerVO;
import com.sydh.sip.server.VideoSessionManager;
import com.sydh.sip.service.IMediaServerService;
import com.sydh.sip.service.IPlayService;
import com.sydh.sip.service.IZmlHookService;
import com.sydh.sip.util.SipUtil;
import com.sydh.sip.util.ZlmApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ZmlHookServiceImpl implements IZmlHookService {

    @Autowired
    private VideoSessionManager videoSessionManager;

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private ZlmApiUtils zlmApiUtils;

    @Autowired
    private SysSipConfig sipConfig;

    @Autowired
    private IPlayService playService;

    @Override
    public JSONObject onHttpAccess(JSONObject json) {
        log.warn("on_http_access：" + json.toString());
        JSONObject ret = new JSONObject();
        // 获取秘钥值
        String secretValue = "";
        String input = json.getString("params");
        String[] params = input.split("&");
        for (String param : params) {
            // 检查是否包含 secret
            if (param.startsWith("secret=")) {
                secretValue = param.substring("secret=".length());
                break;
            }
        }
        // 获取媒体服务器
        if (!secretValue.isEmpty()) {
            String mediaServerId = json.getString("mediaServerId");
            boolean isSecretPresent = false;
            MediaServer mediaServer = new MediaServer();
            mediaServer.setServerId(mediaServerId);
            List<MediaServerVO> list = mediaServerService.selectMediaServerList(mediaServer);
            if (!list.isEmpty()) {
                for (MediaServerVO mediaServer1 : list) {
                    if (Objects.equals(mediaServer1.getSecret(), secretValue)) {
                        isSecretPresent = true;
                    }
                }
            }
            if (isSecretPresent) {
                ret.put("err", "");
            } else {
                ret.put("err", "secret is error");
            }
        } else {
            ret.put("err", "secret is null");
        }
        ret.put("code", 0);
        ret.put("path", "");
        ret.put("second", 3600);
        return ret;
    }

    @Override
    public JSONObject onPlay(JSONObject json) {
        String mediaServerId = json.getString("mediaServerId");
        String id = json.getString("id");
        String schema = json.getString("schema");
        String app = json.getString("app");
        String streamId = json.getString("stream");
        log.warn("[ZLM HOOK] 播放鉴权, {} {}->{}->{}/{}", mediaServerId, id, schema, app, streamId);
        JSONObject ret = new JSONObject();
        ret.put("code", 0);
        ret.put("msg", "success");
        VideoSessionInfo sinfo = videoSessionManager.getSessionInfoBySSRC(streamId);
        if (sinfo != null) {
            if (sinfo.isPushing()) {
                int time = sinfo.getOnPlaytime() + 1;
                sinfo.setOnPlaytime(time);
            }
            videoSessionManager.put(sinfo);
        }
        return ret;
    }

    @Override
    public JSONObject onPublish(JSONObject json) {
        log.info("onPublish:{}", json);
        String id = json.getString("id");
        String mediaServerId = json.getString("mediaServerId");
        String schema = json.getString("schema");
        String app = json.getString("app");
        String streamId = json.getString("stream");
        JSONObject ret = new JSONObject();
        VideoSessionInfo sinfo = videoSessionManager.getSessionInfoBySSRC(streamId);
        if (sinfo != null) {
            log.warn("[ZLM HOOK] 推流鉴权, {}->{}->{}/{}", mediaServerId, schema, app, streamId);
            ret.put("code", 0);
            ret.put("msg", "success");
            switch (sinfo.getType()) {
                case play:
                    ret.put("enable_fmp4", true);
                    ret.put("enable_hls", true);
                    ret.put("enable_rtmp", true);
                    ret.put("enable_rtsp", true);
                    ret.put("enable_audio", true);
                    break;
                case playrecord:
                    ret.put("enable_rtmp", true);
                    ret.put("enable_hls", true);
                    ret.put("enable_mp4", true);
                    ret.put("enable_audio", true);
                    ret.put("mp4_max_second", sipConfig.getMp4MaxSecond());
                    ret.put("mp4_as_player", true);
                    ret.put("mp4_save_path", sipConfig.getZlmRecordPath());
                    sinfo.setRecording(true);
                    break;
                case playback:
                    ret.put("enable_fmp4", true);
                    ret.put("enable_hls", true);
                    ret.put("enable_rtmp", true);
                    ret.put("enable_audio", true);
                    break;
                case download:
                    //开启录像，默认60s
                    ret.put("enable_mp4", true);
                    ret.put("enable_audio", true);
                    ret.put("mp4_max_second", sipConfig.getMp4MaxSecond());
                    ret.put("mp4_save_path", sipConfig.getZlmRecordPath());
                    break;
            }
            sinfo.setPushing(true);
            videoSessionManager.put(sinfo);
        } else {
            if (Objects.equals(app, "broadcast")) {
                //rtc推流
                ret.put("code", 0);
                ret.put("msg", "success");
                ret.put("enable_rtmp", true);
                ret.put("enable_rtsp", true);
                ret.put("enable_audio", true);
                ret.put("enable_rtc", true);
            } else {
                log.warn("[ZLM HOOK] 推流鉴权失败, {}->{}->{}/{}", mediaServerId, schema, app, streamId);
                ret.put("code", 401);
                ret.put("msg", "Unauthorized");
            }

        }
        return ret;
    }

    @Override
    public JSONObject onStreamNoneReader(JSONObject json) {
        String mediaServerId = json.getString("mediaServerId");
        String schema = json.getString("schema");
        String app = json.getString("app");
        String streamId = json.getString("stream");
        log.warn("[ZLM HOOK] 流无人观看, {}->{}->{}/{}", mediaServerId, schema, app, streamId);
        JSONObject ret = new JSONObject();
        VideoSessionInfo sinfo = videoSessionManager.getSessionInfoBySSRC(streamId);
        if (sinfo != null) {
            ret.put("code", 0);
            switch (sinfo.getType()) {
                case playrecord:
                case download:
                    ret.put("close", false);
                    break;
                default:
                    ret.put("close", true);
            }
        }
        return ret;
    }

    @Override
    public JSONObject onStreamNotFound(JSONObject json) {
        String id = json.getString("id");
        String mediaServerId = json.getString("mediaServerId");
        String schema = json.getString("schema");
        String app = json.getString("app");
        String streamId = json.getString("stream");
        log.warn("[ZLM HOOK] 流未找到, {} {}->{}->{}/{}", mediaServerId, id, schema, app, streamId);
        JSONObject ret = new JSONObject();
        ret.put("code", 0);
        ret.put("msg", "success");
        return ret;
    }

    @Override
    public JSONObject onStreamChanged(JSONObject json) {
        log.info("onStreamChanged:{}", json);
        boolean regist = json.getBoolean("regist");
        String mediaServerId = json.getString("mediaServerId");
        String schema = json.getString("schema");
        String app = json.getString("app");
        String streamId = json.getString("stream");
        VideoSessionInfo sinfo = videoSessionManager.getSessionInfoBySSRC(streamId);
        if (sinfo != null) {
            if (regist) {
                log.info("[ZLM HOOK] 流注册, {}->{}->{}/{}", mediaServerId, schema, app, streamId);
                if ("rtsp".equals(schema) && sinfo.getType() == SessionType.play) {
                    MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(sinfo.getDeviceId());
                    String streamUrl = String.format("rtsp://127.0.0.1:%s/%s/%s", mediaInfo.getPortRtsp(), "rtp", sinfo.getStream());
                    String fileName;
                    if (sinfo.getShotPath() != null) {
                        // 可以自定义文件名称规则
                        fileName = sinfo.getShotPath();
                    } else {
                        fileName = sinfo.getDeviceId() + "_" + sinfo.getChannelId() + ".jpg";
                    }
                    // 请求截图
                    log.info("[请求截图]: {}:{}", streamUrl, fileName);
                    zlmApiUtils.getSnap(mediaInfo, streamUrl, 15, 1, fileName);
                }
                sinfo.setPushing(true);
            } else {
                log.info("[ZLM HOOK] 流注销, {}->{}->{}/{}", mediaServerId, schema, app, streamId);
                if (sinfo != null) {
                    if (sinfo.isPushing()) {
                        sinfo.setPushing(false);
                    }
                    if (sinfo.isRecording()) {
                        sinfo.setRecording(false);
                    }
                }
            }
            videoSessionManager.put(sinfo);
        }
        JSONObject ret = new JSONObject();
        ret.put("code", 0);
        ret.put("msg", "success");
        return ret;
    }

    @Override
    public JSONObject onFlowReport(JSONObject json) {
        String id = json.getString("id");
        String mediaServerId = json.getString("mediaServerId");
        String schema = json.getString("schema");
        String app = json.getString("app");
        String streamId = json.getString("stream");
        int duration = json.getInteger("duration");
        int totalBytes = json.getInteger("totalBytes");
        boolean player = json.getBoolean("player");
        VideoSessionInfo sinfo = videoSessionManager.getSessionInfoBySSRC(streamId);
        if (sinfo != null) {
            if (player) {
                int time = sinfo.getOnPlaytime() - 1;
                sinfo.setOnPlaytime(time);
                log.info("[ZLM HOOK] 播放器断开，流量统计事件, {}->{}->{}/{}", mediaServerId, schema, app, streamId);
            } else {
                log.info("[ZLM HOOK] 推流器断开，流量统计事件, {}->{}->{}/{}", mediaServerId, schema, app, streamId);
                sinfo.setPushing(false);
                sinfo.setRecording(false);
                sinfo.setPlayer(0);
                sinfo.setOnPlaytime(0);
                playService.closeStream(sinfo.getDeviceId(), sinfo.getChannelId(), streamId);
            }
            videoSessionManager.put(sinfo);
        }
        log.info("[ZLM HOOK] onFlowReport：维持时间：{}/上下行流量：{}", duration, totalBytes);
        JSONObject ret = new JSONObject();
        ret.put("code", 0);
        ret.put("msg", "success");
        return ret;
    }


    @Override
    public JSONObject onRtpServerTimeout(JSONObject json) {
        String mediaServerId = json.getString("mediaServerId");
        String stream_id = json.getString("stream_id");
        String ssrc = json.getString("ssrc");
        log.warn("[ZLM HOOK] rtpServer收流超时：{}->{}({})", mediaServerId, stream_id, ssrc);
        JSONObject ret = new JSONObject();
        ret.put("code", 0);
        ret.put("msg", "success");
        return ret;
    }

    @Override
    public JSONObject onSendRtpStopped(JSONObject json) {
        log.warn("[ZLM HOOK] rtp发送停止回调：{}", json);
        String mediaServerId = json.getString("mediaServerId");
        String stream_id = json.getString("stream_id");
        String ssrc = json.getString("ssrc");
        log.warn("[ZLM HOOK] rtp发送停止回调：{}->{}({})", mediaServerId, stream_id, ssrc);
        JSONObject ret = new JSONObject();
        ret.put("code", 0);
        ret.put("msg", "success");
        return ret;
    }

    @Override
    public JSONObject onRecordMp4(JSONObject json) {
        String mediaServerId = json.getString("mediaServerId");
        String file_path = json.getString("file_path");
        String app = json.getString("app");
        String stream = json.getString("stream");
        log.info("[ZLM HOOK] 录制完成：{}->{} {} ({})", mediaServerId, app, stream, file_path);
        JSONObject ret = new JSONObject();
        ret.put("code", 0);
        ret.put("msg", "success");
        return ret;
    }

    @Override
    public JSONObject onServerStarted(JSONObject json) {
        log.info("[ZLM HOOK] 流媒体服务启动成功：({})", json);
        JSONObject ret = new JSONObject();
        ret.put("code", 0);
        ret.put("msg", "success");
        return ret;
    }

    @Override
    public JSONObject onServerKeepalive(JSONObject json) {
        log.debug("[ZLM HOOK] 流媒体服务心跳：({})", json);
        JSONObject ret = new JSONObject();
        ret.put("code", 0);
        ret.put("msg", "success");
        return ret;
    }

    @Override
    public JSONObject onServerExited(JSONObject json) {
        log.info("[ZLM HOOK] 流媒体服务存活：({})", json);
        JSONObject ret = new JSONObject();
        ret.put("code", 0);
        ret.put("msg", "success");
        return ret;
    }

    public Stream updateStream(VideoSessionInfo sInfo) {
        if (sInfo == null) {
            log.error("updateStream sInfo is null");
            return null;
        }
        String streamId = sInfo.getStream();
        String ssrc = sInfo.getSsrc();
        if (sInfo.getApp() == null) {
            sInfo.setApp("rtp");
        }
        MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(sInfo.getDeviceId());
        Stream streamUrl = new Stream(sInfo.getDeviceId(), sInfo.getChannelId(), streamId, ssrc);
        streamUrl.setRtmp(String.format("rtmp://%s:%s/%s/%s", mediaInfo.getIp(),
                mediaInfo.getPortRtmp(), sInfo.getApp(), streamId));
        streamUrl.setRtsp(String.format("rtsp://%s:%s/%s/%s", mediaInfo.getIp(),
                mediaInfo.getPortRtsp(), sInfo.getApp(), streamId));
        setFlv(sInfo.getApp(), streamUrl, mediaInfo, streamId, mediaInfo.getSecret());
        setFmp4(sInfo.getApp(), streamUrl, mediaInfo, streamId, mediaInfo.getSecret());
        setHls(sInfo.getApp(), streamUrl, mediaInfo, streamId, mediaInfo.getSecret());
        setRtc(sInfo.getApp(), streamUrl, mediaInfo, streamId, true);
        if (Objects.equals(mediaInfo.getProtocol(), "http")) {
            streamUrl.setPlayurl(streamUrl.getFlv());
        } else if (Objects.equals(mediaInfo.getProtocol(), "https")) {
            streamUrl.setPlayurl(streamUrl.getHttps_flv());
        }
        return streamUrl;
    }

    public Stream buildPushRtc(String app, String deviceId, String channelId) {
        String streamid = String.format("%s_%s_%s", SipUtil.PREFIX_TALK,
                deviceId,
                channelId);
        Stream streamUrl = new Stream(deviceId, channelId, streamid, streamid);
        MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(deviceId);
        setRtc(app, streamUrl, mediaInfo, streamid, false);
        return streamUrl;
    }

    public void setFlv(String app, Stream streamUrl, MediaServer mediaInfo, String streamId, String secret) {
        String file = String.format("%s/%s.live.flv", app, streamId);
        String host = String.format("%s:%s", mediaInfo.getIp(), mediaInfo.getPortHttp());
        String hosts = String.format("%s:%s", mediaInfo.getDomainAlias(), mediaInfo.getPortHttps());
        streamUrl.setFlv(String.format("http://%s/%s?secret=%s", host, file, secret));
        streamUrl.setWs_flv(String.format("ws://%s/%s?secret=%s", host, file,secret));
        streamUrl.setHttps_flv(String.format("https://%s/%s?secret=%s", hosts, file,secret));
        streamUrl.setWss_flv(String.format("wss://%s/%s?secret=%s", hosts, file,secret));
    }

    public void setFmp4(String app, Stream streamUrl, MediaServer mediaInfo, String streamId, String secret) {
        String file = String.format("%s/%s.live.mp4", app, streamId);
        String host = String.format("%s:%s", mediaInfo.getIp(), mediaInfo.getPortHttp());
        String hosts = String.format("%s:%s", mediaInfo.getDomainAlias(), mediaInfo.getPortHttps());
        streamUrl.setFmp4(String.format("http://%s/%s?secret=%s", host, file, secret));
        streamUrl.setWs_fmp4(String.format("ws://%s/%s?secret=%s", host, file, secret));
        streamUrl.setHttps_fmp4(String.format("https://%s/%s?secret=%s", hosts, file, secret));
        streamUrl.setWss_fmp4(String.format("wss://%s/%s?secret=%s", hosts, file, secret));
    }

    public void setHls(String app, Stream streamUrl, MediaServer mediaInfo, String streamId, String secret) {
        String file = String.format("%s/%s/hls.m3u8", app, streamId);
        String host = String.format("%s:%s", mediaInfo.getIp(), mediaInfo.getPortHttp());
        String hosts = String.format("%s:%s", mediaInfo.getDomainAlias(), mediaInfo.getPortHttps());
        streamUrl.setHls(String.format("http://%s/%s?secret=%s", host, file, secret));
        streamUrl.setWs_hls(String.format("ws://%s/%s?secret=%s", host, file, secret));
        streamUrl.setHttps_hls(String.format("https://%s/%s?secret=%s", hosts, file, secret));
        streamUrl.setWss_hls(String.format("wss://%s/%s?secret=%s", hosts, file, secret));
    }

    public void setRtc(String app, Stream streamUrl, MediaServer mediaInfo, String streamId, boolean isPlay) {
        String file = String.format("index/api/webrtc?app=%s&stream=%s&type=%s", app, streamId, isPlay ? "play" : "push");
        String host = String.format("%s:%s", mediaInfo.getIp(), mediaInfo.getPortHttp());
        String hosts = String.format("%s:%s", mediaInfo.getDomainAlias(), mediaInfo.getPortHttps());
        streamUrl.setRtc(String.format("http://%s/%s", host, file));
        streamUrl.setRtcs(String.format("https://%s/%s", hosts, file));
    }

}
