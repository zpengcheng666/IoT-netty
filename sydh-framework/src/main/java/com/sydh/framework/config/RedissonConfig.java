package com.sydh.framework.config;

import cn.hutool.core.util.ObjectUtil;
import com.sydh.framework.config.properties.RedissonProperties;
import com.sydh.framework.handler.KeyPrefixHandler;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import org.redisson.client.codec.StringCodec;
import org.redisson.codec.CompositeCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RedissonProperties.class})
public class RedissonConfig {
    private static final Logger log = LoggerFactory.getLogger(RedissonConfig.class);

    @Autowired
    private RedissonProperties redissonProperties;

    @Bean
    public RedissonAutoConfigurationCustomizer redissonCustomizer() {
        return config -> {
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
            ObjectMapper om = new ObjectMapper();
            om.registerModule(javaTimeModule);
            om.setTimeZone(TimeZone.getDefault());
            om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

            // 使用更安全的PolymorphicTypeValidator实现
            om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

            TypedJsonJacksonCodec jsonCodec = new TypedJsonJacksonCodec(Object.class, om);
            CompositeCodec codec = new CompositeCodec(StringCodec.INSTANCE, jsonCodec, jsonCodec);
            config.setThreads(this.redissonProperties.getThreads())
                    .setNettyThreads(this.redissonProperties.getNettyThreads())
                    .setUseScriptCache(true)
                    .setCodec(codec);

            RedissonProperties.SingleServerConfig singleServerConfig = this.redissonProperties.getSingleServerConfig();
            if (ObjectUtil.isNotNull(singleServerConfig)) {
                configureSingleServer(config.useSingleServer(), singleServerConfig);
            }

            RedissonProperties.ClusterServersConfig clusterServersConfig = this.redissonProperties.getClusterServersConfig();
            if (ObjectUtil.isNotNull(clusterServersConfig)) {
                configureClusterServers(config.useClusterServers(), clusterServersConfig);
            }

            log.info("初始化 redis 配置完成");

            try {
                logRedissonConfig();
            } catch (Exception e) {
                log.warn("输出 Redisson 配置信息时发生异常", e);
                // 可以考虑在此处抛出运行时异常，以便在配置错误时阻止应用启动
                // throw new RuntimeException("Redisson configuration logging failed", e);
            }
        };
    }

    private void configureSingleServer(SingleServerConfig config, RedissonProperties.SingleServerConfig singleServerConfig) {
        config.setNameMapper(new KeyPrefixHandler(this.redissonProperties.getKeyPrefix()))
                .setTimeout(singleServerConfig.getTimeout())
                .setClientName(singleServerConfig.getClientName())
                .setIdleConnectionTimeout(singleServerConfig.getIdleConnectionTimeout())
                .setSubscriptionConnectionPoolSize(singleServerConfig.getSubscriptionConnectionPoolSize())
                .setConnectionMinimumIdleSize(singleServerConfig.getConnectionMinimumIdleSize())
                .setConnectionPoolSize(singleServerConfig.getConnectionPoolSize());
    }

    private void configureClusterServers(ClusterServersConfig config, RedissonProperties.ClusterServersConfig clusterServersConfig) {
        config.setNameMapper(new KeyPrefixHandler(this.redissonProperties.getKeyPrefix()))
                .setTimeout(clusterServersConfig.getTimeout())
                .setClientName(clusterServersConfig.getClientName())
                .setIdleConnectionTimeout(clusterServersConfig.getIdleConnectionTimeout())
                .setSubscriptionConnectionPoolSize(clusterServersConfig.getSubscriptionConnectionPoolSize())
                .setMasterConnectionMinimumIdleSize(clusterServersConfig.getMasterConnectionMinimumIdleSize())
                .setMasterConnectionPoolSize(clusterServersConfig.getMasterConnectionPoolSize())
                .setSlaveConnectionMinimumIdleSize(clusterServersConfig.getSlaveConnectionMinimumIdleSize())
                .setSlaveConnectionPoolSize(clusterServersConfig.getSlaveConnectionPoolSize())
                .setReadMode(clusterServersConfig.getReadMode())
                .setSubscriptionMode(clusterServersConfig.getSubscriptionMode());
    }

    private void logRedissonConfig() {
        log.info("Redisson 线程数: {}", redissonProperties.getThreads());
        log.info("Redisson Netty线程数: {}", redissonProperties.getNettyThreads());
        log.info("Key前缀: {}", redissonProperties.getKeyPrefix());

        RedissonProperties.SingleServerConfig singleConfig = redissonProperties.getSingleServerConfig();
        if (ObjectUtil.isNotNull(singleConfig)) {
            log.info("========== Redis 单机模式配置 ==========");
            log.info("客户端名称: {}", singleConfig.getClientName());
            log.info("连接超时时间: {}ms", singleConfig.getTimeout());
            log.info("空闲连接超时时间: {}ms", singleConfig.getIdleConnectionTimeout());
            log.info("连接池大小: {}", singleConfig.getConnectionPoolSize());
            log.info("最小空闲连接数: {}", singleConfig.getConnectionMinimumIdleSize());
            log.info("订阅连接池大小: {}", singleConfig.getSubscriptionConnectionPoolSize());
        }

        RedissonProperties.ClusterServersConfig clusterConfig = redissonProperties.getClusterServersConfig();
        if (ObjectUtil.isNotNull(clusterConfig)) {
            log.info("========== Redis 集群模式配置 ==========");
            log.info("客户端名称: {}", clusterConfig.getClientName());
            log.info("连接超时时间: {}ms", clusterConfig.getTimeout());
            log.info("空闲连接超时时间: {}ms", clusterConfig.getIdleConnectionTimeout());
            log.info("主节点连接池大小: {}", clusterConfig.getMasterConnectionPoolSize());
            log.info("主节点最小空闲连接数: {}", clusterConfig.getMasterConnectionMinimumIdleSize());
            log.info("从节点连接池大小: {}", clusterConfig.getSlaveConnectionPoolSize());
            log.info("从节点最小空闲连接数: {}", clusterConfig.getSlaveConnectionMinimumIdleSize());
            log.info("订阅连接池大小: {}", clusterConfig.getSubscriptionConnectionPoolSize());
            log.info("读取模式: {}", clusterConfig.getReadMode());
            log.info("订阅模式: {}", clusterConfig.getSubscriptionMode());
        }
    }
}

