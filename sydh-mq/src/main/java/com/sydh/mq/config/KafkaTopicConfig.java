package com.sydh.mq.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.config.TopicBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Topic配置类
 * 用于自动创建IoT平台所需的Topic
 * 
 * @author IoT Team
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "iot.kafka.enabled", havingValue = "true")
public class KafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${iot.kafka.topics.device-data}")
    private String deviceDataTopic;

    @Value("${iot.kafka.topics.device-status}")
    private String deviceStatusTopic;

    @Value("${iot.kafka.topics.device-commands}")
    private String deviceCommandsTopic;

    @Value("${iot.kafka.topics.device-events}")
    private String deviceEventsTopic;

    @Value("${iot.kafka.topics.device-ota}")
    private String deviceOtaTopic;

    @Value("${iot.kafka.partitions:6}")
    private int partitions;

    @Value("${iot.kafka.replication-factor:1}")
    private short replicationFactor;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    /**
     * 设备数据上报Topic
     * 用于处理设备实时数据、属性上报等
     */
    @Bean
    public NewTopic deviceDataTopic() {
        log.info("📋 创建Topic: iot-device-data (分区: {}, 副本: {})", partitions, replicationFactor);
        return TopicBuilder.name(deviceDataTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .build();
    }

    /**
     * 设备状态Topic  
     * 用于处理设备上下线、连接状态等
     */
    @Bean
    public NewTopic deviceStatusTopic() {
        log.info("📋 创建Topic: iot-device-status (分区: {}, 副本: {})", partitions, replicationFactor);
        return TopicBuilder.name(deviceStatusTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .build();
    }

    /**
     * 设备指令Topic
     * 用于处理下发给设备的指令、控制命令等
     */
    @Bean
    public NewTopic deviceCommandsTopic() {
        log.info("📋 创建Topic: iot-device-commands (分区: {}, 副本: {})", partitions, replicationFactor);
        return TopicBuilder.name(deviceCommandsTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .build();
    }

    /**
     * 设备事件Topic
     * 用于处理设备告警、异常事件等
     */
    @Bean
    public NewTopic deviceEventsTopic() {
        log.info("📋 创建Topic: iot-device-events (分区: {}, 副本: {})", partitions, replicationFactor);
        return TopicBuilder.name(deviceEventsTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .build();
    }

    /**
     * 设备OTA升级Topic
     * 用于处理OTA升级相关消息
     */
    @Bean
    public NewTopic deviceOtaTopic() {
        log.info("📋 创建Topic: iot-device-ota (分区: {}, 副本: {})", partitions, replicationFactor);
        return TopicBuilder.name(deviceOtaTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .build();
    }

    /**
     * Kafka Admin配置
     * 用于Topic管理和健康检查
     */
    @Bean
    public KafkaAdmin.NewTopics allTopics() {
        log.info("🎯 IoT Kafka Topics配置完成");
        log.info("   - 设备数据: iot-device-data");
        log.info("   - 设备状态: iot-device-status");
        log.info("   - 设备指令: iot-device-commands");
        log.info("   - 设备事件: iot-device-events");
        log.info("   - OTA升级: iot-device-ota");
        return new KafkaAdmin.NewTopics(
                deviceDataTopic(),
                deviceStatusTopic(),
                deviceCommandsTopic(),
                deviceEventsTopic(),
                deviceOtaTopic()
        );
    }
} 