package com.sydh.iot.tsdb.config;

import com.influxdb.client.*;
import lombok.Data;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @Author gx_ma
 * @Date: 2025/03/04/ 11:19
 * @description
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.datasource.dynamic.datasource.influx")
public class InfluxConfig {
    private boolean enabled;
    private String url;
    private String token;
    private String org;
    private String bucket;
    private String measurement;


    /**
     * 创建 InfluxDBClient 客户端
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "spring.datasource.dynamic.datasource.influx",
            name = "enabled",
            havingValue = "true")
    public InfluxDBClient influxDBClient(){
        return InfluxDBClientFactory.create(influxDBClientOptions());
    }

    private InfluxDBClientOptions influxDBClientOptions() {
        return InfluxDBClientOptions.builder()
                .url(this.url)
                .org(this.org)
                .bucket(this.bucket)
                .authenticateToken(this.token.toCharArray())
                .okHttpClient(
                        new OkHttpClient().newBuilder()
                                .connectTimeout(30000, TimeUnit.MILLISECONDS)
                                .readTimeout(60000, TimeUnit.MILLISECONDS)
                                .build().newBuilder()
                )
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.datasource.dynamic.datasource.influx",
            name = "enabled",
            havingValue = "true")
    public WriteApiBlocking writeApi() {
        InfluxDBClient influxDBClient = influxDBClient();
        return influxDBClient.getWriteApiBlocking();
    }
}
