package com.sydh.bootstrap.websocket;


import com.sydh.mqtt.server.WebSocketLogoutServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class WebSocketBootstrap {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture channelFuture;

    // 初始化Netty服务器
    @PostConstruct
    public void start() throws Exception {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // HTTP编解码器
                            ch.pipeline().addLast(new HttpServerCodec());
                            // 支持大数据流
                            ch.pipeline().addLast(new ChunkedWriteHandler());
                            // HTTP消息聚合器
                            ch.pipeline().addLast(new HttpObjectAggregator(65536));
                            // 心跳检测（60秒无读写则关闭连接）
                            ch.pipeline().addLast(new IdleStateHandler(60, 60, 0, TimeUnit.SECONDS));
                            // WebSocket协议处理器
                            ch.pipeline().addLast(new WebSocketServerProtocolHandler("/ws"));
                            // 自定义消息处理器
                            ch.pipeline().addLast(new WebSocketLogoutServer());
                        }
                    });

            // 绑定端口并启动服务器
            channelFuture = bootstrap.bind(8089).sync();
            log.info("Netty额外WebSocket服务器已启动，端口: " + 8089);
        } catch (Exception e) {
            stop();
            throw e;
        }
    }

    // 关闭Netty服务器
    @PreDestroy
    public void stop() {
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        System.out.println("Netty WebSocket服务器已关闭");
    }
}
