package com.sydh.mqttclient;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(
        @Value("${rest-template.max-connections}") int maxConn,
        @Value("${rest-template.max-per-route}") int maxPerRoute,
        @Value("${rest-template.connection-timeout}") int connTimeout,
        @Value("${rest-template.read-timeout}") int readTimeout) {

        HttpClient httpClient = HttpClientBuilder.create()
            .setMaxConnTotal(maxConn)
            .setMaxConnPerRoute(maxPerRoute)
            .setConnectionTimeToLive(30, TimeUnit.SECONDS)
            .build();

        HttpComponentsClientHttpRequestFactory factory =
            new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(connTimeout);
        factory.setReadTimeout(readTimeout);

        return new RestTemplate(factory);
    }
}
