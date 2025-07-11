package com.sydh.base.codec;


import com.sydh.common.extend.core.domin.mq.DeviceReport;
import io.netty.buffer.ByteBuf;

/**
 * 基础消息解码类
 *
 * @author bill
 */
public interface MessageDecoder {

    /**
     * TCP3.进站消息解码方法
     */
    DeviceReport decode(ByteBuf buf, String clientId);
}
