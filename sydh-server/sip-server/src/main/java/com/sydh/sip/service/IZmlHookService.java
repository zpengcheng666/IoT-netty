package com.sydh.sip.service;

import com.alibaba.fastjson.JSONObject;
import com.sydh.sip.model.Stream;
import com.sydh.sip.model.VideoSessionInfo;

public interface IZmlHookService {
    JSONObject onHttpAccess(JSONObject json);
    JSONObject onPlay(JSONObject json);
    JSONObject onPublish(JSONObject json);
    JSONObject onStreamNoneReader(JSONObject json);
    JSONObject onStreamNotFound(JSONObject json);
    JSONObject onStreamChanged(JSONObject json);
    JSONObject onFlowReport(JSONObject json);
    JSONObject onRtpServerTimeout(JSONObject json);
    JSONObject onSendRtpStopped(JSONObject json);
    JSONObject onRecordMp4(JSONObject json);
    JSONObject onServerStarted(JSONObject json);
    JSONObject onServerKeepalive(JSONObject json);
    JSONObject onServerExited(JSONObject json);
    Stream updateStream(VideoSessionInfo sinfo);
    Stream buildPushRtc(String app, String deviceId, String channelId);
}
