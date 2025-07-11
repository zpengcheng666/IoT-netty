package com.sydh.sip.service.impl;

import com.sydh.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.sydh.sip.enums.FunctionType;
import com.sydh.sip.service.IGatewayService;
import com.sydh.sip.service.IPlayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Slf4j
@Service
public class GatewayServiceImpl implements IGatewayService {
    @Autowired
    private IPlayService playService;

    @Override
    public void sendFunction(String deviceID, List<ThingsModelSimpleItem> functinos) {

    }

    @Override
    public void sendFunction(String deviceID, String identifier, String value) {
        FunctionType Type = FunctionType.fromType(identifier);
        switch (Objects.requireNonNull(Type)) {
            case VIDEOPUSH:
                playService.play(deviceID, value,false);
                break;
            case AUDIOBROADCAST:
                break;
            case OTHER:
                break;
        }
    }


}
