package com.sydh.sip.service;

import com.sydh.sip.model.Stream;

public interface ITalkService {
    Stream getBroadcastUrl(String deviceId, String channelId);
    String broadcast(String deviceId, String channelId);
    String broadcastStop(String deviceId, String channelId);
}
