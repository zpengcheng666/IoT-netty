package com.sydh.sip.model;

import lombok.Data;

@Data
public class RecordInput {
    String deviceId;
    String channelId;
    String startTime;
    String endTime;
}
