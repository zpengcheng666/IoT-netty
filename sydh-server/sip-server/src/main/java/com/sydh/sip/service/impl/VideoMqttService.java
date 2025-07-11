package com.sydh.sip.service.impl;

import com.alibaba.fastjson2.JSON;
import com.sydh.common.enums.TopicType;
import com.sydh.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.sydh.common.utils.gateway.mq.TopicsUtils;
import com.sydh.iot.domain.Device;
import com.sydh.iot.service.IDeviceService;
import com.sydh.mqttclient.PubMqttClient;
import com.sydh.sip.domain.SipConfig;
import com.sydh.sip.domain.SipDevice;
import com.sydh.sip.domain.SipDeviceChannel;
import com.sydh.sip.model.RecordList;
import com.sydh.sip.model.SipDeviceSummary;
import com.sydh.sip.server.msg.Alarm;
import com.sydh.sip.service.IMqttService;
import com.sydh.sip.service.ISipConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class VideoMqttService implements IMqttService {
    @Resource
    private PubMqttClient mqttClient;

    @Autowired
    private ISipConfigService sipConfigService;

    @Autowired
    private IDeviceService deviceService;

    @Resource
    private TopicsUtils topicsUtils;

    @Override
    public void publishInfo(SipDevice device) {
        SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
        if (null != sipConfig) {
            Device iotdevice = deviceService.selectDeviceBySerialNumber(device.getDeviceSipId());
            if (iotdevice == null ) {
                iotdevice = new Device();
                iotdevice.setSerialNumber(device.getDeviceSipId());
                iotdevice.setDeviceName(device.getDeviceName());
            }
            iotdevice.setProductId(sipConfig.getProductId());
            iotdevice.setProductName(device.getModel());
            iotdevice.setRssi(0);
            iotdevice.setStatus(3);
            iotdevice.setFirmwareVersion(BigDecimal.valueOf(1.0));
            iotdevice.setNetworkIp(device.getIp());
            SipDeviceSummary deviceSummary = new SipDeviceSummary(device);
            iotdevice.setSummary(JSON.toJSONString(deviceSummary));
            deviceService.updateDevice(iotdevice);
            Long productId = sipConfig.getProductId();
            if (null != productId && productId != -1L && productId != 0L) {
                String topic = topicsUtils.buildTopic(productId, device.getDeviceSipId(), TopicType.DEV_INFO_POST);
                mqttClient.publish(1, false, topic, JSON.toJSONString(iotdevice));
            }
        }
    }

    @Override
    public void publishStatus(SipDevice device, int deviceStatus) {
        SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
        if (null != sipConfig) {
            Long productId = sipConfig.getProductId();
            if (null != productId && productId != -1L && productId != 0L) {
                int isShadow = 0;
                int rssi = 0;
                String message = "{\"status\":" + deviceStatus + ",\"isShadow\":" + isShadow + ",\"rssi\":" + rssi + "}";
                String topic = topicsUtils.buildTopic(sipConfig.getProductId(), device.getDeviceSipId(), TopicType.STATUS_POST);
                mqttClient.publish(1, false, topic, message);
            }
        }
    }

    @Override
    public void publishEvent(Alarm alarm) {
        Alarm.Info info;
        ThingsModelSimpleItem item;
        SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(alarm.getDeviceId());
        if (null != sipConfig) {
            List<ThingsModelSimpleItem> events = new ArrayList<>();
            switch (alarm.getAlarmMethod()){
                case telAlarm:
                    item = new ThingsModelSimpleItem("telAlarm","1","");
                    events.add(item);
                    break;
                case devAlarm:
                    item = new ThingsModelSimpleItem("devAlarm","1","");
                    info = alarm.getInfo();
                    if(info != null && info.getAlarmType() != null){
                        item.setValue(info.getAlarmType());
                    }
                    events.add(item);
                    break;
                case smsAlarm:
                    item = new ThingsModelSimpleItem("smsAlarm","1","");
                    events.add(item);
                    break;
                case gpsAlarm:
                    item = new ThingsModelSimpleItem("gpsAlarm","1","");
                    events.add(item);
                    break;
                case videoAlarm:
                    item = new ThingsModelSimpleItem("videoAlarm","1","");
                    info = alarm.getInfo();
                    if(info != null && info.getAlarmType() != null){
                        item.setValue(info.getAlarmType());
                    }
                    events.add(item);
                    break;
                case devErrorAlarm:
                    item = new ThingsModelSimpleItem("devErrorAlarm","1","");
                    info = alarm.getInfo();
                    if(info != null && info.getAlarmType() != null){
                        item.setValue(info.getAlarmType());
                    }
                    events.add(item);
                    break;
                case other:
                    item = new ThingsModelSimpleItem("otherAlarm","1","");
                    events.add(item);
                    break;
            }
            Long productId = sipConfig.getProductId();
            if (null != productId && productId != -1L && productId != 0L) {
                String topic = topicsUtils.buildTopic(sipConfig.getProductId(), alarm.getDeviceId(), TopicType.DEV_EVENT_POST);
                mqttClient.publish(1, false, topic, JSON.toJSONString(events));
            }
        }
    }

    public void publishChannelsProperty(String DeviceSipId, List<SipDeviceChannel> channels) {
        SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(DeviceSipId);
        if (null != sipConfig) {
            List<ThingsModelSimpleItem> thingsList = new ArrayList<>();
            ThingsModelSimpleItem item = new ThingsModelSimpleItem();
            item.setId("channel");
            item.setValue(JSON.toJSONString(channels));
            thingsList.add(item);
            publishProperty(sipConfig.getProductId(), DeviceSipId, thingsList, 0);
        }
    }

    @Override
    public void publishRecordsProperty(String DeviceSipId, RecordList recordList) {
        SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(DeviceSipId);
        if (null != sipConfig) {
            List<ThingsModelSimpleItem> thingsList = new ArrayList<>();
            ThingsModelSimpleItem item = new ThingsModelSimpleItem();
            item.setId("recordList");
            item.setValue(JSON.toJSONString(recordList));
            thingsList.add(item);
            publishProperty(sipConfig.getProductId(), DeviceSipId, thingsList, 0);
        }
    }

    public void publishProperty(Long productId, String deviceNum, List<ThingsModelSimpleItem> thingsList, int delay) {
        String pre = "";
        if (delay > 0) {
            pre = "$delayed/" + String.valueOf(delay) + "/";
        }
        if (null != productId && productId != -1L && productId != 0L) {
            String topic = topicsUtils.buildTopic(productId, deviceNum, TopicType.DEV_PROPERTY_POST);
            if (thingsList == null) {
                mqttClient.publish(1, false, topic, "");
            } else {
                mqttClient.publish(1, false, topic, JSON.toJSONString(thingsList));
            }
        }
    }


}
