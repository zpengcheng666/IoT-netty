package com.sydh.server;


import com.sydh.common.constant.SYDHConstant;
import com.sydh.server.config.NettyConfig;
import com.sydh.server.handler.*;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TCP服务
 * 粘包处理为 分隔符和固定长度
 * 需要其他方式处理粘包，按照流程添加
 *
 * @author bill
 */
public class TCPServer extends Server {

    public TCPServer(NettyConfig config) {
        super(config);
    }

    @Override
    protected AbstractBootstrap initialize() {
        bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory(config.name, Thread.MAX_PRIORITY));
        workerGroup = new NioEventLoopGroup(config.workerCore, new DefaultThreadFactory(config.name, Thread.MAX_PRIORITY));
        if (config.businessCore > 0) {
            // 优化业务线程池配置：使用有界队列防止OOM
            businessService = new ThreadPoolExecutor(
                config.businessCore, 
                config.businessMaxThreads,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(config.businessQueueSize),
                new DefaultThreadFactory(config.name + "-B", true, Thread.NORM_PRIORITY),
                new ThreadPoolExecutor.CallerRunsPolicy() // 背压策略
            );
        }
        return new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(NioChannelOption.SO_REUSEADDR, true)
                .option(NioChannelOption.SO_BACKLOG, config.backlogSize) // 使用配置的backlog
                .childOption(NioChannelOption.TCP_NODELAY, true)
                .childOption(NioChannelOption.SO_KEEPALIVE, true) // 启用keepalive
                .childOption(NioChannelOption.SO_RCVBUF, 64 * 1024) // 64KB接收缓冲区
                .childOption(NioChannelOption.SO_SNDBUF, 64 * 1024) // 64KB发送缓冲区
                .childHandler(new ChannelInitializer<NioSocketChannel>() {

                    /*第二个处理器 session处理*/
                    private final TCPMessageAdapter adapter = new TCPMessageAdapter(config.sessionManager);
                    /*3.解码 适配解码器 解码后业务处理*/
                    private final MessageDecoderWrapper decoder = new MessageDecoderWrapper(config.decoder);
                    /*3.编码 适配编码器-编码后业务处理*/
                    private final MessageEncoderWrapper encoder = new MessageEncoderWrapper(config.encoder);
                    /*4.编解码后消息分发器 同步和异步处理*/
                    private final DispatcherHandler dispatcher = new DispatcherHandler(config.handlerMapping, config.handlerInterceptor, bossGroup);

                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new IdleStateHandler(config.readerIdleTime, config.writerIdleTime, config.allIdleTime)) //设置心跳时间
                                // .addLast(SYDHConstant.SERVER.FRAMEDECODER, frameDecoder())//粘包处理器
                                .addLast(SYDHConstant.SERVER.ADAPTER, adapter)//消息适配器
                                .addLast(SYDHConstant.SERVER.DECODER, decoder) //报文解码器
                                .addLast(SYDHConstant.SERVER.ENCODER, encoder) //报文编码器
                                .addLast(SYDHConstant.SERVER.DISPATCHER, dispatcher); //消息分发
                    }
                });
    }

    /**
     * 添加TCP粘包处理器
     */
    private ByteToMessageDecoder frameDecoder() {
        if (config.lengthField == null) {
            /*分隔符处理器，报文以固定包头包尾结束*/
            return new DelimiterBasedFrameDecoder(config.maxFrameLength, config.delimiters);
        }
        /*报文长度的，以长度固定处理器和分隔符处理器 处理*/
        return new LengthFieldAndDelimiterFrameDecoder(config.maxFrameLength, config.lengthField, config.delimiters);
    }
}
