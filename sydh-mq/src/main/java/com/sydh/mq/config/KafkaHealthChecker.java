package com.sydh.mq.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.admin.AdminClient;
//import org.apache.kafka.clients.admin.ListTopicsResult;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.kafka.core.KafkaAdmin;
//
//import java.util.concurrent.TimeUnit;
//
///**
// * Kafka健康检查器
// * 在应用启动时检查Kafka连接状态并输出提示信息
// *
// * @author IoT Team
// */
//@Slf4j
//@Configuration
//@ConditionalOnProperty(name = "iot.kafka.enabled", havingValue = "true")
//@Order(9)
//public class KafkaHealthChecker implements ApplicationRunner {
//
//    @Autowired
//    private KafkaAdmin kafkaAdmin;
//
//    @Value("${iot.message.mode}")
//    private String messageMode;
//
//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        log.info("\n=== Kafka健康检查器启动 ===");
//
//        if ("kafka".equals(messageMode)) {
//            checkKafkaConnection();
//        } else {
//            log.info("\uD83D\uDD04 消息模式: {} (队列模式)", messageMode);
//            log.info("💡 提示: 设置 iot.message.mode=kafka 以启用Kafka模式");
//        }
//
//        log.info("=== Kafka健康检查器完成 ===\n");
//    }
//
//    /**
//     * 检查Kafka连接状态
//     */
//    private void checkKafkaConnection() {
//        log.info("🔍 正在检查Kafka连接状态...");
//        log.info("\uD83D\uDCE1 Kafka服务器: {}", bootstrapServers);
//
//        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
//            // 尝试获取Topic列表，验证连接
//            ListTopicsResult topicsResult = adminClient.listTopics();
//            int topicCount = topicsResult.names().get(10, TimeUnit.SECONDS).size();
//
//            // 连接成功
//            log.info("✅ Kafka连接成功！");
//            log.info("\uD83C\uDFAF 消息模式: {} (Kafka模式)", messageMode);
//            log.info("\uD83D\uDCCA 发现 {} 个Topic", topicCount);
//            log.info("🚀 Kafka消息分发系统已就绪");
//
//        } catch (Exception e) {
//            log.error("❌ Kafka连接失败: {}", e.getMessage());
//            log.warn("⚠️  将使用队列模式作为后备方案");
//        }
//    }
//}