package com.sydh.mqtt.server;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LogoutConsumer {


    @RabbitListener(queues = "SEND_LOGOUT_84fb604a-a892abccfd54")
    @RabbitHandler
    public void receive(Message message, Channel channel) throws Exception{
        String msg = new String(message.getBody());
        WebSocketLogoutServer.broadcastMessage(msg);
    }
}
