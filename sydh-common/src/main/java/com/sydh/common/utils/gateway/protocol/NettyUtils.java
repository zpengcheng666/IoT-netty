package com.sydh.common.utils.gateway.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;


public class NettyUtils {
    public static byte[] readBytesFromByteBuf(ByteBuf buf) {
        return ByteBufUtil.getBytes(buf);
    }
}
