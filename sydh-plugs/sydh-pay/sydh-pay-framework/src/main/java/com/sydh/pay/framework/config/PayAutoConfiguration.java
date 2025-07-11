package com.sydh.pay.framework.config;

import com.sydh.pay.framework.client.PayClientFactory;
import com.sydh.pay.framework.client.impl.PayClientFactoryImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 支付配置类
 *
 * @author sydh
 */
@AutoConfiguration
public class PayAutoConfiguration {

    @Bean
    public PayClientFactory payClientFactory() {
        return new PayClientFactoryImpl();
    }

}
