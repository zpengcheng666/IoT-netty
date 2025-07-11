package com.sydh.sip.service;

import com.sydh.sip.model.Stream;

public interface IPlayService {

    Stream play(String deviceId, String channelId, boolean record);

    String screenshot(String deviceId, String channelId);

    Stream playback(String deviceId, String channelId, String startTime, String endTime);

    String closeStream(String deviceId, String channelId, String streamId);

    String playbackPause(String deviceId, String channelId, String streamId);

    String playbackReplay(String deviceId, String channelId, String streamId);

    String playbackSeek(String deviceId, String channelId, String streamId, long seektime);

    String playbackSpeed(String deviceId, String channelId, String streamId, Integer speed);
}
