package com.sydh.bootstrap.tcp.config;

import com.sydh.base.core.HandlerInterceptor;
import com.sydh.base.session.Session;
import com.sydh.common.extend.core.protocol.Message;
import com.sydh.modbus.pak.TcpDtu;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息拦截应答
 * @author bill
 */
@Slf4j
public class TcpHandlerInterceptor implements HandlerInterceptor<Message> {
    @Override
    public Message notSupported(Message request, Session session) {
        return null;
    }

    @Override
    public boolean beforeHandle(Message request, Session session) {
        int messageId = Integer.parseInt(request.getMessageId());
        if (messageId == TcpDtu.注册报文 || messageId == TcpDtu.心跳包 || messageId == TcpDtu.整包消息){
            return true;
        }
        if (!session.isRegistered()){
            log.warn("设备未注册,session={}",session);
            return true;
        }
        return false;
    }

    @Override
    public Message successful(Message request, Session session) {
        return null;
    }

    /**
     * 调用之后
     */
    @Override
    public void afterHandle(Message request, Message response, Session session) {
        if (response != null){
            //response.setSerialNo(session.nextSerialNO());
        }
    }

    @Override
    public Message exceptional(Message request, Session session, Exception e) {
        return null;
    }
}
