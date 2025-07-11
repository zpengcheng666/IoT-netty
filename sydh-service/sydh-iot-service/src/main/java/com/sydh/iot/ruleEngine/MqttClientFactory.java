package com.sydh.iot.ruleEngine;

import com.sydh.iot.model.MqttClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MqttClientFactory {

    private static final Map<String, MqttAsyncClient> CLIENT_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, MqttConnectOptions> CLIENT_OPTIONS_CACHE = new ConcurrentHashMap<>();

    public static MqttAsyncClient instance(MqttClientConfig config, MqttCallbackonSuccess callback) throws MqttException {
        if (config.getKey() == null) {
            return null;
        }
        MqttAsyncClient client = CLIENT_CACHE.get(config.getKey());
        if (client == null) {
            log.debug("=>mqtt客户端：{} 不存在", config.getKey());
            client = instanceNew(config, callback);
            CLIENT_CACHE.put(config.getKey(), client);
        } else {
            log.debug("=>mqtt客户端：{} 存在", config.getKey());
            MqttConnectOptions options = CLIENT_OPTIONS_CACHE.get(config.getKey());
            if (options != null && Objects.equals(options.getUserName(), config.getUsername()) && new String(options.getPassword()).equals(config.getPassword())) {
                if (!client.isConnected()) {
                    client.reconnect();
                }
            } else {
                log.debug("=>mqtt客户端：{} 配置不一致，重建", config.getKey());
                client = instanceNew(config, callback);
                CLIENT_CACHE.put(config.getKey(), client);
            }
        }
        return client;
    }

    public static MqttAsyncClient instanceNew(MqttClientConfig config, MqttCallbackonSuccess callback) {
        MqttAsyncClient client = null;
        log.debug("=>mqtt客户端config：{}", config);
        try {
            client = new MqttAsyncClient(config.getHostUrl(), config.getClientId(), new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(config.getUsername());
            options.setPassword(config.getPassword().toCharArray());
            options.setCleanSession(false);
            options.setAutomaticReconnect(true);
            if (callback != null) {
                callback.setClient(client);
            }
            IMqttToken token = client.connect(options, null, callback);
            token.waitForCompletion();
            CLIENT_OPTIONS_CACHE.put(config.getKey(), options);
        } catch (MqttException e) {
            log.error("=>mqtt客户端创建错误，原因：" + e.getMessage());
        }
        return client;
    }

    public static void addSubscribe(String key, String topic) {
        try {
            MqttAsyncClient client = CLIENT_CACHE.get(key);
            if (client != null && client.isConnected()) {
                log.warn("addSubscribe：{}，topic：{}", key, topic);
                client.subscribe(topic, 0, new MessageArrivedHandler(topic, client.getClientId()));
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static void addSubscribe(MqttAsyncClient client, String topic) {
        try {
            log.warn("addSubscribe：{}，topic：{}", client.getClientId(), topic);
            client.subscribe(topic, 0, new MessageArrivedHandler(topic, client.getClientId()));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static void delSubscribe(String key, String topic) {
        try {
            MqttAsyncClient client = CLIENT_CACHE.get(key);
            if (client != null) {
                client.unsubscribe(topic);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static void remove(String key) {
        MqttAsyncClient client = CLIENT_CACHE.get(key);
        if (client != null) {
            try {
                if (client.isConnected()) {
                    client.disconnect();
                    client.close();
                }
                CLIENT_CACHE.remove(key);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
