package com.sydh.coap.server;

import com.sydh.coap.codec.CoapMessageDecoder;
import com.sydh.coap.codec.CoapMessageEncoder;
import com.sydh.coap.handler.ReqDispatcher;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;


public class CoapServerChannelInitializer extends ChannelInitializer<DatagramChannel> {

    private final ResourceRegistry resourceRegistry;

    public CoapServerChannelInitializer(ResourceRegistry resourceRegistry) {
        super();
        this.resourceRegistry = resourceRegistry;
    }

    @Override
    protected void initChannel(DatagramChannel datagramChannel) throws Exception {
        ChannelPipeline p = datagramChannel.pipeline();
        p.addLast(new CoapMessageEncoder())
                .addLast(new CoapMessageDecoder())
                .addLast(new ReqDispatcher(this.resourceRegistry));
    }
}
