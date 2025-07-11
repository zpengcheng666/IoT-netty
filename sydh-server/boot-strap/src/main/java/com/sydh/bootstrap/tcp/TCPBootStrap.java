package com.sydh.bootstrap.tcp;

import com.sydh.base.codec.Delimiter;
import com.sydh.base.core.HandlerMapping;
import com.sydh.base.session.SessionManager;
import com.sydh.bootstrap.tcp.config.TcpHandlerInterceptor;
import com.sydh.common.enums.ServerType;
import com.sydh.modbus.codec.MessageAdapter;
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
 * TCP服务端启动
 * @author bill
 */
@Order(11)
@Configuration
@ConditionalOnProperty(value = "server.tcp.enabled", havingValue = "true")//设置是否启动
@ConfigurationProperties(value = "server.tcp")
@Data
public class TCPBootStrap {

    private int port;
    private int keepAlive;
    private byte delimiter;
    @Autowired
    private MessageAdapter messageAdapter;
    @Autowired
    private NettyConfigFactory configFactory;
    private HandlerMapping handlerMapping;
    private TcpHandlerInterceptor handlerInterpolator;
    private SessionManager sessionManager;

    public TCPBootStrap(HandlerMapping handlerMapping, TcpHandlerInterceptor interpolator, SessionManager sessionManager){
        this.handlerMapping = handlerMapping;
        this.handlerInterpolator = interpolator;
        this.sessionManager = sessionManager;
    }

    @Bean(initMethod = "start",destroyMethod = "stop")
    public Server TCPServer(){
        return configFactory.createBuilder(ServerType.TCP)
                .setIdleStateTime(keepAlive,0,0)
                .setPort(port)
                 //设置报文最大长度  modbus-rtu
                .setMaxFrameLength(100)
                .setDelimiters(new Delimiter(new byte[]{0x7e}, true))
                .setDecoder(messageAdapter)
                .setEncoder(messageAdapter)
                .setHandlerMapping(handlerMapping)
                .setHandlerInterceptor(handlerInterpolator)
                .setSessionManager(sessionManager)
                .setName(ServerType.TCP.getDes())
                .build();
    }
}
