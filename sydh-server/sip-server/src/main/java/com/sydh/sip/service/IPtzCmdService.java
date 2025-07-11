package com.sydh.sip.service;

import com.sydh.sip.enums.Direct;

public interface IPtzCmdService {
    public boolean directPtzCmd(String deviceId, String channelId, Direct direct, Integer speed);
}
