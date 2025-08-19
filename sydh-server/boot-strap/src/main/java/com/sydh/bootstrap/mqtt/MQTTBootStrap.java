package com.sydh.bootstrap.mqtt;

import com.sydh.common.enums.ServerType;
import com.sydh.mqtt.server.MqttServer;
import com.sydh.mqtt.server.WebSocketLogoutServer;
import com.sydh.mqtt.server.WebSocketServer;
import com.sydh.server.Server;
import com.sydh.server.config.NettyConfig;
import com.sydh.server.config.NettyConfigFactory;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * MQTT-BROKER启动
 * @author gsb
 * @date 2022/9/17 17:25
 */
@Order(10)
@Configuration
@ConfigurationProperties(value = "server.broker")
@Data
public class MQTTBootStrap {

    @Autowired
    private MqttServer mqttServer;
    @Autowired
    private WebSocketServer webSocketServer;
    @Autowired
    private NettyConfigFactory configFactory;
    /*端口*/
    private int port;
    /*心跳时间*/
    private int keepAlive;
    /*webSocket端口*/
    private int websocketPort;
    /*webSocket路由*/
    private String websocketPath;


    /**
     * 启动mqttBroker
     * @return server
     */
    @ConditionalOnProperty(value = "server.broker.enabled", havingValue = "true")
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server mqttBroker() {
        return configFactory.createBuilder(ServerType.MQTT)
                .setIdleStateTime(0,0,keepAlive)
                .setName(ServerType.MQTT.getDes())
                .setPort(port)
                .setServer(mqttServer)
                .build();
    }

    @ConditionalOnProperty(value = "server.broker.enabled", havingValue = "true")
    @Bean(initMethod = "start",destroyMethod = "stop")
    public Server webSocket(){
        return configFactory.createBuilder(ServerType.WEBSOCKET)
                .setIdleStateTime(0,0,keepAlive)
                .setName(ServerType.WEBSOCKET.getDes())
                .setPort(websocketPort)
                .setServer(webSocketServer)
                .build();
    }
}
