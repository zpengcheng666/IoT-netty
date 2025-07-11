package com.sydh.bootstrap.http;

import com.sydh.common.enums.ServerType;
import com.sydh.http.server.HttpServer;
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

@Order(13)
@Configuration
@ConditionalOnProperty(name = "server.http.enabled", havingValue = "true")//设置是否启动
@ConfigurationProperties(value = "server.http")
@Data
public class HttpBootStrap {
    @Autowired
    private HttpServer httpServer;
    @Autowired
    private NettyConfigFactory configFactory;
    private int port;

    @Bean(initMethod = "start",destroyMethod = "stop")
    public Server httpServerBoot(){
        return configFactory.createBuilder(ServerType.HTTP)
                .setPort(port)
                .setName(ServerType.HTTP.getDes())
                .setServer(httpServer)
                .build();
    }
}
