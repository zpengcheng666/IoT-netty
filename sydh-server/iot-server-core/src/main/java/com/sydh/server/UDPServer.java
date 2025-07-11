package com.sydh.server;

import com.sydh.common.constant.SYDHConstant;
import com.sydh.server.config.NettyConfig;
import com.sydh.server.handler.DispatcherHandler;
import com.sydh.server.handler.MessageDecoderWrapper;
import com.sydh.server.handler.MessageEncoderWrapper;
import com.sydh.server.handler.UDPMessageAdapter;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * UDP服务
 *
 * @author gsb
 * @date 2022/11/7 13:44
 */
public class UDPServer extends Server {

    public UDPServer(NettyConfig config) {
        super(config);
    }

    @Override
    protected AbstractBootstrap initialize() {
        bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory(config.name, Thread.MAX_PRIORITY));
        if (config.businessCore > 0) {
            businessService = new ThreadPoolExecutor(config.businessCore, config.businessCore, 1L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(), new DefaultThreadFactory(config.name + "-B", true, Thread.NORM_PRIORITY));
        }
        return new Bootstrap()
                .group(bossGroup)
                .channel(NioDatagramChannel.class)
                .option(NioChannelOption.SO_REUSEADDR, true)
                .option(NioChannelOption.SO_RCVBUF, 1024 * 1024 * 50)
                .handler(new ChannelInitializer<NioDatagramChannel>() {

                    private final UDPMessageAdapter adapter = UDPMessageAdapter.newInstance(config.sessionManager, config.readerIdleTime, config.delimiters);
                    private final MessageDecoderWrapper decoder = new MessageDecoderWrapper(config.decoder);
                    private final MessageEncoderWrapper encoder = new MessageEncoderWrapper(config.encoder);
                    private final DispatcherHandler dispatcherHandler = new DispatcherHandler(config.handlerMapping, config.handlerInterceptor, businessService);

                    @Override
                    protected void initChannel(NioDatagramChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(SYDHConstant.SERVER.ADAPTER, adapter)
                                .addLast(SYDHConstant.SERVER.DECODER, decoder)
                                .addLast(SYDHConstant.SERVER.ENCODER, encoder)
                                .addLast(SYDHConstant.SERVER.DISPATCHER, dispatcherHandler);
                    }
                });
    }
}
