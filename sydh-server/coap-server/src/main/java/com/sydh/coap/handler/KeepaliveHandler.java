package com.sydh.coap.handler;

import com.sydh.coap.model.*;
import com.sydh.coap.model.options.ContentFormat;
import com.sydh.coap.service.ICoapSessionService;
import com.sydh.common.enums.DeviceStatus;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.spring.SpringUtils;
import com.sydh.iot.domain.Device;
import com.sydh.iot.service.IDeviceService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class KeepaliveHandler extends AbstractResourceHandler {
    private final String path;

    private static IDeviceService deviceService = SpringUtils.getBean(IDeviceService.class);

    private static ICoapSessionService coapSessionService = SpringUtils.getBean(ICoapSessionService.class);

    public KeepaliveHandler(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public CoapResponse handle(CoapMessage request) {
        if (request.getMessageCode() == MessageCode.POST) {
            CoapResponse coapResponse = new CoapResponse(request.getMessageType(),
                    MessageCode.CONTENT_205);

            String query = ((CoapRequest) request).getUriQuery();
            Map<String, String> queryParams = this.parseQueryParams(query);
            // 现在你可以使用 queryParams 这个 Map
            log.info("Query parameters: {}", queryParams);
            CoapSession session = coapSessionService.getSession(queryParams.get("token"));
            if (session != null) {
                String serialNumber = session.getSerialNumber();
                Device device = deviceService.selectDeviceBySerialNumber(serialNumber);
                if (device != null) {
                    if (device.getActiveTime() == null && device.getStatus() == DeviceStatus.ONLINE.getType()) {
                        device.setActiveTime(DateUtils.getNowDate());
                    }
                    device.setStatus(DeviceStatus.ONLINE.getType());
                    deviceService.updateDevice(device);
                }
            } else {
                coapResponse = new CoapResponse(request.getMessageType(),
                        MessageCode.UNAUTHORIZED_401);
                coapResponse.setContent("token 不存在".getBytes(CoapMessage.CHARSET), ContentFormat.TEXT_PLAIN_UTF8);
            }
            coapResponse.setSender(request.getSender());
            coapResponse.setMessageID(request.getMessageID());
            coapResponse.setToken(request.getToken());
            return coapResponse;
        } else {
            CoapResponse coapResponse = new CoapResponse(request.getMessageType(),
                    MessageCode.METHOD_NOT_ALLOWED_405);
            String message = "Service does not allow " + request.getMessageCodeName() + " requests.";
            coapResponse.setContent(message.getBytes(CoapMessage.CHARSET), ContentFormat.TEXT_PLAIN_UTF8);
            coapResponse.setSender(request.getSender());
            coapResponse.setMessageID(request.getMessageID());
            coapResponse.setToken(request.getToken());
            return coapResponse;

        }
    }
}
