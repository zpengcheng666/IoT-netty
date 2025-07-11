package com.sydh.http.handler.req;

import com.sydh.common.enums.DeviceStatus;
import com.sydh.common.utils.DateUtils;
import com.sydh.http.handler.IHttpReqHandler;
import com.sydh.http.server.HttpListener;
import com.sydh.iot.domain.Device;
import com.sydh.iot.service.IDeviceService;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Slf4j
@Component
public class KeepaliveHttpReqHandler implements InitializingBean, IHttpReqHandler {
    @Autowired
    private HttpListener httpListener;

    @Autowired
    private IDeviceService deviceService;

    @Override
    public void processMsg(FullHttpRequest req, HttpSession session) {
        String serialNumber = (String) session.getAttribute("SerialNumber");
        Device device = deviceService.selectDeviceBySerialNumber(serialNumber);
        if (device != null) {
            if (device.getActiveTime() == null && device.getStatus() == DeviceStatus.ONLINE.getType()) {
                device.setActiveTime(DateUtils.getNowDate());
            }
            device.setStatus(DeviceStatus.ONLINE.getType());
            deviceService.updateDevice(device);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String uri = "/keepalive";
        httpListener.addRequestProcessor(uri, this);
    }
}
