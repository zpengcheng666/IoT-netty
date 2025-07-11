package com.sydh.sip.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InviteInfo {
    private String ssrc;
    private String callId;
    private String fromTag;
    private String viaTag;
    private String toTag;
    private int port;
}
