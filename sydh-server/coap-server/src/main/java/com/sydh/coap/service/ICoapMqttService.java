package com.sydh.coap.service;

import com.sydh.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.sydh.iot.domain.Device;

import java.util.List;

public interface ICoapMqttService {
    void publishInfo(Device device);

    void publishStatus(Device device, int deviceStatus);

    void publishFunction(Device device, List<ThingsModelSimpleItem> thingsList);

    void publishEvent(Device device, List<ThingsModelSimpleItem> thingsList);

    void publishProperty(Device device, List<ThingsModelSimpleItem> thingsList, int delay);

    void publishMonitor(Device device, List<ThingsModelSimpleItem> thingsList);
}
