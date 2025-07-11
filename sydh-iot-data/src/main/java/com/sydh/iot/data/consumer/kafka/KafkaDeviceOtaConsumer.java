package com.sydh.iot.data.consumer.kafka;

import com.alibaba.fastjson2.JSON;
import com.sydh.common.extend.core.domin.mq.ota.OtaUpgradeBo;
import com.sydh.iot.data.service.IMqttMessagePublish;
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
 * Kafka设备OTA消费者
 * 处理OTA升级相关消息
 * 
 * @author IoT Team
 */
@Slf4j
@Component
public class KafkaDeviceOtaConsumer {

    @Autowired
    private IMqttMessagePublish mqttMessagePublish;

    /**
     * 消费设备OTA Topic的消息
     * 处理OTA升级相关消息
     */
    @KafkaListener(
        topics = "${iot.kafka.topics.device-ota}",
        groupId = "device-ota-processor"
    )
    public void handleDeviceOta(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
            ConsumerRecord<String, String> record,
            Acknowledgment acknowledgment) {
        
        try {
            log.debug("收到设备OTA消息 - Topic: {}, Key: {}, Partition: {}, Offset: {}", 
                topic, key, partition, record.offset());
            
            // 解析消息
            OtaUpgradeBo otaUpgrade = JSON.parseObject(message, OtaUpgradeBo.class);
            
            // 调用现有的OTA处理逻辑
            mqttMessagePublish.upGradeOTA(otaUpgrade);
            
            // 手动提交偏移量
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
            
        } catch (Exception e) {
            log.error("处理设备OTA消息失败 - Key: {}, Message: {}, Error: {}", 
                key, message, e.getMessage(), e);
            
            // 对于OTA消息，失败时可能需要特殊处理
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }
} 