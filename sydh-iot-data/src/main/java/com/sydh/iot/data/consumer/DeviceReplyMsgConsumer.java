package com.sydh.iot.data.consumer;

import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.enums.TopicType;
import com.sydh.common.extend.core.domin.mq.DeviceReportBo;
import com.sydh.iot.data.service.IDeviceReportMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 设备消息回复
 *
 * @author gsb
 * @date 2022/10/10 11:12
 */
@Component
@Slf4j
public class DeviceReplyMsgConsumer {


    @Resource
    private IDeviceReportMessageService deviceReportMessageService;


    /*设备回调消息，统一用上报model*/
    @Async(SYDHConstant.TASK.DEVICE_REPLY_MESSAGE_TASK)
    public void consume(DeviceReportBo bo) {
        try {
            String topicName = bo.getTopicName();

            if (topicName.endsWith(TopicType.SERVICE_INVOKE_REPLY.getTopicSuffix())) {
                //普通设备回复消息
                deviceReportMessageService.parseReplyMsg(bo);
            } else if (topicName.endsWith(SYDHConstant.MQTT.OTA_REPLY)) {
                //OTA升级的回复消息
                deviceReportMessageService.parseOTAUpdateReply(bo);
            }
        } catch (Exception e) {
            log.error("=>设备回复消息消费异常", e);
        }
    }
}
