package com.sydh.iot.data.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.listener.ContainerProperties;
//
//import javax.annotation.PostConstruct;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Kafka消费者配置类
// * 配置消费者工厂和监听器容器
// *
// * @author IoT Team
// */
//@Slf4j
//@Configuration
//@EnableKafka
//@ConditionalOnProperty(name = "iot.kafka.enabled", havingValue = "true")
//public class KafkaConsumerConfig {
//
//    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
//    private String bootstrapServers;
//
//    @Value("${spring.kafka.consumer.group-id:iot-device-consumer}")
//    private String groupId;
//
//    @Value("${spring.kafka.listener.concurrency:3}")
//    private int concurrency;
//
//    @PostConstruct
//    public void init() {
//        log.info("🎧 初始化Kafka消费者配置");
//        log.info("   📡 服务器: {}", bootstrapServers);
//        log.info("   👥 消费者组: {}", groupId);
//        log.info("   🔀 并发数: {}", concurrency);
//    }
//
//    /**
//     * 消费者配置
//     */
//    @Bean
//    public Map<String, Object> consumerConfigs() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
//        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
//        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);
//        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024);
//        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);
//
//        log.info("⚙️  Kafka消费者配置完成");
//        return props;
//    }
//
//    /**
//     * 消费者工厂
//     */
//    @Bean
//    public ConsumerFactory<String, String> consumerFactory() {
//        log.info("🏭 创建Kafka消费者工厂");
//        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
//    }
//
//    /**
//     * Kafka监听器容器工厂
//     */
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, String> factory =
//            new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory());
//        factory.setConcurrency(concurrency);
//
//        // 手动提交模式
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
//        factory.getContainerProperties().setPollTimeout(3000);
//
//        log.info("🎧 Kafka监听器容器工厂配置完成");
//        log.info("✅ Kafka消费者系统已就绪");
//
//        return factory;
//    }
//}