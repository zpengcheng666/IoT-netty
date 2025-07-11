package com.sydh.flowdev.test;

import com.sydh.common.extend.core.domin.mq.message.DeviceData;
import com.sydh.flowdev.codec.FlowDevDecoder;
import com.sydh.flowdev.codec.FlowDevEncoder;
import com.sydh.flowdev.model.FlowDev;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

/**
 * @author bill
 */
public class FlowDevCodeTest {

    private static FlowDevDecoder decoder = new FlowDevDecoder("com.sydh");
    private static FlowDevEncoder encoder = new FlowDevEncoder("com.sydh");


    public static void main(String[] args) {

        String flowData = "681B68B33701120008C100000000000000000000022050004341231811215716";
        ByteBuf in = Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(flowData));
        DeviceData data = DeviceData.builder()
                .buf(in).build();
        FlowDev flowDev = decoder.decode(data, null);
        System.out.println(flowDev);

    }
}
