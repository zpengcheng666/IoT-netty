package com.sydh.mqtt.server;

import com.sydh.common.constant.SYDHConstant;
import com.sydh.mqtt.codec.WebSocketMqttCodec;
import com.sydh.mqtt.handler.adapter.MqttMessageAdapter;
import com.sydh.server.Server;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author gsb
 * @date 2022/9/15 14:23
 */
@Component
@Slf4j
public class WebSocketServer extends Server {

    @Autowired
    private WebSocketMqttCodec webSocketMqttCodec;
    @Autowired
    private MqttMessageAdapter mqttMessageAdapter;

    @Autowired
    private WebSocketLogoutServer webSocketLogoutServer;


    @Override
    protected AbstractBootstrap initialize() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        return new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline()
                                .addFirst(SYDHConstant.WS.HEART_BEAT
                                        , new IdleStateHandler(60, 60, 0, TimeUnit.SECONDS))
                                /*http请求响应*/
                                .addLast(SYDHConstant.WS.HTTP_SERVER_CODEC, new HttpServerCodec())
                                /*聚合header与body组成完整的Http请求，最大数据量为1Mb*/
                                .addLast(SYDHConstant.WS.AGGREGATOR, new HttpObjectAggregator(1024 * 1024))
                                /*压缩出站数据*/
                                .addLast(SYDHConstant.WS.COMPRESSOR, new HttpContentCompressor())
                                /*WebSocket协议配置mqtt*/
                                .addLast(SYDHConstant.WS.PROTOCOL, new WebSocketServerProtocolHandler("/mqtt",
                                        "mqtt,mqttv3.1,mqttv3.1.1,mqttv5.0", true, 65536))
//                                .addLast(SYDHConstant.WS.PROTOCOL, new WebSocketServerProtocolHandler("/ws",
//                                         true, 65536))
                                .addLast(SYDHConstant.WS.MQTT_WEBSOCKET, webSocketMqttCodec)
                                .addLast(SYDHConstant.WS.DECODER, new MqttDecoder())
                                .addLast(SYDHConstant.WS.ENCODER, MqttEncoder.INSTANCE)
                                .addLast(SYDHConstant.WS.BROKER_HANDLER, mqttMessageAdapter);
                    }
                });
    }
}
