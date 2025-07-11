package com.sydh.modbus.codec;

import com.sydh.common.exception.ServiceException;
import com.sydh.common.extend.core.domin.mq.message.DeviceData;
import com.sydh.common.extend.core.protocol.modbus.ModbusCode;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.gateway.CRC16Utils;
import com.sydh.modbus.model.ModbusRtu;
import com.sydh.modbus.pak.TcpDtu;
import com.sydh.protocol.WModelManager;
import com.sydh.protocol.base.model.ActiveModel;
import com.sydh.protocol.util.ArrayMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * modbus-rtu协议解码器
 *
 * @author bill
 */
@Slf4j
@Component
@NoArgsConstructor
public class ModbusDecoder {

    @Resource
    private WModelManager modelManager;
    private ArrayMap<ActiveModel> headerSchemaMap;

    public ModbusDecoder(String... basePackages) {
        this.modelManager = new WModelManager(basePackages);
        this.headerSchemaMap = this.modelManager.getActiveMap(ModbusRtu.class);
    }

    public ModbusRtu decode(DeviceData deviceData) {
        try {
            this.build();
            ByteBuf in = deviceData.getBuf();
            int start = in.getUnsignedByte(0);
            int messageId = in.getUnsignedByte(1);
            int currentMessageId = messageId;
            if (!(start == TcpDtu.起始位 && (messageId == TcpDtu.注册报文 || messageId == TcpDtu.心跳包))) {
                //03解析
                currentMessageId = 0;
                if (deviceData.getCode() == ModbusCode.Write06 || messageId == ModbusCode.Write06.getCode() ||
                    deviceData.getCode() == ModbusCode.Write05 || messageId == ModbusCode.Write05.getCode()) {
                    //设备回复06解析
                    currentMessageId = 2;
                } else if (deviceData.getCode() == ModbusCode.Read01 || messageId == ModbusCode.Read01.getCode() ||
                        deviceData.getCode() == ModbusCode.Read02 || messageId == ModbusCode.Read02.getCode()) {
                    // 01、02解析
                    currentMessageId = 4;
                }
               in = verify(in);
            }
            messageId = currentMessageId;
            ActiveModel<ModbusRtu> activeModel = headerSchemaMap.get(messageId);
            ModbusRtu message = new ModbusRtu();
            message.setPayload(in);
            activeModel.mergeFrom(in, message, null);
            log.info("=>解析:[{}]", message);
            return message;
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    private ByteBuf verify(ByteBuf in) {
        ByteBuf copy = in.duplicate();
        byte[] source = new byte[in.writerIndex()];
        copy.readBytes(source);
        byte[] checkBytes = {source[source.length - 2], source[source.length - 1]};
        byte[] sourceCheck = ArrayUtils.subarray(source, 0, source.length - 2);
        String crc = CRC16Utils.getCRC(sourceCheck);
        if (!crc.equalsIgnoreCase(ByteBufUtil.hexDump(checkBytes))) {
            log.warn("=>CRC校验异常,原始报文：{},报文={}", source, ByteBufUtil.hexDump(source));
            throw new ServiceException(MessageUtils.message("modbus.crc.check.abnormal"));
        }
        return Unpooled.wrappedBuffer(sourceCheck);
    }

    private void build() {
        if (this.headerSchemaMap == null) {
            this.headerSchemaMap = this.modelManager.getActiveMap(ModbusRtu.class);
        }
    }


}
