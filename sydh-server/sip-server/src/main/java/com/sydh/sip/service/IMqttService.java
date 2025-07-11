package com.sydh.sip.service;

import com.sydh.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.sydh.sip.domain.SipDevice;
import com.sydh.sip.domain.SipDeviceChannel;
import com.sydh.sip.model.RecordList;
import com.sydh.sip.server.msg.Alarm;

import java.util.List;

public interface IMqttService {
    void publishInfo(SipDevice device);
    void publishStatus(SipDevice device, int deviceStatus);
    void publishEvent(Alarm alarm);
    void publishProperty(Long productId, String deviceNum, List<ThingsModelSimpleItem> thingsList, int delay);
    void publishChannelsProperty(String DeviceSipId, List<SipDeviceChannel> channels);
    void publishRecordsProperty(String DeviceSipId, RecordList recordList);
}
