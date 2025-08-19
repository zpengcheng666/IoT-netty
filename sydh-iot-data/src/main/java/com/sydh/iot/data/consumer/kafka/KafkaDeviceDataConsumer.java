package com.sydh.iot.data.consumer.kafka;

import com.alibaba.fastjson2.JSON;
import com.sydh.common.extend.core.domin.mq.DeviceReportBo;
import com.sydh.iot.data.service.IDeviceReportMessageService;
import com.sydh.iot.data.service.impl.DeviceOtherMsgHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
//
///**
// * Kafka设备数据消费者
// * 处理设备上报、回复等数据消息
// *
// * @author IoT Team
// */
//@Slf4j
//@Component
//public class KafkaDeviceDataConsumer {
//
//    @Autowired
//    private IDeviceReportMessageService deviceReportMessageService;
//
//    @Autowired
//    private DeviceOtherMsgHandler deviceOtherMsgHandler;
//
//    /**
//     * 消费设备数据Topic的消息
//     * 包括设备上报、回复、其他消息等
//     */
//    @KafkaListener(
//        topics = "${iot.kafka.topics.device-data}",
//        groupId = "device-data-processor",
//        containerFactory = "kafkaListenerContainerFactory"
//    )
//    public void handleDeviceData(
//            @Payload String message,
//            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
//            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
//            @Header(value = KafkaHeaders.RECEIVED_MESSAGE_KEY, required = false) String key,
//            ConsumerRecord<String, String> record,
//            Acknowledgment acknowledgment) {
//
//        try {
//            log.debug("收到设备数据消息 - Topic: {}, Key: {}, Partition: {}, Offset: {}",
//                topic, key, partition, record.offset());
//
//            // 解析消息
//            DeviceReportBo deviceReport = JSON.parseObject(message, DeviceReportBo.class);
//
//            // 根据消息key中的类型标识路由到不同的处理器
//            String messageType = extractMessageType(key);
//
//            switch (messageType) {
//                case "report":
//                    // 设备主动上报数据
//                    deviceReportMessageService.parseReportMsg(deviceReport);
//                    break;
//                case "reply":
//                    // 设备回复消息
//                    deviceReportMessageService.parseReplyMsg(deviceReport);
//                    break;
//                case "other":
//                    // 其他类型消息（如事件、服务调用等）
//                    deviceOtherMsgHandler.messageHandler(deviceReport);
//                    break;
//                default:
//                    log.warn("未知的消息类型: {}, 使用默认处理器", messageType);
//                    deviceReportMessageService.parseReportMsg(deviceReport);
//                    break;
//            }
//
//            // 手动提交偏移量
//            acknowledgment.acknowledge();
//
//        } catch (Exception e) {
//            log.error("处理设备数据消息失败 - Key: {}, Message: {}, Error: {}",
//                key, message, e.getMessage(), e);
//
//            // 根据业务需求决定是否提交偏移量
//            // 对于解析失败的消息，可以选择跳过或重试
//            acknowledgment.acknowledge();
//        }
//    }
//
//    /**
//     * 从消息Key中提取消息类型
//     * Key格式: deviceSN:messageType:timestamp
//     */
//    private String extractMessageType(String key) {
//        if (key == null || key.trim().isEmpty()) {
//            log.warn("消息Key为空，使用默认处理器");
//            return "unknown";
//        }
//
//        if (key.contains(":")) {
//            String[] parts = key.split(":");
//            if (parts.length >= 2) {
//                return parts[1];
//            }
//        }
//
//        log.warn("消息Key格式不正确: {}, 使用默认处理器", key);
//        return "unknown";
//    }
//}