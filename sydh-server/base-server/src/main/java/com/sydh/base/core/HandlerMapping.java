package com.sydh.base.core;

import com.sydh.base.core.hanler.BaseHandler;

/**
 * 消息处理接口
 * @author bill
 */
public interface HandlerMapping {

    BaseHandler getHandler(int messageId);
}
