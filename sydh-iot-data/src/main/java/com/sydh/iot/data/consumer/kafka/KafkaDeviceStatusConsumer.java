package com.sydh.iot.data.consumer.kafka;
//
//import com.alibaba.fastjson2.JSON;
//import com.sydh.common.extend.core.domin.mq.DeviceStatusBo;
//import com.sydh.iot.data.consumer.DeviceStatusConsumer;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.kafka.support.KafkaHeaders;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Component;
//
///**
// * Kafka设备状态消费者
// * 处理设备上下线、连接状态等消息
// *
// * @author IoT Team
// */
//@Slf4j
//@Component
//public class KafkaDeviceStatusConsumer {
//
//    @Autowired
//    private DeviceStatusConsumer deviceStatusConsumer;
//
//    /**
//     * 消费设备状态Topic的消息
//     * 处理设备上下线、连接状态变更等
//     */
//    @KafkaListener(
//        topics = "${iot.kafka.topics.device-status}",
//        groupId = "device-status-processor"
//    )
//    public void handleDeviceStatus(
//            @Payload String message,
//            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
//            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
//            @Header(value = KafkaHeaders.RECEIVED_MESSAGE_KEY, required = false) String key,
//            ConsumerRecord<String, String> record,
//            Acknowledgment acknowledgment) {
//
//        try {
//            log.debug("收到设备状态消息 - Topic: {}, Key: {}, Partition: {}, Offset: {}",
//                topic, key, partition, record.offset());
//
//            // 解析消息
//            DeviceStatusBo deviceStatus = JSON.parseObject(message, DeviceStatusBo.class);
//
//            // 调用现有的设备状态处理逻辑
//            deviceStatusConsumer.consume(deviceStatus);
//
//            // 手动提交偏移量
//            if (acknowledgment != null) {
//                acknowledgment.acknowledge();
//            }
//
//        } catch (Exception e) {
//            log.error("处理设备状态消息失败 - Key: {}, Message: {}, Error: {}",
//                key, message, e.getMessage(), e);
//
//            // 对于状态消息，通常需要保证处理，可以根据需要实现重试逻辑
//            if (acknowledgment != null) {
//                acknowledgment.acknowledge();
//            }
//        }
//    }
//}