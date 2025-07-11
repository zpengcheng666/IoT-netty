package com.sydh.mqtt.handler;

import com.sydh.mqtt.annotation.Process;
import com.sydh.mqtt.handler.adapter.MqttHandler;
import com.sydh.mqtt.manager.ClientManager;
import com.sydh.mqtt.manager.ResponseManager;
import com.sydh.base.session.Session;
import com.sydh.base.util.AttributeUtils;
import com.sydh.mqtt.utils.MqttMessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * topic取消订阅处理
 *
 * @author bill
 */
@Slf4j
@Process(type = MqttMessageType.UNSUBSCRIBE)
public class MqttUnsubscribe implements MqttHandler {

    @Override
    public void handler(ChannelHandlerContext ctx, MqttMessage message) {
        MqttUnsubscribeMessage unsubscribeMessage = (MqttUnsubscribeMessage) message;
        List<String> topics = unsubscribeMessage.payload().topics();
        log.debug("=>收到取消订阅请求,topics[{}]", topics);
        Session session = AttributeUtils.getSession(ctx.channel());
        topics.forEach(topic -> {
            ClientManager.unsubscribe(topic, session);
        });
        MqttUnsubAckMessage unsubAckMessage = MqttMessageUtils.buildUnsubAckMessage(unsubscribeMessage);
        ResponseManager.responseMessage(session, unsubAckMessage, true);
        /*更新客户端平台时间*/
        ClientManager.updatePing(session.getClientId());
    }
}
