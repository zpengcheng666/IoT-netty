package com.sydh.coap.service.impl;

import com.alibaba.fastjson2.JSON;
import com.sydh.coap.service.ICoapMqttService;
import com.sydh.common.enums.TopicType;
import com.sydh.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.sydh.common.utils.gateway.mq.TopicsUtils;
import com.sydh.iot.domain.Device;
import com.sydh.mqttclient.PubMqttClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class CoapMqttServiceImpl implements ICoapMqttService {
    @Resource
    private PubMqttClient mqttClient;
    @Resource
    private TopicsUtils topicsUtils;

    @Override
    public void publishInfo(Device device) {
        device.setRssi(0);
        device.setStatus(3);
        device.setFirmwareVersion(BigDecimal.valueOf(1.0));
        String topic = topicsUtils.buildTopic(device.getProductId(), device.getSerialNumber(), TopicType.DEV_INFO_POST);
        mqttClient.publish(1, false, topic, JSON.toJSONString(device));

    }

    @Override
    public void publishStatus(Device device, int deviceStatus) {

    }

    public void publishFunction(Device device, List<ThingsModelSimpleItem> thingsList) {
        String topic = topicsUtils.buildTopic(device.getProductId(), device.getSerialNumber(), TopicType.FUNCTION_POST);
        if (thingsList == null) {
            mqttClient.publish(1, false, topic, "");
        } else {
            mqttClient.publish(1, false, topic, JSON.toJSONString(thingsList));
        }
    }

    @Override
    public void publishEvent(Device device, List<ThingsModelSimpleItem> thingsList) {
        String topic = topicsUtils.buildTopic(device.getProductId(), device.getSerialNumber(), TopicType.DEV_EVENT_POST);
        if (thingsList == null) {
            mqttClient.publish(1, false, topic, "");
        } else {
            mqttClient.publish(1, false, topic, JSON.toJSONString(thingsList));
        }
    }

    @Override
    public void publishProperty(Device device, List<ThingsModelSimpleItem> thingsList, int delay) {
        String pre = "";
        if (delay > 0) {
            pre = "$delayed/" + String.valueOf(delay) + "/";
        }
        String topic = topicsUtils.buildTopic(device.getProductId(), device.getSerialNumber(), TopicType.DEV_PROPERTY_POST);
        if (thingsList == null) {
            mqttClient.publish(1, false, topic, "");
        } else {
            mqttClient.publish(1, false, topic, JSON.toJSONString(thingsList));
        }
    }

    @Override
    public void publishMonitor(Device device, List<ThingsModelSimpleItem> thingsList) {
        String topic = topicsUtils.buildTopic(device.getProductId(), device.getSerialNumber(), TopicType.DEV_PROPERTY_POST);
        if (thingsList == null) {
            mqttClient.publish(1, false, topic, "");
        } else {
            mqttClient.publish(1, false, topic, JSON.toJSONString(thingsList));
        }
    }
}
