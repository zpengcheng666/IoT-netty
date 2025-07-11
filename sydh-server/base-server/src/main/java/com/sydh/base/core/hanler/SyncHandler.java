package com.sydh.base.core.hanler;

import com.sydh.base.session.Session;
import com.sydh.common.extend.core.protocol.Message;

import java.lang.reflect.Method;

/**
 * 同步处理报文
 * @author bill
 */
public class SyncHandler extends BaseHandler{

    public SyncHandler(Object target, Method targetMethod, String desc,boolean async) {
        super(target, targetMethod, desc, async);
    }

    @Override
    public <T extends Message> T invoke(T request, Session session) throws Exception {
        return super.invoke(request, session);
    }
}
