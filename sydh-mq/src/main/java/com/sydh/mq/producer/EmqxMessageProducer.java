package com.sydh.mq.producer;

import com.sydh.common.extend.core.domin.mq.DeviceReportBo;
import com.sydh.mqttclient.IEmqxMessageProducer;
import org.springframework.stereotype.Component;
import com.sydh.common.constant.SYDHConstant;

/**
 * @author bill
 */
@Component
public class EmqxMessageProducer implements IEmqxMessageProducer {


    @Override
    public void sendEmqxMessage(String topicName, DeviceReportBo deviceReportBo) {
        if (topicName.contains("property/post")){
            MessageProducer.sendPublishMsg(deviceReportBo);
        } else if (topicName.contains(SYDHConstant.MQTT.OTA_REPLY)) {
            MessageProducer.sendDeviceReplyMsg(deviceReportBo);
        } else {
            MessageProducer.sendOtherMsg(deviceReportBo);
        }
    }
}
