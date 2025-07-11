package com.sydh.sip.handler.req.message;

import com.sydh.sip.domain.SipDevice;
import com.sydh.sip.handler.IReqHandler;
import com.sydh.sip.handler.req.ReqAbstractHandler;
import com.sydh.sip.server.IGBListener;
import com.sydh.sip.server.SIPSender;
import com.sydh.sip.service.ISipDeviceService;
import com.sydh.sip.util.SipUtil;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class MessageRequestProcessor extends ReqAbstractHandler implements InitializingBean, IReqHandler {
    @Autowired
    private IGBListener sipListener;

    @Autowired
    private ISipDeviceService sipDeviceService;

    @Autowired
    private SIPSender sipSender;

    private static Map<String, IMessageHandler> messageHandlerMap = new ConcurrentHashMap<>();

    public void addHandler(String name, IMessageHandler handler) {
        messageHandlerMap.put(name, handler);
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        String method = "MESSAGE";
        sipListener.addRequestProcessor(method, this);
    }

    @Override
    public void processMsg(RequestEvent evt) {
        SIPRequest sipRequest = (SIPRequest)evt.getRequest();
        String deviceId = SipUtil.getUserIdFromFromHeader(sipRequest);
        // 查询设备是否存在
        SipDevice device = sipDeviceService.selectSipDeviceBySipId(deviceId);
        try {
            if (device == null ) {
                // 不存在则回复404
                responseAck(evt, Response.NOT_FOUND, "device "+ deviceId +" not found");
                log.warn("[设备未找到 ]： {}", deviceId);
            } else {
                Element rootElement = null;
                try {
                    rootElement = getRootElement(evt);
                    if (rootElement == null) {
                        log.error("处理MESSAGE请求  未获取到消息体{}", evt.getRequest());
                        responseAck(evt, Response.BAD_REQUEST, "content is null");
                        return;
                    }
                } catch (DocumentException e) {
                    log.warn("解析XML消息内容异常", e);
                    // 不存在则回复404
                    responseAck(evt, Response.BAD_REQUEST, e.getMessage());
                }
                assert rootElement != null;
                String name = rootElement.getName();
                //log.debug("接收到消息：" + evt.getRequest());
                IMessageHandler messageHandler = messageHandlerMap.get(name);
                if ( messageHandler != null) {
                    messageHandler.handlerCmdType(evt, device, rootElement);
                } else {
                    // 不支持的message
                    // 不存在则回复415
                    responseAck(evt, Response.UNSUPPORTED_MEDIA_TYPE, "Unsupported message type, must Control/Notify/Query/Response");
                }
            }
        } catch (SipException e) {
            log.warn("SIP 回复错误", e);
        } catch (InvalidArgumentException e) {
            log.warn("参数无效", e);
        } catch (ParseException e) {
            log.warn("SIP回复时解析异常", e);
        }
    }
}
