package com.sydh.bootstrap.tcp.config;

import com.sydh.base.core.HandlerMapping;
import com.sydh.base.core.SpringHandlerMapping;
import com.sydh.base.session.SessionListener;
import com.sydh.base.session.SessionManager;
import com.sydh.protocol.WModelManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bill
 */
@Configuration
public class TcpBeanConfig {

    @Bean
    public HandlerMapping handlerMapping(){
        return new SpringHandlerMapping();
    }

    @Bean
    public TcpHandlerInterceptor handlerInterceptor(){
        return new TcpHandlerInterceptor();
    }

    @Bean
    public SessionListener sessionListener(){
        return new TcpSessionListener();
    }

    @Bean
    public SessionManager sessionManager(SessionListener sessionListener){
        return new SessionManager(sessionListener);
    }

    @Bean
    public WModelManager wModelManager(){
        return new WModelManager("com.sydh.modbus");
    }


}
