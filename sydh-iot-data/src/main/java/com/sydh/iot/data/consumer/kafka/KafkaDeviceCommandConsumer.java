package com.sydh.iot.data.consumer.kafka;

import com.alibaba.fastjson2.JSON;
import com.sydh.common.extend.core.domin.mq.MQSendMessageBo;
import com.sydh.iot.data.consumer.DevicePropFetchConsumer;
import com.sydh.iot.data.consumer.FunctionInvokeConsumer;
import com.sydh.iot.model.modbus.ModbusPollJob;
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
 * Kafka设备指令消费者
 * 处理设备指令下发、属性获取等消息
 * 
 * @author IoT Team
 */
@Slf4j
@Component
public class KafkaDeviceCommandConsumer {

    @Autowired
    private DevicePropFetchConsumer devicePropFetchConsumer;

    @Autowired
    private FunctionInvokeConsumer functionInvokeConsumer;

    /**
     * 消费设备指令Topic的消息
     * 包括属性获取、服务调用等指令
     */
    @KafkaListener(
        topics = "${iot.kafka.topics.device-commands}",
        groupId = "device-command-processor"
    )
    public void handleDeviceCommand(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
            ConsumerRecord<String, String> record,
            Acknowledgment acknowledgment) {
        
        try {
            log.debug("收到设备指令消息 - Topic: {}, Key: {}, Partition: {}, Offset: {}", 
                topic, key, partition, record.offset());
            
            // 根据消息key中的类型标识路由到不同的处理器
            String messageType = extractMessageType(key);
            
            switch (messageType) {
                case "prop-fetch":
                    // 设备属性获取
                    ModbusPollJob modbusPollJob = JSON.parseObject(message, ModbusPollJob.class);
                    devicePropFetchConsumer.consume(modbusPollJob);
                    break;
                case "function-invoke":
                    // 设备服务调用
                    MQSendMessageBo functionInvoke = JSON.parseObject(message, MQSendMessageBo.class);
                    functionInvokeConsumer.handler(functionInvoke);
                    break;
                default:
                    log.warn("未知的指令类型: {}, 跳过处理", messageType);
                    break;
            }
            
            // 手动提交偏移量
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
            
        } catch (Exception e) {
            log.error("处理设备指令消息失败 - Key: {}, Message: {}, Error: {}", 
                key, message, e.getMessage(), e);
            
            // 对于指令消息，失败时可能需要重试或告警
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