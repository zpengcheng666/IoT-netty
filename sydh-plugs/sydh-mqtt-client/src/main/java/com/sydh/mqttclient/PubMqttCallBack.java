package com.sydh.mqttclient;

import com.sydh.common.utils.gateway.mq.TopicsPost;
import com.sydh.common.utils.gateway.mq.TopicsUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * mqtt客户端回调
 */
@Slf4j
@Component
@Data
@NoArgsConstructor
public class PubMqttCallBack implements MqttCallbackExtended {
    /**
     * mqtt客户端
     */
    private MqttAsyncClient client;
    /**
     * 创建客户端参数
     */
    private MqttConnectOptions options;

    @Resource
    private TopicsUtils topicsUtils;

    @Resource
    private PubMqttClient mqttClient;

    private Boolean enabled;

    private IMqttMessageListener listener;


    public PubMqttCallBack(MqttAsyncClient client, MqttConnectOptions options, Boolean enabled, IMqttMessageListener listener) {
        this.client = client;
        this.options = options;
        this.enabled = enabled;
        this.listener = listener;
    }

    /**
     * mqtt客户端连接
     *
     * @param cause 错误
     */
    @Override
    public void connectionLost(Throwable cause) {
        // 连接丢失后，一般在这里面进行重连
        log.debug("=>mqtt 连接丢失", cause);
        mqttClient.setConnected(false);
        int count = 1;
        // int sleepTime = 0;
        boolean willConnect = true;
        while (willConnect) {
            try {
                Thread.sleep(1000);
                log.debug("=>连接[{}]断开，尝试重连第{}次", this.client.getServerURI(), count++);
                this.client.connect(this.options);
                mqttClient.setConnected(true);
                log.debug("=>重连成功");
                willConnect = false;
            } catch (Exception e) {
                log.error("=>重连异常", e);
            }
        }
    }

    /**
     * 客户端订阅主题回调消息
     *
     * @param topic   主题
     * @param message 消息
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // subscribe后得到的消息会执行到这里面
        try {
            log.info("订阅消息主题：{}", topic);
            log.info("订阅消息：{}", message);
            listener.messageArrived(topic, message);
        } catch (Exception e) {
            log.warn("mqtt 订阅消息异常", e);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }


    /**
     * 监听mqtt连接消息
     */
    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        log.info("✅ MQTT内部客户端已经连接!");
        mqttClient.setConnected(true);
        // 连接成功后，订阅主题，enable为false表示使用emq3
        if (!enabled) {
            try {
                TopicsPost allPost = topicsUtils.getAllPost();
                client.subscribe(allPost.getTopics(), allPost.getQos());
                //client.subscribe("/+/+/status/post",1);
                log.info("✅ MQTT主题订阅成功: {}", Arrays.asList(allPost.getTopics()));
            } catch (MqttException e) {
                log.error("❌ MQTT主题订阅失败: {}", e.getMessage());
            }
        }
    }
}
