package com.sydh.iot.data.consumer.kafka;

import com.alibaba.fastjson2.JSON;
import com.sydh.common.extend.core.domin.mq.DeviceTestReportBo;
import com.sydh.iot.data.consumer.DeviceTestConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka设备事件消费者
 * 处理设备调试、告警事件等消息
 * 
 * @author IoT Team
 */
@Slf4j
@Component
public class KafkaDeviceEventConsumer {

    @Autowired
    private DeviceTestConsumer deviceTestConsumer;

    /**
     * 消费设备事件Topic的消息
     * 包括设备调试、告警等事件
     */
    @KafkaListener(
        topics = "${iot.kafka.topics.device-events}",
        groupId = "device-event-processor"
    )
    public void handleDeviceEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
            ConsumerRecord<String, String> record,
            Acknowledgment acknowledgment) {
        
        try {
            log.debug("收到设备事件消息 - Topic: {}, Key: {}, Partition: {}, Offset: {}", 
                topic, key, partition, record.offset());
            
            // 根据消息key中的类型标识路由到不同的处理器
            String messageType = extractMessageType(key);
            
            switch (messageType) {
                case "test":
                    // 设备调试消息
                    DeviceTestReportBo testReport = JSON.parseObject(message, DeviceTestReportBo.class);
                    deviceTestConsumer.consume(testReport);
                    break;
                default:
                    log.warn("未知的事件类型: {}, 跳过处理", messageType);
                    break;
            }
            
            // 手动提交偏移量
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
            
        } catch (Exception e) {
            log.error("处理设备事件消息失败 - Key: {}, Message: {}, Error: {}", 
                key, message, e.getMessage(), e);
            
            // 对于事件消息，通常需要保证处理
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }

    /**
     * 从消息Key中提取消息类型
     * Key格式: deviceSN:messageType:timestamp
     */
    private String extractMessageType(String key) {
        if (key != null && key.contains(":")) {
            String[] parts = key.split(":");
            if (parts.length >= 2) {
                return parts[1];
            }
        }
        return "unknown";
    }
} 