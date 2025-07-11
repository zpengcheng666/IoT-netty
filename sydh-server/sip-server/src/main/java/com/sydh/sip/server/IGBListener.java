package com.sydh.sip.server;

import com.sydh.sip.handler.IReqHandler;
import com.sydh.sip.handler.IResHandler;

import javax.sip.SipListener;

public interface IGBListener extends SipListener {
    public void addRequestProcessor(String method, IReqHandler processor);
    public void addResponseProcessor(String method, IResHandler processor);
}
