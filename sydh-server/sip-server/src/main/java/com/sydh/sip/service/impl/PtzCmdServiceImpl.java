package com.sydh.sip.service.impl;

import com.sydh.sip.domain.SipDevice;
import com.sydh.sip.enums.Direct;
import com.sydh.sip.server.MessageInvoker;
import com.sydh.sip.server.msg.DeviceControl;
import com.sydh.sip.service.IPtzCmdService;
import com.sydh.sip.service.ISipDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
public class PtzCmdServiceImpl implements IPtzCmdService {

    @Autowired
    private MessageInvoker messageInvoker;

    @Autowired
    private ISipDeviceService sipDeviceService;

    public boolean directPtzCmd(String deviceId, String channelId, Direct direct, Integer speed) {
        Map<Direct, Integer> directAndSpeed = new HashMap<>();
        directAndSpeed.put(direct, speed);
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        if (dev != null) {
            DeviceControl control = new DeviceControl();
            control.setPtzDirect(directAndSpeed);
            control.setDeviceId(channelId);
            messageInvoker.deviceControl(dev, control);
            return true;
        }
        return false;
    }

}
