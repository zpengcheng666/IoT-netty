package com.fastbee.sip.service;


import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;

import java.util.List;

public interface IGatewayService {
    void sendFunction(String deviceID,List<ThingsModelSimpleItem> functinos);
    void sendFunction(String deviceID,String identifier,String value);
}
