package com.sydh.coap.handler;

import com.sydh.coap.model.CoapMessage;
import com.sydh.coap.model.CoapRequest;
import com.sydh.coap.server.ResourceRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
@Setter
@Getter
public class ReqDispatcher extends SimpleChannelInboundHandler<CoapMessage> {

    private ResourceRegistry resourceRegistry;
    private ChannelHandlerContext context;

    public ReqDispatcher(ResourceRegistry resourceRegistry) {
        this.resourceRegistry = resourceRegistry;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, CoapMessage coapMessage) throws Exception {
        log.debug("ReqDispatcher message: {}", coapMessage);
        if (!(coapMessage instanceof CoapRequest)) {
            log.info("remoteAddress:{}",coapMessage.getSender());
            return;
        }
        CoapRequest coapRequest = (CoapRequest) coapMessage;
        Object response = resourceRegistry.respond(coapRequest);
        if (response != null) {
            channelHandlerContext.writeAndFlush(response);
        } else {
            log.warn("No response generated for CoapRequest: {}", coapRequest);
            // 根据业务需求，这里可以发送一个错误响应或者不做任何操作
        }
    }

}
