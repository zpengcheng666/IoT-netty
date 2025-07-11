package com.sydh.sip.handler.req.message.response.cmdType;

import com.sydh.sip.domain.SipDevice;
import com.sydh.sip.handler.req.ReqAbstractHandler;
import com.sydh.sip.handler.req.message.IMessageHandler;
import com.sydh.sip.handler.req.message.response.ResponseMessageHandler;
import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import java.text.ParseException;

@Component
public class DeviceConfigHandler extends ReqAbstractHandler implements InitializingBean, IMessageHandler {

    @Autowired
    private ResponseMessageHandler responseMessageHandler;
    @Override
    public void handlerCmdType(RequestEvent evt, SipDevice device, Element element) {
        try {
            // 回复200 OK
            responseAck(evt);
        } catch (ParseException | SipException | InvalidArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String cmdType = "DeviceConfig";
        responseMessageHandler.addHandler(cmdType, this);
    }
}
