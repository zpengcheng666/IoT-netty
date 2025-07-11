package com.sydh.sip.handler;

import javax.sip.RequestEvent;

public interface IReqHandler {
    public void processMsg(RequestEvent evt);
}
