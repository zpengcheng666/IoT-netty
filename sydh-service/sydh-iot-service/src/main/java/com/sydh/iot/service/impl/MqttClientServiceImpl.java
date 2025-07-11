package com.sydh.iot.service.impl;

import com.sydh.iot.model.MqttClientConfig;
import com.sydh.iot.ruleEngine.MqttClientFactory;
import org.springframework.stereotype.Service;
import com.sydh.iot.domain.MqttClient;
import com.sydh.iot.service.IMqttClientService;

/**
 * mqtt桥接配置表Service业务层处理
 *
 * @author gx_ma
 * @date 2024-06-03
 */
@Service
public class MqttClientServiceImpl implements IMqttClientService
{
    @Override
    public void deleteMqttClientByKey(String key) {
        MqttClientFactory.remove(key);
    }

    public MqttClientConfig buildmqttclientconfig(MqttClient client) {
        return MqttClientConfig.builder()
                .key(client.getHostUrl() + client.getClientId())
                .hostUrl(client.getHostUrl())
                .clientId(client.getClientId())
                .username(client.getUsername())
                .password(client.getPassword())
                .build();
    }
}
