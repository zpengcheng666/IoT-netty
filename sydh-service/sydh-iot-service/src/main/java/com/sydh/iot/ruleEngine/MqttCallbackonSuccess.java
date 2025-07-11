package com.sydh.iot.ruleEngine;

import com.sydh.iot.domain.Bridge;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;

import java.util.Objects;

/**
 * @author zhuangpengli
 */
@Slf4j
@Data
public class MqttCallbackonSuccess implements IMqttActionListener {
    private Bridge bridge;
    private MqttAsyncClient client;

    public MqttCallbackonSuccess(Bridge bridge) {
        this.bridge = bridge;
    }
    @Override
    public void onSuccess(IMqttToken iMqttToken) {
        log.info("MqttAsyncClientId：{}，ServerURI：{}", client.getClientId(), client.getServerURI());
        if (client != null && bridge.getDirection() == 1 && !Objects.equals(bridge.getRoute(), "")) {
            MqttClientFactory.addSubscribe(client, bridge.getRoute());
        }
    }

    @Override
    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {

    }
}
