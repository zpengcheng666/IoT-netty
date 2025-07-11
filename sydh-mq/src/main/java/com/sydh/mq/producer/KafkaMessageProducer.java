package com.sydh.mq.producer;

import com.alibaba.fastjson2.JSON;
import com.sydh.common.extend.core.domin.mq.DeviceReportBo;
import com.sydh.common.extend.core.domin.mq.DeviceStatusBo;
import com.sydh.common.extend.core.domin.mq.DeviceTestReportBo;
import com.sydh.common.extend.core.domin.mq.MQSendMessageBo;
import com.sydh.common.extend.core.domin.mq.ota.OtaUpgradeBo;
import com.sydh.iot.model.modbus.ModbusPollJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * Kafka消息生产者
 * 负责将设备消息发送到相应的Kafka Topic
 * 
 * @author IoT Team
 */
@Slf4j
@Component
public class KafkaMessageProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

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

    /**
     * 发送设备属性获取消息
     * 用于Modbus轮询等场景
     */
    public void sendPropFetch(ModbusPollJob deviceJob) {
        try {
            String message = JSON.toJSONString(deviceJob);
            String key = generateKey(deviceJob.getPollMsg().getSerialNumber(), "prop-fetch");
            
            sendMessage(deviceCommandsTopic, key, message, "设备属性获取");
        } catch (Exception e) {
            log.error("发送设备属性获取消息失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 发送设备服务调用消息
     */
    public void sendFunctionInvoke(MQSendMessageBo bo) {
        try {
            String message = JSON.toJSONString(bo);
            String key = generateKey(bo.getSerialNumber(), "function-invoke");
            
            sendMessage(deviceCommandsTopic, key, message, "设备服务调用");
        } catch (Exception e) {
            log.error("发送设备服务调用消息失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 发送设备上报消息
     */
    public void sendPublishMsg(DeviceReportBo bo) {
        try {
            String message = JSON.toJSONString(bo);
            String key = generateKey(bo.getSerialNumber(), "report");
            
            sendMessage(deviceDataTopic, key, message, "设备数据上报");
        } catch (Exception e) {
            log.error("发送设备上报消息失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 发送设备其他消息
     */
    public void sendOtherMsg(DeviceReportBo bo) {
        try {
            String message = JSON.toJSONString(bo);
            String key = generateKey(bo.getSerialNumber(), "other");
            
            sendMessage(deviceDataTopic, key, message, "设备其他消息");
        } catch (Exception e) {
            log.error("发送设备其他消息失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 发送设备状态消息
     */
    public void sendStatusMsg(DeviceStatusBo bo) {
        try {
            String message = JSON.toJSONString(bo);
            String key = generateKey(bo.getSerialNumber(), "status");
            
            sendMessage(deviceStatusTopic, key, message, "设备状态变更");
        } catch (Exception e) {
            log.error("发送设备状态消息失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 发送设备调试消息
     */
    public void sendDeviceTestMsg(DeviceTestReportBo bo) {
        try {
            String message = JSON.toJSONString(bo);
            String key = generateKey(bo.getSerialNumber(), "test");
            
            sendMessage(deviceEventsTopic, key, message, "设备调试消息");
        } catch (Exception e) {
            log.error("发送设备调试消息失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 发送OTA升级消息
     */
    public void sendOtaUpgradeMsg(OtaUpgradeBo bo) {
        try {
            String message = JSON.toJSONString(bo);
            String key = generateKey(bo.getSerialNumber(), "ota");
            
            sendMessage(deviceOtaTopic, key, message, "OTA升级");
        } catch (Exception e) {
            log.error("发送OTA升级消息失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 发送设备回复消息
     */
    public void sendDeviceReplyMsg(DeviceReportBo bo) {
        try {
            String message = JSON.toJSONString(bo);
            String key = generateKey(bo.getSerialNumber(), "reply");
            
            sendMessage(deviceDataTopic, key, message, "设备回复消息");
        } catch (Exception e) {
            log.error("发送设备回复消息失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 通用消息发送方法
     */
    private void sendMessage(String topic, String key, String message, String description) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, message);
        
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.debug("{}消息发送成功 - Topic: {}, Key: {}, Partition: {}, Offset: {}", 
                    description, topic, key, 
                    result.getRecordMetadata().partition(), 
                    result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("{}消息发送失败 - Topic: {}, Key: {}, Error: {}", 
                    description, topic, key, ex.getMessage(), ex);
            }
        });
    }

    /**
     * 生成消息Key，用于确保同一设备的消息发送到同一分区
     * 格式: deviceSN:messageType:timestamp
     */
    private String generateKey(String serialNumber, String messageType) {
        return String.format("%s:%s:%d", serialNumber, messageType, System.currentTimeMillis());
    }
} 