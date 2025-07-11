
package com.sydh.coap.handler;

import com.sydh.coap.model.CoapRequest;
import com.sydh.coap.model.CoapResponse;
import com.google.common.util.concurrent.SettableFuture;

import java.net.InetSocketAddress;

public interface RequestConsumer {
    public void processCoapRequest(SettableFuture<CoapResponse> responseFuture, CoapRequest coapRequest,
                                            InetSocketAddress remoteSocket) throws Exception;
}
