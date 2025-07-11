package com.sydh.bootstrap.udp;

import com.sydh.base.core.HandlerMapping;
import com.sydh.base.session.SessionManager;
import com.sydh.bootstrap.tcp.config.TcpHandlerInterceptor;
import com.sydh.common.enums.ServerType;
import com.sydh.modbus.codec.MessageAdapter;
import com.sydh.server.Server;
import com.sydh.server.config.NettyConfig;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * UDP服务端启动
 * @author bill
 */
@Order(12)
@Configuration
@ConditionalOnProperty(value = "server.udp.enabled", havingValue = "true")//设置是否启动
@ConfigurationProperties(value = "server.udp")
@Data
public class UDPBootStrap {

    private int port;
    private byte delimiter;

    private MessageAdapter messageAdapter;
    private HandlerMapping handlerMapping;
    private TcpHandlerInterceptor handlerInterpolator;
    private SessionManager sessionManager;

    public UDPBootStrap(MessageAdapter messageAdapter, HandlerMapping handlerMapping, TcpHandlerInterceptor interpolator, SessionManager sessionManager){
        this.messageAdapter = messageAdapter;
        this.handlerMapping = handlerMapping;
        this.handlerInterpolator = interpolator;
        this.sessionManager = sessionManager;
    }

    @Bean(initMethod = "start",destroyMethod = "stop")
    public Server UDPServer(){
        return NettyConfig.custom()
                .setPort(port)
                //.setDelimiters(new Delimiter(new byte[]{0x0D},false)) //分隔符配置
                .setDecoder(messageAdapter)
                .setEncoder(messageAdapter)
                .setHandlerMapping(handlerMapping)
                .setHandlerInterceptor(handlerInterpolator)
                .setSessionManager(sessionManager)
                .setName(ServerType.UDP.getDes())
                .setType(ServerType.UDP)
                .build();
    }

}
