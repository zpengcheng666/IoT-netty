package com.sydh.mqtt.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketLogoutServer extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    // 存储客户端ID与Channel的映射关系
    private static final Map<String, Channel> CLIENT_CHANNELS = new ConcurrentHashMap<>();
    // 存储Channel与客户端ID的映射关系
    private static final Map<Channel, String> CHANNEL_CLIENTS = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        try {
            String content = textWebSocketFrame.text();
            Channel channel = channelHandlerContext.channel();
            log.info("收到消息: {}", content);

            // 处理客户端注册（格式: register:clientId）
            if (content.startsWith("register:")) {
                String clientId = content.substring("register:".length()).trim();
                registerClient(channel, clientId);
                sendMessageToClient(clientId, "注册成功，客户端ID: " + clientId);
                return;
            }

            // 处理定向消息（格式: @clientId:message）
            if (content.startsWith("@")) {
                int colonIndex = content.indexOf(":");
                if (colonIndex > 1) {
                    String targetClientId = content.substring(1, colonIndex);
                    String message = content.substring(colonIndex + 1);
                    sendMessageToClient(targetClientId, message);

                    // 回复发送者消息已送达
                    channel.writeAndFlush(new TextWebSocketFrame("已发送给 " + targetClientId + ": " + message));
                    return;
                }
            }

            // 默认广播消息
            broadcastMessage(content, channel);
        } catch (Exception e) {
            log.error("处理消息异常", e);
        }
    }

    private void registerClient(Channel channel, String clientId) {
        // 如果客户端ID已存在，先移除旧连接
        if (CLIENT_CHANNELS.containsKey(clientId)) {
            Channel oldChannel = CLIENT_CHANNELS.get(clientId);
            CHANNEL_CLIENTS.remove(oldChannel);
            oldChannel.close();
        }

        CLIENT_CHANNELS.put(clientId, channel);
        CHANNEL_CLIENTS.put(channel, clientId);
        log.info("客户端注册: {}", clientId);
        broadcastMessage("新客户端上线: " + clientId);
    }

    public static boolean sendMessageToClient(String clientId, String message) {
        Channel channel = CLIENT_CHANNELS.get(clientId);
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return true;
        }
        return false;
    }

    // 广播消息给所有客户端
    public static void broadcastMessage(String message) {
        broadcastMessage(message, null);
    }

    // 广播消息给所有客户端，排除指定通道
    public static void broadcastMessage(String message, Channel excludeChannel) {
        TextWebSocketFrame frame = new TextWebSocketFrame(message);
        Map<String, Channel> clientChannels = CLIENT_CHANNELS;
        Collection<Channel> values = clientChannels.values();
        for (Channel channel : values) {
            if (channel != excludeChannel && channel.isActive()) {
                channel.writeAndFlush(frame.retain());
            }
        }
    }
}
