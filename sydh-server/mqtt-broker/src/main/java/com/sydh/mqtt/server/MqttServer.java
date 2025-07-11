package com.sydh.mqtt.server;

import com.sydh.common.constant.SYDHConstant;
import com.sydh.mqtt.handler.adapter.MqttMessageAdapter;
import com.sydh.server.Server;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MqttServer extends Server {

    @Autowired
    private MqttMessageAdapter messageAdapter;

    @Override
    protected AbstractBootstrap initialize() {
        bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory(config.name, Thread.MAX_PRIORITY));
        workerGroup = new NioEventLoopGroup(config.workerCore, new DefaultThreadFactory(config.name, Thread.MAX_PRIORITY));

        if (config.businessCore > 0) {
            // 优化MQTT业务线程池：支持大量订阅发布操作
            businessService = new ThreadPoolExecutor(
                config.businessCore, 
                config.businessMaxThreads,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(config.businessQueueSize),
                new DefaultThreadFactory(config.name + "-MQTT-B", true, Thread.NORM_PRIORITY),
                new ThreadPoolExecutor.CallerRunsPolicy() // 背压策略
            );
        }
        return new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO)) // 调整日志级别
                .option(ChannelOption.SO_BACKLOG, config.backlogSize) // 使用配置的backlog
                .option(ChannelOption.SO_REUSEADDR, true) // 端口复用
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .childOption(ChannelOption.TCP_NODELAY, true) // 禁用Nagle算法
                .childOption(ChannelOption.SO_RCVBUF, 32 * 1024) // 32KB接收缓冲区
                .childOption(ChannelOption.SO_SNDBUF, 32 * 1024) // 32KB发送缓冲区
                .childHandler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel channel) {
                        //客户端心跳检测机制
                        channel.pipeline()
                                .addFirst(SYDHConstant.SERVER.IDLE
                                        , new IdleStateHandler(config.readerIdleTime, config.writerIdleTime, config.allIdleTime, TimeUnit.SECONDS))
                                .addLast(SYDHConstant.SERVER.DECODER, new MqttDecoder(1024 * 1024 * 2))
                                .addLast(SYDHConstant.SERVER.ENCODER, MqttEncoder.INSTANCE)
                                .addLast(messageAdapter);
                    }
                });


    }
}
