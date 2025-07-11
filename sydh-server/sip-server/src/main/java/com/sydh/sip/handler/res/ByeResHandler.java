package com.sydh.sip.handler.res;

import com.sydh.sip.handler.IResHandler;
import com.sydh.sip.server.IGBListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.ResponseEvent;
import java.text.ParseException;

@Slf4j
@Component
public class ByeResHandler implements InitializingBean,IResHandler {
    @Autowired
    private IGBListener sipListener;

    @Override
    public void processMsg(ResponseEvent evt) throws ParseException {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String method = "BYE";
        sipListener.addResponseProcessor(method,this);
    }
}
