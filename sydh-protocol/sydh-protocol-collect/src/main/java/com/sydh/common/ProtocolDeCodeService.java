package com.sydh.common;

import com.sydh.common.exception.ServiceException;
import com.sydh.common.extend.core.domin.mq.message.DeviceData;
import com.sydh.common.extend.core.response.DeCodeBo;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.gateway.CRC16Utils;
import com.sydh.modbus.codec.ModbusDecoder;
import com.sydh.modbus.codec.ModbusEncoder;
import com.sydh.modbus.model.ModbusRtu;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * 协议编解码
 *
 * @author gsb
 * @date 2023/4/8 15:50
 */
@Component
@Slf4j
public class ProtocolDeCodeService {

    private static ModbusDecoder decoder = new ModbusDecoder("com.sydh.modbus");
    private static ModbusEncoder encoder = new ModbusEncoder("com.sydh.modbus");

    public String protocolDeCode(DeCodeBo bo) {
        if (null == bo) {
            throw new ServiceException(MessageUtils.message("protocol.input.content.is.empty"));
        }
        String payload = bo.getPayload();
        /*1-解析 2-读指令 3-写指令 4-CRC生成 5-CRC校验*/
        switch (bo.getType()) {
            case 1:
                ByteBuf buf = Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(payload));
                DeviceData data = DeviceData.builder()
                        .buf(buf)
                        .build();
                ModbusRtu message = decoder.decode(data);
                ReferenceCountUtil.release(buf);
                String[] split = message.toString().split("Modbus");
                return split[0].replace(";", "<br/>");
            case 2:
            case 3:
                ModbusRtu rtu = new ModbusRtu();
                BeanUtils.copyProperties(bo,rtu);
                ByteBuf in = encoder.encode(rtu);
                byte[] bytes = ByteBufUtil.getBytes(in);
                byte[] result = CRC16Utils.CRC(bytes);
                String hexDump = ByteBufUtil.hexDump(result);
                ReferenceCountUtil.release(in);
                return hexDump;
            case 4:
                byte[] crc16Byte = ByteBufUtil.decodeHexDump(payload);
                String crc = CRC16Utils.getCRC(crc16Byte);
                return payload + crc;
            case 5:
                byte[] crcByte = ByteBufUtil.decodeHexDump(payload);
                byte[] checksCRC = {crcByte[crcByte.length -2],crcByte[crcByte.length-1]};
                byte[] sourceCRC = ArrayUtils.subarray(crcByte, 0, crcByte.length - 2);
                String crc1 = CRC16Utils.getCRC(sourceCRC);
                String check = ByteBufUtil.hexDump(checksCRC);
                if (!crc1.equalsIgnoreCase(check)){
                    return "原报文CRC:" + check +"校验失败,CRC值应为:" + crc1 +
                            "<br/>完整报文:" + ByteBufUtil.hexDump(sourceCRC) +crc1;
                }else {
                    return "校验通过！";
                }
        }
        return null;
    }


}
