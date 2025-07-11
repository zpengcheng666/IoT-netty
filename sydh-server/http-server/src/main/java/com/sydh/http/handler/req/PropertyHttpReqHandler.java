package com.sydh.http.handler.req;

import com.alibaba.fastjson2.JSON;
import com.sydh.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.sydh.http.handler.IHttpReqHandler;
import com.sydh.http.server.HttpListener;
import com.sydh.http.service.IHttpMqttService;
import com.sydh.iot.domain.Device;
import com.sydh.iot.service.IDeviceService;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class PropertyHttpReqHandler implements InitializingBean, IHttpReqHandler {
    @Autowired
    private HttpListener httpListener;

    @Autowired
    private IHttpMqttService mqttService;

    @Autowired
    private IDeviceService deviceService;

    @Override
    public void afterPropertiesSet() throws Exception {
        String uri = "/property/post";
        httpListener.addRequestProcessor(uri, this);
    }

    @Override
    public void processMsg(FullHttpRequest req, HttpSession session) {
        String serialNumber = (String) session.getAttribute("SerialNumber");
        Device device = deviceService.selectDeviceBySerialNumber(serialNumber);
        if (device != null) {
            List<ThingsModelSimpleItem> thingsModelSimpleItems = JSON.parseArray(req.content().toString(StandardCharsets.UTF_8), ThingsModelSimpleItem.class);
            mqttService.publishProperty(device, thingsModelSimpleItems, 0);
        }
    }
}
