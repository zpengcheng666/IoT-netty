package com.sydh.mqtt.handler;

import com.sydh.base.service.ISessionStore;
import com.sydh.base.session.Session;
import com.sydh.base.util.AttributeUtils;
import com.sydh.mqtt.annotation.Process;
import com.sydh.mqtt.handler.adapter.MqttHandler;
import com.sydh.mqtt.manager.ClientManager;
import com.sydh.mqtt.manager.ResponseManager;
import com.sydh.mqtt.manager.SessionManger;
import com.sydh.mqtt.utils.MqttMessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * 客户端Ping消息应答
 *
 * @author bill
 */
@Slf4j
@Process(type = MqttMessageType.PINGREQ)
public class MqttPingreq implements MqttHandler {

    @Resource
    private ISessionStore sessionStore;

    @Override
    public void handler(ChannelHandlerContext ctx, MqttMessage message) {
        /*获取客户端id*/
        String clientId = AttributeUtils.getClientId(ctx.channel());
        //平台检测session是否同步
        Session session = AttributeUtils.getSession(ctx.channel());
        boolean containsKey = sessionStore.containsKey(clientId);
        if (!containsKey){
            SessionManger.buildSession(clientId,session);
        }
        try {
            // log.debug("=>客户端:{},心跳信息", clientId);
            /*更新客户端ping时间*/
            ClientManager.updatePing(clientId);
            /*响应设备的ping消息*/
            MqttMessage pingResp = MqttMessageUtils.buildPingResp();
            ResponseManager.sendMessage(pingResp, clientId, true);
        } catch (Exception e) {
            log.error("=>客户端:{},ping异常:{}", clientId, e);
        }
    }
}
